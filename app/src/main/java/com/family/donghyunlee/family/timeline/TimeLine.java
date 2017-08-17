package com.family.donghyunlee.family.timeline;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.PhotoSel;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.bucket.Bucket;
import com.family.donghyunlee.family.data.MyBucketList;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.TitleItem;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.photoalbum.PhotoAlbum;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-31.
 */

public class TimeLine extends AppCompatActivity {

    private static final String TAG = TimeLine.class.getSimpleName();
    private static final Integer GET_BUCKITANSWER_TO_SERVER = 0;
    private static final int GET_PROFILE_TO_SERVER = 1;
    private static final int TIMELINE_TITLE_REQUESTCODE = 101;

    private boolean isFristTime;
    private SharedPreferences pref;
    private String groupId;

    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private DatabaseReference userReference;
    private DatabaseReference titleReference;
    private ArrayList<MyBucketList> elseItems;
    private MyBucketList myItem;
    private MyBucketList tempItem;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private LinearLayoutManager linearLayoutManager;
    private TimelineRecyclerAdapter recyclerAdapter;
    private ArrayList<TimeLineItem> timeline_items;
    private ArrayList<User> profile_items;
    private ValueEventListener mValueEventListener;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String storageTitleFolder;
    private StorageReference titlepathRef;


    private EditText inputTitle;
    //  private IOverScrollDecor mVertOverScrollEffect;
    @BindView(R.id.bar_photo)
    ImageButton barPhoto;
    @BindView(R.id.bar_calender)
    ImageButton barCalender;
    @BindView(R.id.bar_push)
    ImageButton barPush;
    @BindView(R.id.bar_setting)
    ImageButton barSetting;
    @BindView(R.id.timeline_title)
    TextView timelineTitle;
    @BindView(R.id.timeline_title_image)
    ImageView timelineTitleImage;
    @BindView(R.id.timeline_title_edit)
    ImageButton titleEdit;

    @BindView(R.id.rv_timeline)
    RecyclerView recyclerView;
    private Bitmap photo;
    private Uri filePath;

    @Override
    protected void onStart() {
        super.onStart();
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TitleItem titleItem = dataSnapshot.getValue(TitleItem.class);
                if (titleItem.getTitle().equals("empty") && titleItem.getTitleImage().equals("empty")) {
                    timelineTitle.setText("We are Fam!");
                    timelineTitleImage.setImageResource(0);
                } else if (!titleItem.getTitleImage().equals("empty") && titleItem.getTitleImage().equals("empty")) {
                    timelineTitle.setText(titleItem.getTitle());
                    timelineTitleImage.setImageResource(0);
                } else if (titleItem.getTitleImage().equals("empty") && !titleItem.getTitleImage().equals("empty")) {
                    timelineTitle.setText("We are Fam!");
                    titleImageDownload(titleItem.getTitleImage());
                } else {
                    timelineTitle.setText(titleItem.getTitle());
                    titleImageDownload(titleItem.getTitleImage());
                }


                Toast.makeText(TimeLine.this, " 타이틀 리스너 Test ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(TimeLine.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        titleReference.addValueEventListener(mValueEventListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // timeline child listener clean in rv adapter
        //recyclerAdapter.cleanupListener();
        // timeline title listener clean
        Toast.makeText(this, "여기여기", Toast.LENGTH_SHORT).show();
        if (mValueEventListener != null) {
            titleReference.removeEventListener(mValueEventListener);
        }

    }
    @OnClick(R.id.bar_push)
    void pushClick(){

    }
    @OnClick(R.id.bar_setting)
    void settingClick(){

    }
    @OnClick(R.id.bar_photo)
    void photoClick() {
        Intent intent = new Intent(TimeLine.this, PhotoAlbum.class);
        startActivity(intent);
    }

    @OnClick(R.id.bar_calender)
    void calenderClick() {
        Intent intent = new Intent(TimeLine.this, Bucket.class);
        startActivity(intent);
    }

    @OnClick(R.id.timeline_title_edit)
    void onEditClick() {

        PopupMenu popupMenu = new PopupMenu(this, titleEdit);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.title_menu, menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_edit_title:
                        AlertDialog.Builder dialog = createDialog();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialog_edit_title, (ViewGroup) findViewById(R.id.popup_edit_root));
                        inputTitle = (EditText) layout.findViewById(R.id.dialog_title);
                        dialog.setView(layout);
                        dialog.show();
                        return true;
                    case R.id.menu_edit_titleimage:
                        Intent intent = new Intent(TimeLine.this, PhotoSel.class);
                        startActivityForResult(intent, TIMELINE_TITLE_REQUESTCODE);
                        return true;
                    case R.id.menu_edit_reset:
                        TitleItem titleItem = new TitleItem("empty", "empty");
                        titleReference.setValue(titleItem);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();

    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setInit();
        settingRecycler();

    }
    void titleImageDownload(String filename){
        titlepathRef = storageRef.child(storageTitleFolder + "/" + filename);
        Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(titlepathRef).centerCrop()
                .crossFade().into(timelineTitleImage);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.menu_edit_title:
                AlertDialog.Builder dialog = createDialog();
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.dialog_edit_title, (ViewGroup) findViewById(R.id.popup_edit_root));
                inputTitle = (EditText) layout.findViewById(R.id.dialog_title);
                dialog.setView(layout);
                dialog.show();
                return true;
            case R.id.menu_edit_titleimage:
                Intent intent = new Intent(TimeLine.this, PhotoSel.class);
                startActivityForResult(intent, TIMELINE_TITLE_REQUESTCODE);
                return true;
            case R.id.menu_edit_reset:
                titleReference.child("titleImage").setValue("empty");
                titleReference.child("title").setValue("empty");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case TIMELINE_TITLE_REQUESTCODE: {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    byte[] byteArray = bundle.getByteArray("PHOTO");
                    photo = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    filePath = Uri.parse(bundle.getString("IMAGE_URI"));
                    fileUpload(filePath);

                } else {
                    Log.e(TAG, ">>>>> " + "TIMELINE_TITLE_REQUESTCODE don't result ");
                }
                break;
            }
            default:
                break;
        }
    }

    private void fileUpload(Uri filePath) {
        final String filename;
        if (filePath != null) {
            // 파일이름 생성.
            Log.i(TAG, "!!!!!!5");
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(getResources().getString(R.string.date_format));
            Date now = new Date();
            filename = formatter.format(now) + getResources().getString(R.string.file_extension);

            StorageReference storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage))
                    .child(getResources().getString(R.string.storage_timeline_title_folder) + "/" + filename);
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 실시간 데이터베이스에 저장
                    titleReference.child("titleImage").setValue(filename);
                    Toast.makeText(getApplicationContext(), "이미지가 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
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

    private AlertDialog.Builder createDialog() {
        AlertDialog.Builder insertDialog = new android.support.v7.app.AlertDialog.Builder(this);
        insertDialog.setTitle("타이틀 명 변경")
                .setMessage("가족 들만의 타이틀 명을 변경해보세요!")
                .setPositiveButton(R.string.dialog_posi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (inputTitle.length() <= 0) {
                            //TODO 다이얼로그 꺼지는 것 처리
                            Toast.makeText(TimeLine.this, "입력이 되지 않았습니다.", Toast.LENGTH_SHORT).show();

                        } else {

                            titleReference.child("titleImage").setValue("empty");
                            titleReference.child("title").setValue(inputTitle.getText().toString());
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_negati, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        return insertDialog;
    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
        }


        timeline_items = new ArrayList<>();
        profile_items = new ArrayList<>();
        elseItems = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        userReference = database.getReference().child("users");
        groupReference = database.getReference().child("groups").child(groupId).child("members");
        titleReference = database.getReference().child("groups").child(groupId).child("title");
        currentUser = mAuth.getCurrentUser();


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
        storageTitleFolder = getResources().getString(R.string.storage_timeline_title_folder);
        // 버킷리스트가 처음인지 체크.
        Intent intent = getIntent();
        isFristTime = (boolean) intent.getSerializableExtra("ISFIRSTTIME?");
        if (isFristTime) {
            // 버킷리스트 공유하고 자기는 공유받고. getBucketAnswer 접근.
            new AccessDatabaseTask().execute(GET_BUCKITANSWER_TO_SERVER);
        }
        new AccessDatabaseTask().execute(GET_PROFILE_TO_SERVER);
    }

    private void settingRecycler() {


        linearLayoutManager = new LinearLayoutManager(this);
        // 리사이클러뷰 setting
        recyclerAdapter = new TimelineRecyclerAdapter(this, timeline_items, profile_items, groupId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        // Overscroll Vertical
//        mVertOverScrollEffect = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerView));


    }

    public class AccessDatabaseTask extends AsyncTask<Integer, String, Long> {

        public Long result = null;


        private ArrayList<StorageReference> storageItems;
        private String storageProfileFolder;
        private DatabaseReference timelineCardReference;
        private DatabaseReference profileReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            storage = FirebaseStorage.getInstance();
            storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);

            timelineCardReference = database.getReference().child("groups").child(groupId).child("timelineCard");
            profileReference = database.getReference().child("groups").child(groupId).child("members");
        }

        @Override
        protected Long doInBackground(Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.
            if (params[0].intValue() == GET_BUCKITANSWER_TO_SERVER) {
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while (child.hasNext()) {
                            User user = child.next().getValue(User.class);
                            if (currentUser.getUid().equals(user.getId()) == true) {
                                myItem = dataSnapshot.child(user.getId()).child("myBucketAnswer").getValue(MyBucketList.class);
                            }
                            if (currentUser.getUid().equals(user.getId()) == false) {
                                tempItem = dataSnapshot.child(user.getId()).child("myBucketAnswer").getValue(MyBucketList.class);
                                elseItems.add(tempItem);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                groupReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while (child.hasNext()) {
                            User user = child.next().getValue(User.class);
                            if (currentUser.getUid().equals(user.getId()) == true) {
                                for (int i = 0; i < elseItems.size(); i++) {
                                    groupReference.child(user.getId()).child("elseBucketAnswer")
                                            .child(elseItems.get(i).getUserId()).setValue(elseItems.get(i));
                                    Log.i(TAG, ">>>>>      tempItem: " + elseItems.get(0).getUserId());
                                }
                                Log.i(TAG, ">>>>>      tempItem: " + elseItems.get(0).getUserId());
                            } else {
                                groupReference.child(user.getId()).child("elseBucketAnswer")
                                        .child(myItem.getUserId()).setValue(myItem);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return result;
            } else if (params[0].intValue() == GET_PROFILE_TO_SERVER) {
                profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while (child.hasNext()) {

                            User item = child.next().getValue(User.class);
                            profile_items.add(item);
                        }
                        recyclerAdapter.setProfileItem(profile_items);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long s) {
            super.onPostExecute(s);

            Log.i(TAG, ">>>>>> " + s + " <<");
            return;
        }

        @Override
        protected void onCancelled() {
            Log.i("TAG", ">>>>> doItBackground 취소");
            super.onCancelled();
        }
    }

}