package com.family.donghyunlee.family;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.timeline.TimeLine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by DONGHYUNLEE on 2017-08-20.
 */

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    int SPLASH_TIME = 5000;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private SharedPreferences pref;
    private Handler mHandler;
    SharedPreferences.Editor editor;
    private AVLoadingIndicatorView avl;
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>  splash user!:  " + currentUser);
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView splashImage = (ImageView) findViewById(R.id.splash_image);
         avl = (AVLoadingIndicatorView) findViewById(R.id.avi);
        mHandler = new Handler();

        setInit();
        avl.show();
        Glide.with(getApplicationContext()).load("").centerCrop().placeholder(R.drawable.ic_icon_font).into(splashImage);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                avl.hide();
                overridePendingTransition(0, android.R.anim.fade_in);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, SPLASH_TIME);
    }

    private void setInit() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        databaseReference = database.getReference("users");
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            Log.i(TAG, ">>>>>>     USERID: " + currentUser.getUid());

            databaseReference = database.getReference("users").child(currentUser.getUid());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User curUser = dataSnapshot.getValue(User.class);
                    if(curUser == null)
                        return;
                    pref = getSharedPreferences("pref", MODE_PRIVATE);
                    Log.i(TAG, "????????????????????     USERID:" + curUser.getId());

                    if (curUser.getGroupId().equals("empty")) {
                        editor = pref.edit();
                        editor.putString("userId", currentUser.getUid());
                        editor.commit();
                        mHandler.removeCallbacksAndMessages(null);
                        avl.hide();
                        Intent intent = new Intent(SplashActivity.this, Waiting.class);
                        overridePendingTransition(0, android.R.anim.fade_in);
                        startActivity(intent);
                        finish();
                    }else {
                        editor = pref.edit();
                        editor.putString("userId", currentUser.getUid());
                        editor.putString("groupId", curUser.getGroupId());
                        editor.commit();
                        mHandler.removeCallbacksAndMessages(null);
                        avl.hide();
                        Intent intent = new Intent(getApplicationContext(), TimeLine.class);
                        overridePendingTransition(0, android.R.anim.fade_in);
                        intent.putExtra("ISFIRSTTIME?", false);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            mHandler.removeCallbacksAndMessages(null);
            avl.hide();
            Log.i(TAG, "기존 로그인을 하지 않음.");
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            overridePendingTransition(0, android.R.anim.fade_in);
            startActivity(intent);
            finish();
        }
    }
}
