package com.family.donghyunlee.family.bucket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.WishListRecyclerItem;
import com.family.donghyunlee.family.dialog.BucketContentDialogFragment;
import com.family.donghyunlee.family.dialog.IndividualContentDialogFragment;
import com.family.donghyunlee.family.dialog.ShareContentDialogFragment;
import com.family.donghyunlee.family.photoalbum.RecyclerItemClickListener;
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


    private static final int REQUESTCODE_BUCKETCONTENT = 104;
    @BindView(R.id.bucket_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_wishlist)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ImageButton progress;
    @BindView(R.id.fab_bucket)
    FloatingActionButton fab;
    
    private static final int REQUSETCODE_INADAPTER = 1;
    private static final String TAG = Bucket.class.getSimpleName();
    private static final int GETDATAFROMSERVER= 0;
    private static final int SETDATATOSERVER= 1;
    private ArrayList<WishListRecyclerItem> items;
    private StaggeredGridLayoutManager layoutManager;
    private WishListRecyclerAdapter recyclerAdapter;
    private FirebaseDatabase database;
    private SharedPreferences pref;
    private String groupId;
    private List<String> answerList;
    private List<String> questionList;
    private int item_position;

    private int color;
    private Boolean share;
    private String bucketKeyRegistered;
    @OnClick(R.id.progress)
    void onProgressClick(){
        Intent intent = new Intent(Bucket.this, Progress.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.step_back);
    }
    @OnClick(R.id.fab_bucket)
    void onFabClick(){



    }
    @OnClick(R.id.bucket_back)
    void backClick(){
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }
        setInit();


    }

    private void setInit() {
        items = new ArrayList<>();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        database = FirebaseDatabase.getInstance();
        new AccessDatabaseTask().execute(GETDATAFROMSERVER);
        actionLayoutShape();
        itemTouchListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fab.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fab.hide();
    }

    private void actionLayoutShape() {
        // Linear 레이아웃매니저 setting

        // 리사이클러뷰 setting
        recyclerAdapter = new WishListRecyclerAdapter(this, items, R.layout.fragment_wishlist, groupId);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUESTCODE_BUCKETCONTENT){
            if (resultCode == RESULT_OK) {
                item_position = (int) data.getSerializableExtra("position");
                color = (int) data.getSerializableExtra("color");
                share = (Boolean) data.getSerializableExtra("share");
                bucketKeyRegistered = (String) data.getSerializableExtra("bucketKeyRegistered");


                Log.i(TAG, ">>>>>>>>>>>>>>>>      color: " + color + "///share: " +share);

                WishListRecyclerItem temp_item = recyclerAdapter.getItems().get(item_position);
                temp_item.setColor(color);
                temp_item.setShare(share);
                temp_item.setBucketKeyRegistered(bucketKeyRegistered);
                recyclerAdapter.notifyItemChanged(item_position, temp_item);


            } else if (resultCode == RESULT_CANCELED) {
                Log.e(TAG, ">>>>>>      onActivityResult canceled");
            }
        }

    }
    private void itemTouchListener() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 등록 및 진행 상황 확인
                fab.hide();
                // newInstance(String nickName, String profileImage, String date
            //, String question, String answer)
                WishListRecyclerItem item = recyclerAdapter.getItems().get(position);
                if(item.getColor() == 0) {
                    BucketContentDialogFragment dialogFragment =
                            BucketContentDialogFragment.newInstance(item.getWishListKey(), item.getNickName(), item.getImgProfilePath(),
                                    item.getDate(), item.getQuestion(), item.getAnswer(), position);
                    fragmentManager.beginTransaction().commit();
                    dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                    dialogFragment.show(fragmentManager, "bucketContentDialog");
                }
                else if(item.getColor() == 1){
                    // 진행 중
                   if(item.getShare() == false){
                      IndividualContentDialogFragment dialogFragment =
                               IndividualContentDialogFragment.newInstance(item.getShare(), item.getBucketKeyRegistered(), position);
                       fragmentManager.beginTransaction().commit();
                       dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                       dialogFragment.show(fragmentManager, "individualContentDialog");
                   }
                   else if(item.getShare() == true){
                       ShareContentDialogFragment dialogFragment =
                               ShareContentDialogFragment.newInstance(item.getShare(), item.getBucketKeyRegistered(), position);
                       fragmentManager.beginTransaction().commit();
                       dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                       dialogFragment.show(fragmentManager, "shareContentDialog");
                   }

                }
                else if(item.getColor() == 2){
                    // 완료.
                    Toast.makeText(Bucket.this, "hi2", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // 이동 및 삭제
            }
        }));
    }
    public class AccessDatabaseTask extends AsyncTask<Integer, String, Long> {

        public Long result = null;
        private DatabaseReference groupReference;
        private DatabaseReference bucketReference;
        private DatabaseReference memberReference;
        private DatabaseReference wishlistReference;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;

        //private MyBucketList myBucketList;
        private WishListRecyclerItem wishListRecyclerItem;
        private WishListRecyclerItem item;
        private FirebaseStorage storage;
        private StorageReference storageRef;
        private StorageReference pathRef;
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

        }

        @Override
        protected Long doInBackground(Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.
            if (params[0].intValue() == GETDATAFROMSERVER) {
                wishlistReference = database.getReference().child("groups").child(groupId)
                        .child("wishList");
                wishlistReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while(child.hasNext()){
                            wishListRecyclerItem = child.next().getValue(WishListRecyclerItem.class);
                            items.add(wishListRecyclerItem);
                        }

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
