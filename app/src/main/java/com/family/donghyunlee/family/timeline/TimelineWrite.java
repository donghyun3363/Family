package com.family.donghyunlee.family.timeline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.PhotoSel;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.IsCheck;
import com.family.donghyunlee.family.data.PushItem;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.TimelineCountItem;
import com.family.donghyunlee.family.data.User;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-08-16.
 */

public class TimelineWrite extends AppCompatActivity {

    @BindView(R.id.timeline_write_done)
    Button timelineWriteDone;
    @BindView(R.id.timeline_write_content)
    EditText timelineWriteContent;
    @BindView(R.id.timeline_write_image)
    ImageView timelineWriteImage;
    @BindView(R.id.timeline_write_img_click)
    ImageButton timelineWriteImgClick;
    @BindView(R.id.timeline_write_delete_image)
    ImageButton imgaeDelete;
    @BindView(R.id.timeline_write_image_container)
    FrameLayout imageContainer;

    private static final int GET_TIMELINE_WRITE_REQUESTCODE = 0;
    private static final String TAG = TimelineWrite.class.getSimpleName();
    private Uri uri;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private DatabaseReference userReference;
    private DatabaseReference pushReference;
    private DatabaseReference memberReference;
    private String groupId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private int flag = 0;
    private int switchFlag = 0; // image가 있을 떄 없을 때 스위치 .. 0이 꺼짐 .. 1이 켜짐
    private User userItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelinewrite);
        ButterKnife.bind(this);

        setInit();
        settingListener();
    }

    // 카메라부터 사진을 가지고옴.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GET_TIMELINE_WRITE_REQUESTCODE: {
                if (resultCode == RESULT_OK) {
                    timelineWriteImage.setVisibility(timelineWriteImage.VISIBLE);
                    imgaeDelete.setVisibility(timelineWriteImgClick.VISIBLE);
                    switchFlag =1;
                    uri = data.getParcelableExtra("IMAGE_URI");

                    Log.i(TAG,">>>>>>>>>>>>>     uri" + uri);
                    Glide.with(getApplicationContext()).load(uri).centerCrop().crossFade().into(timelineWriteImage);


                    // 파이어베이스 데이터베이스에 추가 및 저장소 올리기.
                    //oneItemADD(INALBUM);
                } else {
                    Log.e(TAG, ">>>>> " + "GET_INALBUM_PHOTOSEL_REQUESTCODE don't result ");
                }
                break;
            }
            default:
                break;
        }
    }
    @OnClick(R.id.timeline_Write_back)
    void onBackClick() {
        finish();
    }
    @OnClick(R.id.timeline_write_img_click)
    void onImgClick() {
        Intent intent = new Intent(TimelineWrite.this, PhotoSel.class);
        startActivityForResult(intent, GET_TIMELINE_WRITE_REQUESTCODE);
    }
    @OnClick(R.id.timeline_write_delete_image)
    void onDeleteClick() {
        timelineWriteImage.setImageResource(0);
        timelineWriteImage.setVisibility(timelineWriteImage.GONE);
        imgaeDelete.setVisibility(timelineWriteImgClick.GONE);
        switchFlag = 0;
    }

    @OnClick(R.id.timeline_write_done)
    void onDoneClick() {

        if (validateForm() == false)
            return;

        //server로 저장 후 뒤 액티비티에가서 item 추가.
        if(switchFlag == 0){
            Log.i(TAG, "!!!!!!!1" + timelineWriteImage.getResources());
            databaseAccess(null);
        } else if(switchFlag == 1){
            Log.i(TAG, "!!!!!!!2" + timelineWriteImage.getResources());
            fileUpload(uri);
        }
        finish();
    }

    private void pushDatabasehAccess(final User userItem){

        pushReference = database.getReference().child("groups").child(groupId).child("push");
        memberReference = database.getReference().child("groups").child(groupId).child("members");
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        final SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

        memberReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    User curUser = child.next().getValue(User.class);
                    if(!curUser.getId().equals(currentUser.getUid())){
                        String data = userItem.getUserNicname() + "님이" + " 타임라인에 게시물을 업로드하였습니다.";
                        String key;
                        PushItem item = new PushItem(userItem.getUserImage(), data ,CurDateFormat.format(date));
                        key = pushReference.child(curUser.getId()).push().getKey();
                        pushReference.child(curUser.getId()).child(key).setValue(item);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
        }
        Intent intent = getIntent();
        flag = intent.getExtras().getInt("FLAG");

        if(flag == 1){
            intent = new Intent(TimelineWrite.this, PhotoSel.class);
            startActivityForResult(intent, GET_TIMELINE_WRITE_REQUESTCODE);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
    }


    private void databaseAccess(final String filename){


        groupReference = database.getReference("groups");
        userReference = database.getReference("groups").child(groupId).child("members").child(currentUser.getUid());

        Log.i(TAG, "groupid: " + groupId + "/ userId: " + currentUser.getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key;
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
                TimeLineItem item;
                userItem = dataSnapshot.getValue(User.class);
                pushDatabasehAccess(userItem);
                groupReference = groupReference.child(groupId)
                        .child("timelineCard");
                key = groupReference.push().getKey();
                TimelineCountItem timelineCountItem = new TimelineCountItem(0, 0);
                if(filename == null) {
                    item = new TimeLineItem(timelineCountItem, key, userItem.getUserNicname(), userItem.getUserImage(), CurDateFormat.format(date),
                            timelineWriteContent.getText().toString(), userItem.getId(), "empty");
                } else{
                    item = new TimeLineItem(timelineCountItem, key, userItem.getUserNicname(), userItem.getUserImage(), CurDateFormat.format(date),
                            timelineWriteContent.getText().toString(), userItem.getId(), filename);
                }
                groupReference.child(key).setValue(item);

                // like of user each set
                IsCheck ischeck = new IsCheck(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void fileUpload(Uri filePath) {
        final String filename;
        Log.i(TAG, "!!!!!!!4");
        if (filePath != null) {
            // 파일이름 생성.
            Log.i(TAG, "!!!!!!5");
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(getResources().getString(R.string.date_format));
            Date now = new Date();
            filename = formatter.format(now) + getResources().getString(R.string.file_extension);

            StorageReference storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage))
                    .child(getResources().getString(R.string.storage_timeline_folder) + "/" + filename);
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 실시간 데이터베이스에 저장
                    databaseAccess(filename);
                    Toast.makeText(getApplicationContext(), "글과 이미지가 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
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
    private boolean validateForm() {
        boolean valid = true;

        String email = timelineWriteContent.getText().toString();
        if (TextUtils.isEmpty(email)) {
            timelineWriteContent.setError(getResources().getString(R.string.text_error));
            timelineWriteContent.requestFocus();
            valid = false;
        }
        return valid;
    }

    private void settingListener() {
        // EditText Listener Watcher Function (버튼 키 색 변경.)
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timelineWriteContent.length() > 0) {
                    timelineWriteDone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                    timelineWriteDone.setEnabled(true);
                } else {
                    timelineWriteDone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhGray));
                    timelineWriteDone.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        timelineWriteContent.addTextChangedListener(watcher);
    }
}

