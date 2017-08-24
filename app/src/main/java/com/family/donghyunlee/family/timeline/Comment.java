package com.family.donghyunlee.family.timeline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.family.donghyunlee.family.PhotoSel;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.CommentCountItem;
import com.family.donghyunlee.family.data.CommentItem;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.TimelineCountItem;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.dialog.CommentCardDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-08-18.
 */

public class Comment extends AppCompatActivity implements CommentCardDialogFragment.OnFragmentListener, ImageViewFragment.OnImageViewFragmentListener {

    private static final String TAG = Comment.class.getSimpleName();
    @BindView(R.id.rv_comment)
    RecyclerView recyclerView;

    @BindView(R.id.comment_edittext)
    EditText commentEdittext;
    @BindView(R.id.comment_done)
    Button commentDone;


    private CommentRecyclerAdapter recyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<CommentItem> items;
    private SharedPreferences pref;
    private String groupId;
    private TimeLineItem currentItem;
    private static final int GET_Comment_Image_REQUESTCODE = 101;
    private static int isChecked = 0; // 현재 이미지뷰 프래그먼트가 없을 때

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference comment_pathRef;
    private String tsorageCommentFolder;

    private FirebaseDatabase database;
    private DatabaseReference commentReference;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CommentItem item;
    private int likeCnt;
    private int commentCnt;
    private User currentUserItem;
    int currentPositon;
    private Uri uri;
    private String filePath;
    private String commentKey;
    private ImageViewFragment fragment;
    private Uri getUriToFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        setInit();
        getUser();
        settingRecycler();
        settingListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerAdapter.cleanupListener();
    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경

            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_b));
        }

        Intent intent = getIntent();
        currentItem = (TimeLineItem) intent.getExtras().getParcelable("TimelineItem");
        commentCnt = (int) intent.getIntExtra("CommentCnt", 0);
        likeCnt = (int) intent.getIntExtra("LikeCnt", 0);
        currentPositon = (int) intent.getIntExtra("position", 0);
        TimelineCountItem timelineCountItem = new TimelineCountItem(likeCnt, commentCnt);
        currentItem.setTimelineCountItem(timelineCountItem);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        items = new ArrayList<>();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        storage = FirebaseStorage.getInstance();
        tsorageCommentFolder = getResources().getString(R.string.storage_comment_folder);
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));

    }



    private void getUser() {
        userReference = database.getReference().child("users").child(currentUser.getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO USER를 가져오기전에 OnClick했을 때 문제가 생긴다.. 이게 수행될 때까지 다이얼로그 프로그래스바? 해주는게 맞냐?
                currentUserItem = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.back_comment)
    void backClick() {
        // 마지막 타임라인 아이템을 저장 ( because of 커맨트 수 )
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("recent_timeline_key", currentItem.getTimeline_key());
        editor.putInt("recent_timeline_position", currentPositon);
        editor.commit();

        Intent sendIntent = new Intent(this, TimeLine.class);
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);    // 기존, 현재 순
    }

    @OnClick(R.id.comment_done)
    void onDoneClick() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(getResources().getString(R.string.date_format));

        CommentCountItem commentCountItem = new CommentCountItem(0);
        commentReference = database.getReference().child("groups").child(groupId).child("commentCard").child(currentItem.getTimeline_key());
        commentKey = commentReference.push().getKey();
        Log.i(TAG, ">>>>>>>                                  HERE");

        if (isChecked == 0) { // 이미지가 없을 때
            Log.i(TAG, ">>>>>>>                                  HER2");
            item = new CommentItem(currentItem.getTimeline_key(), commentKey, "empty", currentUserItem.getUserNicname(), CurDateFormat.format(date),
                    commentEdittext.getText().toString(), commentCountItem, currentUserItem.getUserImage());
            commentReference.child(commentKey).setValue(item);
            commentEdittext.setText("");
            Log.i(TAG, ">>>>>>>                                  HERE3");
            changeCommentIncreCount();
            Log.i(TAG, ">>>>>>>                                  HERE4");
            recyclerAdapter.setAddCommentCnt(recyclerAdapter.getAddCommentCnt() + 1);
            recyclerAdapter.setIsAdd(true); // 추가되는 아이템이라는 것을 알려주는 Signal
            Log.i(TAG, ">>>>>>>                                  HERE5");
        } else {               // 이미지가 있을 때

            isChecked = 0; // 0은 프래그먼트가 죽을 떄
            String filename = formatter.format(date) + getResources().getString(R.string.file_extension);
            item = new CommentItem(currentItem.getTimeline_key(), commentKey
                    , filename, currentUserItem.getUserNicname(), CurDateFormat.format(date),
                    commentEdittext.getText().toString(), commentCountItem, currentUserItem.getUserImage());
            storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage))
                    .child(getResources().getString(R.string.storage_comment_folder) + "/" + filename);
            storageRef.putFile(getUriToFragment).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    commentReference.child(commentKey).setValue(item);
                    commentEdittext.setText("");
                    changeCommentIncreCount();
                    recyclerAdapter.setAddCommentCnt(recyclerAdapter.getAddCommentCnt() + 1);
                    recyclerAdapter.setIsAdd(true); // 추가되는 아이템이라는 것을 알려주는 Signal
                    Toast.makeText(getApplicationContext(), "글과 이미지가 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(fragment).commit();
                    fragmentManager.popBackStack();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplication(), getResources().getString(R.string.uploading_failed), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }



    }

    private void changeCommentIncreCount() {
        String key = currentItem.getTimeline_key();
        commentReference = database.getReference().child("groups").child(groupId)
                .child("timelineCard").child(key).child("timelineCountItem");
        commentReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                TimelineCountItem timelineCountItem = mutableData.getValue(TimelineCountItem.class);
                if (timelineCountItem == null) {
                    ++commentCnt;
                    return Transaction.success(mutableData);
                } else {
                    int cnt = timelineCountItem.getCommentCnt();
                    timelineCountItem.setCommentCnt(++cnt);
                }
                mutableData.setValue(timelineCountItem);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

    }


    @OnClick(R.id.comment_image_get)
    void onImageClick() {
        Intent intent = new Intent(Comment.this, PhotoSel.class);
        startActivityForResult(intent, GET_Comment_Image_REQUESTCODE);
    }

    // 카메라부터 사진을 가지고옴.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GET_Comment_Image_REQUESTCODE: {
                if (resultCode == RESULT_OK) {
                    uri = data.getParcelableExtra("IMAGE_URI");
                    filePath = getRealPathFromUri(this, uri);
                    isChecked = 1;
                    fragment = ImageViewFragment.newInstance(filePath);
                    startFragment(fragment);
                } else {
                    Log.e(TAG, ">>>>> " + "GET_Comment_Image_REQUESTCODE don't result ");
                }
                break;
            }
            default:
                break;
        }
    }

    // 가상경로를 절대경로로 바꿈
    private String getRealPathFromUri(Context context, Uri selectedUri) {
        String[] columns = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE };

        Cursor cursor = context.getContentResolver().query(selectedUri, columns, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int pathColumnIndex     = cursor.getColumnIndex( columns[0] );
            int mimeTypeColumnIndex = cursor.getColumnIndex( columns[1] );

            String contentPath = cursor.getString(pathColumnIndex);
            String mimeType    = cursor.getString(mimeTypeColumnIndex);
            cursor.close();

            if(mimeType.startsWith("image")) {
                // image
            }
            else if(mimeType.startsWith("video")) {
                // video
            }

            return contentPath;
        }
        return selectedUri.getPath();
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    private void startFragment(final ImageViewFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.imageview_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void settingListener() {
        // EditText Listener Watcher Function (버튼 키 색 변경.)
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(commentEdittext.getText().toString())) {
                    commentDone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                    commentDone.setEnabled(true);
                } else {
                    commentDone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhGray));
                    commentDone.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        commentEdittext.addTextChangedListener(watcher);
    }


    private void settingRecycler() {


        linearLayoutManager = new LinearLayoutManager(this);
        // 리사이클러뷰 setting

        FragmentManager fragmentManager = getSupportFragmentManager();
        recyclerAdapter = new CommentRecyclerAdapter(this, items, groupId, currentItem, recyclerView, fragmentManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }

    @Override
    public void onReceivedData(int cnt) {
        // Fragment 내에서 Activity 호출하는 lister로 cnt가 필요없다.. add 카운트를 -1해주면됨! 매개변수 무시!
        recyclerAdapter.setAddCommentCnt(recyclerAdapter.getAddCommentCnt() - 1);
        recyclerAdapter.notifyItemChanged(0);
    }

    @Override
    public void onReceivedData(Uri uri) {
        getUriToFragment = uri;
    }
}
