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


        Intent inetnt = getIntent();
        currentItem =  (TimeLineItem)inetnt.getExtras().getParcelable("TimelineItem");
        commentCnt = (int)inetnt.getIntExtra("CommentCnt", 0);
        likeCnt = (int)inetnt.getIntExtra("LikeCnt", 0);
        TimelineCountItem timelineCountItem = new TimelineCountItem(likeCnt, commentCnt);
        Log.i(TAG, ">>>>>>>>>444" + currentItem.getTimeline_key());
        Log.i(TAG, ">>>>>>>>>incommentCnt" + commentCnt);
        Log.i(TAG, ">>>>>>>>>incLikeCnt" + likeCnt);
        currentItem.setTimelineCountItem(timelineCountItem);
        Log.i(TAG,">>>>>>>>> 551111 //  " + currentItem.getTimelineCountItem().getLikeCnt());

        Log.i(TAG,">>>>>>>>> 5511///  " + currentItem.getTimelineCountItem().getCommentCnt());


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
                currentUserItem = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @OnClick(R.id.back_comment)
    void backClick(){
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);    // 기존, 현재 순
    }

    @OnClick(R.id.comment_done)
    void onDoneClick(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
        CommentCountItem commentCountItem = new CommentCountItem(0);
        commentReference = database.getReference().child("groups").child(groupId).child("commentCard");
        commentKey = commentReference.push().getKey();

        //public CommentItem(String commentKey, String commentNickName, String commentDate,
        // String commentContent, String commentLikeCnt, String commentProfileImage) {
        if(isChecked == 0){ // 이미지가 없을 때
            item = new CommentItem(commentKey, "empty", currentUserItem.getUserNicname(), CurDateFormat.format(date),
                    commentEdittext.getText().toString(), commentCountItem,currentUserItem.getUserImage());
        }
        else{               // 이미지가 있을 때
            item = new CommentItem(commentKey, filePath, currentUserItem.getUserNicname(), CurDateFormat.format(date),
                    commentEdittext.getText().toString(), commentCountItem,currentUserItem.getUserImage());
        }
        commentReference.child(commentKey).setValue(item);
        // 디비에 넣기!
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
