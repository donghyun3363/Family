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
    @BindView(R.id.progress)
    ImageButton progress;

    private static final int REQUSETCODE_INADAPTER = 1;
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
    private int item_position;

    @OnClick(R.id.progress)
    void onProgressClick(){
        Intent intent = new Intent(Bucket.this, Progress.class);
        startActivity(intent);
    }
    @OnClick(R.id.fab_bucket)
    void onFabClick(){



    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("버킷 공간");
        items = new ArrayList<>();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        database = FirebaseDatabase.getInstance();

        new AccessDatabaseTask().execute(GETDATAFROMSERVER);
        actionLayoutShape();




    }

    private void actionLayoutShape() {
        // Linear 레이아웃매니저 setting
        linearLayoutManager = new LinearLayoutManager(this);
        // 리사이클러뷰 setting
        recyclerAdapter = new WishListRecyclerAdapter(this, items, R.layout.fragment_wishlist, groupId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUSETCODE_INADAPTER) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                item_position = bundle.getInt("POSITION");
                recyclerAdapter.notifyItemRemoved(item_position);
            } else if (resultCode == RESULT_CANCELED) {
                Log.e(TAG, ">>>>>>      onActivityResult canceled");
            }
        }
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
                overridePendingTransition(R.anim.step_in, R.anim.slide_out);
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
        private String temp_userId;
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

        }

        @Override
        protected Long doInBackground(Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.
            if (params[0].intValue() == GETDATAFROMSERVER) {
                groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.child(currentUser.getUid())
                                .child("elseBucketAnswer").getChildren().iterator();
                        while(child.hasNext()){
                            myBucketList = child.next().getValue(MyBucketList.class);
                            temp_userId = myBucketList.getUserId();
                            questionList = myBucketList.getQuestion();
                            answerList = myBucketList.getAnswer();
                            User temp_user = dataSnapshot.child(temp_userId).getValue(User.class);
                            int firstSize = Math.min(questionList.size(), answerList.size());
                            for(int i = 0 ; i < firstSize ; i++){
                                //int imgProfilePath, String nickName, String date, String question, String answer
                                item = new WishListRecyclerItem(temp_user.getUserImage(), temp_user.getUserNicname(), myBucketList.getDate(),
                                        questionList.get(i), answerList.get(i));
                                pathRef = storageRef.child(storageProfileFolder + "/" + temp_user.getUserImage());
                                storageItems.add(pathRef);
                                items.add(item);
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

            return;
        }
        @Override
        protected void onCancelled() {
            Log.i("TAG", ">>>>> doItBackground 취소");
            super.onCancelled();
        }
    }
}
