package com.family.donghyunlee.family.bucket;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.MyBucketList;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.data.WishListRecyclerItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-08-11.
 */

public class Bucket extends AppCompatActivity{


    @BindView(R.id.bucket_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_wishlist)
    RecyclerView recyclerView;
    @BindView(R.id.bucket_avi)
    AVLoadingIndicatorView avLoadingIndicatorView;
    @BindView(R.id.progress)
    ImageButton progress;

    private static final String TAG = Bucket.class.getSimpleName();
    private static final int GETDATAFROMSERVER= 0;
    private static final int SETDATATOSERVER= 1;
    private ArrayList<WishListRecyclerItem> items;
    private LinearLayoutManager linearLayoutManager;
    private WishListRecyclerAdapter recyclerAdapter;
    private FirebaseDatabase database;
    private SharedPreferences pref;
    private String groupId;
    private List<String> answerList;
    private List<String> questionList;

    @OnClick(R.id.progress)
    void onProgressClick(){
        Intent intent = new Intent(Bucket.this, Progress.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("버킷 공간");

        items = new ArrayList<>();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        database = FirebaseDatabase.getInstance();

        new AccessDatabaseTask().execute(GETDATAFROMSERVER);

        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerAdapter = new WishListRecyclerAdapter(getBaseContext(), items, R.layout.fragment_wishlist, groupId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                //onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class AccessDatabaseTask extends AsyncTask<Integer, String, Long> {

        public Long result = null;
        private DatabaseReference groupReference;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;
        private MyBucketList myBucketList;
        private WishListRecyclerItem item;
        private FirebaseStorage storage;
        private StorageReference storageRef;
        private StorageReference pathRef;
        private ArrayList<StorageReference> storageItems;
        private String storageProfileFolder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            groupReference = database.getReference().child("groups").child(groupId).child("members");
            items = new ArrayList<>();
            storage = FirebaseStorage.getInstance();
            storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);
            storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
            storageItems = new ArrayList<>();
            avLoadingIndicatorView.show();
        }

        @Override
        protected Long doInBackground(Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.
            if (params[0].intValue() == GETDATAFROMSERVER) {
                groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while(child.hasNext()){
                            User user = child.next().getValue(User.class);
                            if(!user.getId().equals(currentUser.getUid())){

                                myBucketList = child.next().child("myBucketAnswer").getValue(MyBucketList.class);
                                questionList = myBucketList.getQuestion();
                                answerList = myBucketList.getAnswer();
                                //int imgProfilePath, String nickName, String date, String question, String answer
                                for(int i = 0 ; i < questionList.size() ; i++){
                                    item = new WishListRecyclerItem(user.getUserImage(), user.getUserNicname(), myBucketList.getDate(),
                                            questionList.get(i), answerList.get(i));
                                    pathRef = storageRef.child(storageProfileFolder + "/" + user.getUserImage());
                                    storageItems.add(pathRef);
                                    items.add(item);
                                }
                                // 이미지 스토리지 접근
                            }
                        }
                        recyclerAdapter.setStorageitem(storageItems);
                        recyclerAdapter.notifyDataSetChanged();
                        return;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return result;
            }
//            else if (params[0].intValue() == SETDATAINTOSERVER)   {
//                List<String> answer = new ArrayList<>();
//                answerData = recyclerAdapter.getAnswer();
//                for(int i = 0 ; i < answerData.length ; i++){
//                    answer.add(answerData[i]);
//                }
//                userReference.setValue(answer);
//                return result;
//            }

            if (this.isCancelled()) {
                return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


        }
        @Override
        protected void onPostExecute(Long s) {
            super.onPostExecute(s);
            Log.i(TAG, ">>>>>> " + s + " <<");

            avLoadingIndicatorView.hide();
            return;
        }
        @Override
        protected void onCancelled() {
            Log.i("TAG", ">>>>> doItBackground 취소");
            super.onCancelled();
        }
    }
}
