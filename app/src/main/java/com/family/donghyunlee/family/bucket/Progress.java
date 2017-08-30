package com.family.donghyunlee.family.bucket;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;
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

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class Progress extends AppCompatActivity {

    private static final int GETSHAREINTOSERVER = 0;
    private static final int GETINDIVIDUALINTOSERVER = 1;
    private static final String TAG = Progress.class.getSimpleName();

    @BindView(R.id.progress_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_progress)
    RecyclerView recyclerView;

    @BindView(R.id.progress_sort_text)
    TextView progressSortText;
    @BindView(R.id.progress_sort)
    LinearLayout progressSort;
    @BindView(R.id.progress_percent)
    TextView progressPercent;
    private ArrayList<WishListRecyclerItem> items;
    private ProgressRecyclerAdapter recyclerAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private String groupId;
    private SharedPreferences pref;
    private FirebaseDatabase database;
    private int changeFlag;
    private DatabaseReference wishListReference;

    private DatabaseReference individualReference;

    private DatabaseReference shareReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseStorage storage;

    private ArrayList<WishListRecyclerItem> done_items;
    private int doneBucketCount;
    private int myBucketCount;
    private int totoalCount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        ButterKnife.bind(this);

        setInit();
        setMyBucket();
    }
    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }

        changeFlag = 0;
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        currentUser = mAuth.getCurrentUser();


        items = new ArrayList<>();
        done_items = new ArrayList<>();
        myBucketCount = 0;
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerAdapter = new ProgressRecyclerAdapter(this, items, R.layout.activity_progress, groupId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        itemTouchListener();
    }
    private void setMyBucket() {
        wishListReference = database.getReference().child("groups").child(groupId).child("wishList");
        individualReference = database.getReference().child("groups").child(groupId).child("individualBucket");
        shareReference = database.getReference().child("groups").child(groupId).child("shareBucket");
        wishListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()) {
                    ++totoalCount;
                    final WishListRecyclerItem wishListRecyclerItem = child.next().getValue(WishListRecyclerItem.class);
                    if(wishListRecyclerItem.getUserId().equals(currentUser.getUid())){
                        items.add(wishListRecyclerItem);
                        Log.i(TAG, ">>>>>>>>>>>>>>>>>>???  " +wishListRecyclerItem.getAnswer());
                        if(wishListRecyclerItem.getBucketKeyRegistered() == null){
                            continue;
                        }
                        individualReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                                Iterator<DataSnapshot> child2 = dataSnapshot.getChildren().iterator();

                                while(child.hasNext()){
                                    String key = child.next().getKey();
                                    ToProgressItem toProgressItem = child2.next().getValue(ToProgressItem.class);
                                    if(wishListRecyclerItem.getBucketKeyRegistered().equals(key)){
                                        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>????? 1    : " + toProgressItem.getComplete());
                                        if(toProgressItem.getComplete()){
                                            Log.i(TAG, ">>>>>>>>>>>>>>>>> HERE1");
                                            ++myBucketCount;
                                        }
                                    }
                                    shareReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                                            Iterator<DataSnapshot> child2 = dataSnapshot.getChildren().iterator();

                                            while(child.hasNext()){
                                                String key = child.next().getKey();
                                                ToProgressItem toProgressItem = child2.next().getValue(ToProgressItem.class);
                                                if(wishListRecyclerItem.getBucketKeyRegistered().equals(key)){
                                                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>????? 2    : " + toProgressItem.getComplete());
                                                    if(toProgressItem.getComplete()){

                                                        Log.i(TAG, ">>>>>>>>>>>>>>>>> HERE2");
                                                        ++myBucketCount;
                                                    }
                                                }
                                            }



                                            progressPercent.setText(String.valueOf(myBucketCount));
                                            myBucketCount = 0;
                                            totoalCount =0;
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>?????  3  : " + myBucketCount);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    recyclerAdapter.notifyDataSetChanged();
                }
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>?????  5  : " + myBucketCount);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    private void setDoneBucket() {
        individualReference = database.getReference().child("groups").child(groupId).child("individualBucket");
        shareReference = database.getReference().child("groups").child(groupId).child("shareBucket");
        wishListReference = database.getReference().child("groups").child(groupId).child("wishList");
        individualReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                Iterator<DataSnapshot> child2 = dataSnapshot.getChildren().iterator();

                while(child.hasNext()){
                    final String key = child.next().getKey();
                    Log.i(TAG, ">>>>>>>>>>>>    " + key);
                    ToProgressItem toProgressItem = child2.next().getValue(ToProgressItem.class);
                    if(toProgressItem.getUserId().equals(currentUser.getUid())){
                        wishListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                                while(child.hasNext()) {
                                    WishListRecyclerItem wishListRecyclerItem = child.next().getValue(WishListRecyclerItem.class);
                                    if(wishListRecyclerItem.getBucketKeyRegistered() == null){
                                        continue;
                                    }
                                    if(wishListRecyclerItem.getBucketKeyRegistered().equals(key)){
                                        done_items.add(wishListRecyclerItem);

                                    }
                                    Log.i(TAG, ">>>>>>>>>>>>      done_items: " + done_items.size());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                shareReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        Iterator<DataSnapshot> child2 = dataSnapshot.getChildren().iterator();

                        while (child.hasNext()) {
                            final String key = child.next().getKey();
                            ToProgressItem toProgressItem = child2.next().getValue(ToProgressItem.class);
                            if (toProgressItem.getUserId().equals(currentUser.getUid())) {
                                wishListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                                        while (child.hasNext()) {
                                            WishListRecyclerItem wishListRecyclerItem = child.next().getValue(WishListRecyclerItem.class);
                                            if(wishListRecyclerItem.getBucketKeyRegistered() == null){
                                                continue;
                                            }
                                            if (wishListRecyclerItem.getBucketKeyRegistered().equals(key)) {
                                                done_items.add(wishListRecyclerItem);
                                            }
                                        }
                                        recyclerAdapter.notifyDataSetChanged();
                                        Log.i(TAG, ">>>>>>>>>>>>      done_items2222222222: " + done_items.size());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if(done_items.size() == 0){
                    recyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @OnClick(R.id.progress_back)
    void backClick(){
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }

    @OnClick(R.id.progress_sort)
    void onSortClick(){

        PopupMenu popupMenu = new PopupMenu(this, progressSort);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.progress_menu, menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.menu_my_bucket:
                        items.clear();
                        done_items.clear();
                        recyclerAdapter.setItems(items);
                        setMyBucket();
                        progressSortText.setText("나의 버컷모음");
                        return true;

                    case R.id.menu_done_bucket:
                        items.clear();
                        done_items.clear();
                        recyclerAdapter.setItems(done_items);
                        setDoneBucket();
                        progressSortText.setText("나의 등록일정");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
    private void itemTouchListener() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 등록 및 진행 상황 확인


                WishListRecyclerItem item = recyclerAdapter.getItems().get(position);
                if (item.getColor() == 0) {
                    BucketContentDialogFragment dialogFragment =
                            BucketContentDialogFragment.newInstance(item.getWishListKey(), item.getNickName(), item.getImgProfilePath(),
                                    item.getDate(), item.getQuestion(), item.getAnswer(), position);
                    fragmentManager.beginTransaction().commit();
                    dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                    dialogFragment.show(fragmentManager, "bucketContentDialog");
                } else if (item.getColor() == 1 || item.getColor() == 2) {
                    // 진행 중
                    if (item.getShare() == false) {
                        IndividualContentDialogFragment dialogFragment =
                                IndividualContentDialogFragment.newInstance(item.getWishListKey(), item.getShare(), item.getBucketKeyRegistered(), position);
                        fragmentManager.beginTransaction().commit();
                        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                        dialogFragment.show(fragmentManager, "individualContentDialog");
                    } else if (item.getShare() == true) {
                        ShareContentDialogFragment dialogFragment =
                                ShareContentDialogFragment.newInstance(item.getWishListKey(), item.getShare(), item.getBucketKeyRegistered(), position);
                        fragmentManager.beginTransaction().commit();
                        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                        dialogFragment.show(fragmentManager, "shareContentDialog");
                    }
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {
                // 이동 및 삭제
            }
        }));
    }
}










 