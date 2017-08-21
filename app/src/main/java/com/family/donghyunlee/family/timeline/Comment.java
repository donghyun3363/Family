package com.family.donghyunlee.family.timeline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.family.donghyunlee.family.PhotoSel;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.CommentCountItem;
import com.family.donghyunlee.family.data.CommentItem;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.TimelineCountItem;
import com.family.donghyunlee.family.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-08-18.
 */

public class Comment extends AppCompatActivity {

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
        currentItem =  (TimeLineItem)intent.getExtras().getParcelable("TimelineItem");
        commentCnt = (int)intent.getIntExtra("CommentCnt", 0);
        likeCnt = (int)intent.getIntExtra("LikeCnt", 0);
        currentPositon = (int)intent.getIntExtra("position", 0);
        TimelineCountItem timelineCountItem = new TimelineCountItem(likeCnt, commentCnt);
        currentItem.setTimelineCountItem(timelineCountItem);

        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>2121212121 :"+currentItem.getTimeline_contentImage());

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        items = new ArrayList<>();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

    }
    private void getUser(){
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
    void backClick(){
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
    void onDoneClick(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
        CommentCountItem commentCountItem = new CommentCountItem(0);
        commentReference = database.getReference().child("groups").child(groupId).child("commentCard").child(currentItem.getTimeline_key());
        commentKey = commentReference.push().getKey();

        if(isChecked == 0){ // 이미지가 없을 때
            item = new CommentItem(currentItem.getTimeline_key(), commentKey, "empty", currentUserItem.getUserNicname(), CurDateFormat.format(date),
                    commentEdittext.getText().toString(), commentCountItem,currentUserItem.getUserImage());
        }
        else{               // 이미지가 있을 때
            item = new CommentItem(currentItem.getTimeline_key(), commentKey, filePath, currentUserItem.getUserNicname(), CurDateFormat.format(date),
                    commentEdittext.getText().toString(), commentCountItem,currentUserItem.getUserImage());
        }
        commentReference.child(commentKey).setValue(item);
        commentEdittext.setText("");
        changeCommentCount();

        recyclerAdapter.setAddCommentCnt(recyclerAdapter.getAddCommentCnt() + 1);
        recyclerAdapter.notifyItemChanged(0);
        // 디비에 넣기!
    }
    private void changeCommentCount(){
        String key = currentItem.getTimeline_key();
        commentReference = database.getReference().child("groups").child(groupId)
                .child("timelineCard").child(key).child("timelineCountItem");
        commentReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                TimelineCountItem timelineCountItem = mutableData.getValue(TimelineCountItem.class);
                if(timelineCountItem == null){
                    Log.i(TAG, ">>>>>>     her");
                    ++commentCnt;
                    return Transaction.success(mutableData);
                } else{
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
    void onImageClick(){
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
                    Log.i(TAG, ">>>>>>>>      9999999999" + uri);
                    filePath = uri.getPath();
                    isChecked=1;
                    startFragment(ImageViewFragment.newInstance(filePath));
                } else {
                Log.e(TAG, ">>>>> " + "GET_Comment_Image_REQUESTCODE don't result ");
            }
                break;
            }
            default:
                break;
        }
    }

    public void setIsChecked(int isChecked){
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
        recyclerAdapter = new CommentRecyclerAdapter(this, items, groupId, currentItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }
}
