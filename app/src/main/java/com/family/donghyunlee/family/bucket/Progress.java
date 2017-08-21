package com.family.donghyunlee.family.bucket;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;
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

import static android.util.Log.i;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class Progress extends AppCompatActivity {

    private static final int GETSHAREINTOSERVER = 0;
    private static final int GETINDIVIDUALINTOSERVER = 1;
    private static final String TAG = Progress.class.getSimpleName();

    @BindView(R.id.progress_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_share)
    RecyclerView recyclerViewShare;
    @BindView(R.id.rv_individual)
    RecyclerView recyclerViewIndividaul;
    @BindView(R.id.progress_temp)
    TextView progressTemp;

    private ArrayList<ToProgressItem> indi_items;
    private ArrayList<ToProgressItem> shar_items;
    private IndividualRecyclerAdapter Indi_recyclerAdapter;
    private ShareRecyclerAdapter shar_recyclerAdapter;
    private LinearLayoutManager indi_linearLayoutManager;
    private LinearLayoutManager shar_linearLayoutManager;
    private String groupId;
    private SharedPreferences pref;
    private FirebaseDatabase database;
    private int changeFlag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        ButterKnife.bind(this);

        setInit();

    }
    @OnClick(R.id.fab_progress)
    void onFabClick(){


        if(changeFlag == 0){
            Animation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
            hideAnimation.setDuration(1000);
            recyclerViewShare.setVisibility(View.INVISIBLE);
            recyclerViewShare.setAnimation(hideAnimation);

            Animation showAnimation = new AlphaAnimation(0.0f, 1.0f);
            showAnimation.setDuration(1500);
            recyclerViewIndividaul.setVisibility(View.VISIBLE);
            recyclerViewIndividaul.setAnimation(showAnimation);
            changeFlag = 1;
            progressTemp.setText("개인 버킷리스트");
        }
        else{
            Animation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
            hideAnimation.setDuration(1000);
            recyclerViewIndividaul.setVisibility(View.INVISIBLE);
            recyclerViewIndividaul.setAnimation(hideAnimation);

            Animation showAnimation = new AlphaAnimation(0.0f, 1.0f);
            showAnimation.setDuration(1500);
            recyclerViewShare.setVisibility(View.VISIBLE);
            recyclerViewShare.setAnimation(showAnimation);

            changeFlag = 0;
            progressTemp.setText("공유 버킷리스트");
        }

    }
    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }

        changeFlag = 0;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("등록된 버킷");
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        indi_items = new ArrayList<>();
        shar_items = new ArrayList<>();

        indi_linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        Indi_recyclerAdapter = new IndividualRecyclerAdapter(this, indi_items, R.layout.activity_progress, groupId);
        recyclerViewIndividaul.setHasFixedSize(true);
        recyclerViewIndividaul.setLayoutManager(indi_linearLayoutManager);
        recyclerViewIndividaul.setAdapter(Indi_recyclerAdapter);

        shar_linearLayoutManager = new LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false);
        shar_recyclerAdapter = new ShareRecyclerAdapter(this, shar_items, R.layout.activity_progress, groupId);
        recyclerViewShare.setHasFixedSize(true);
        recyclerViewShare.setLayoutManager(shar_linearLayoutManager);
        recyclerViewShare.setAdapter(shar_recyclerAdapter);


        new AccessDatabaseTask().execute(GETSHAREINTOSERVER);
        new AccessDatabaseTask().execute(GETINDIVIDUALINTOSERVER);
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
        private DatabaseReference shareReference;
        private DatabaseReference individualReference;
        private DatabaseReference groupReference;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;

        private FirebaseStorage storage;
        private StorageReference storageRef;
        private StorageReference pathRef;
        private ArrayList<StorageReference> shareStorageItems;
        private ArrayList<StorageReference> individualStorageItems;
        private String storageProfileFolder;


        private ToProgressItem toProgressItem;
        private ToProgressItem shareItem;
        private ToProgressItem individualItem;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance();

            currentUser = mAuth.getCurrentUser();
            shareReference = database.getReference().child("groups").child(groupId).child("shareBucket");
            individualReference = database.getReference().child("groups").child(groupId).child("members").child(currentUser.getUid())
                    .child("individualBucket");
            groupReference = database.getReference().child("groups").child(groupId).child("members");
            shareStorageItems = new ArrayList<>();
            individualStorageItems = new ArrayList<>();
            storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);
            storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));

            //avLoadingIndicatorView.show();
        }

        @Override
        protected Long doInBackground(Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.
            if (params[0].intValue() == GETSHAREINTOSERVER) {
                shareReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while(child.hasNext()){
                            toProgressItem = child.next().getValue(ToProgressItem.class);

                            i(TAG, ">>>>>>11111     " + toProgressItem.getUserId());
                            shareItem = new ToProgressItem(toProgressItem.getUserId(), toProgressItem.getProfilePath(), toProgressItem.getNickName()
                                    , toProgressItem.getDate(), toProgressItem.getTitle(), toProgressItem.getTitle(), toProgressItem.getStartDate(),
                                    toProgressItem.getEndDate(), toProgressItem.getStartTime(), toProgressItem.getEndTime(), toProgressItem.getMemo(),
                                    toProgressItem.isShareCheck(), toProgressItem.isWithCheck());
                            pathRef = storageRef.child(storageProfileFolder + "/" + toProgressItem.getProfilePath());
                            shareStorageItems.add(pathRef);
                            shar_items.add(shareItem);

                        }
                        shar_recyclerAdapter.setStorageitem(shareStorageItems);
                        shar_recyclerAdapter.notifyDataSetChanged();
                        return;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return result;
            }
            else if (params[0].intValue() == GETINDIVIDUALINTOSERVER) {
                individualReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while(child.hasNext()){
                            toProgressItem = child.next().getValue(ToProgressItem.class);

                            i(TAG, ">>>>>>22222     " + toProgressItem.getUserId());
                            individualItem = new ToProgressItem(toProgressItem.getUserId(), toProgressItem.getProfilePath(), toProgressItem.getNickName()
                                    , toProgressItem.getDate(), toProgressItem.getTitle(), toProgressItem.getTitle(), toProgressItem.getStartDate(),
                                    toProgressItem.getEndDate(), toProgressItem.getStartTime(), toProgressItem.getEndTime(), toProgressItem.getMemo(),
                                    toProgressItem.isShareCheck(), toProgressItem.isWithCheck());
                            pathRef = storageRef.child(storageProfileFolder + "/" + toProgressItem.getProfilePath());
                            individualStorageItems.add(pathRef);
                            indi_items.add(individualItem);
                        }
                        Indi_recyclerAdapter.setStorageitem(individualStorageItems);
                        Indi_recyclerAdapter.notifyDataSetChanged();
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
            i(TAG, ">>>>>> " + s + " <<");

            //avLoadingIndicatorView.hide();
            return;
        }
        @Override
        protected void onCancelled() {
            i("TAG", ">>>>> doItBackground 취소");
            super.onCancelled();
        }
    }
}
 