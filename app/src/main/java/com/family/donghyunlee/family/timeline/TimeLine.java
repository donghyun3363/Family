package com.family.donghyunlee.family.timeline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.bucket.Bucket;
import com.family.donghyunlee.family.data.MyBucketList;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.photoalbum.PhotoAlbum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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

    private boolean isFristTime;
    private SharedPreferences pref;
    private String groupId;

    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private DatabaseReference userReference;
    private ArrayList<MyBucketList> elseItems;
    private MyBucketList myItem;
    private MyBucketList tempItem;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private LinearLayoutManager linearLayoutManager;
    private TimelineRecyclerAdapter recyclerAdapter;
    private ArrayList<TimeLineItem> timeline_items;
    private ArrayList<User> profile_items;
  //  private IOverScrollDecor mVertOverScrollEffect;
    @BindView(R.id.bar_photo)
    ImageButton barPhoto;
    @BindView(R.id.bar_calender)
    ImageButton barCalender;
    @BindView(R.id.bar_push)
    ImageButton barPush;
    @BindView(R.id.bar_setting)
    ImageButton barSetting;

    @BindView(R.id.rv_timeline)
    RecyclerView recyclerView;

    @Override
    protected void onStop() {
        super.onStop();
        // timeline child listener clean
        recyclerAdapter.cleanupListener();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setInit();
        settingRecycler();
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
        currentUser = mAuth.getCurrentUser();
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

        private FirebaseStorage storage;
        private StorageReference storageRef;
        private StorageReference pathRef;
        private ArrayList<StorageReference> storageItems;
        private String storageProfileFolder;
        private DatabaseReference timelineCardReference;
        private DatabaseReference profileReference;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            storage = FirebaseStorage.getInstance();
            storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);
            storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
            timelineCardReference = database.getReference().child("groups").child(groupId).child("timelineCard").child("-KrexnQvzuvew81gXFY4");
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