package com.family.donghyunlee.family;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.family.donghyunlee.family.data.MyBucketList;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.timeline.TimeLine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by DONGHYUNLEE on 2017-08-02.
 */

public class Waiting extends AppCompatActivity {

    @BindView(R.id.enter_waiting)
    Button enterWaiting;

    private static final String TAG = Waiting.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private User myUser;
    private MyBucketList myBucketList;
    private String groupKey;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        ButterKnife.bind(this);

        setInit();
    }


    private void setInit() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        new AccessDatabaseTask().execute();

    }


    @OnClick(R.id.enter_waiting)
    void onEnter() {

        userReference = database.getReference("users").child(currentUser.getUid());
        databaseReference = database.getReference("groups");
        //TODO 개발자 임시 설정.
        //groupKey = databaseReference.push().getKey();
        groupKey = "-KrFnp_7rxi0DYq5amJ9";
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myUser = dataSnapshot.getValue(User.class);
                myBucketList = dataSnapshot.child("myBucketAnswer").getValue(MyBucketList.class);
                Log.i(TAG, ">>>>> myBucketList" + myBucketList.getAnswer());
                try{
                    databaseReference.child(groupKey).child("members").child(currentUser.getUid()).setValue(myUser);
                    databaseReference.child(groupKey).child("members").child(currentUser.getUid()).child("myBucketAnswer").setValue(myBucketList);
                    databaseReference = database.getReference("users").child(currentUser.getUid()).child("groupId");
                    databaseReference.setValue(groupKey);
                    editor.putString("groupId", groupKey);
                    editor.commit();
                } catch (NullPointerException e) {
                    Log.e(TAG, ">>>>>>     myUser를 찾을 수 없는 에러");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, ">>>>>     Cancel Database error");
            }
        });
        Intent intent = new Intent(Waiting.this, TimeLine.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    void startFragment(final com.family.donghyunlee.family.bucketpage.BucketListFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.waiting_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public class AccessDatabaseTask extends AsyncTask<String, Void, String> {

        public String result;
        private DatabaseReference isBucketReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isBucketReference = database.getReference("users").child(currentUser.getUid());
        }

        @Override
        protected String doInBackground(String... params) {
            isBucketReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getIsBucket().equals("empty")) {
                        startFragment(com.family.donghyunlee.family.bucketpage.BucketListFragment.newInstance());
                    }
                    return;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


}
