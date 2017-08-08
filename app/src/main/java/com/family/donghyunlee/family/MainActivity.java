package com.family.donghyunlee.family;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, ">>>>>>          mAuth: " + mAuth);
        currentUser = mAuth.getCurrentUser();
        //Log.e(TAG, ">>>>>>          ID: " + currentUser.getUid());
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setInit();

    }

    private void setInit() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference("users");
        ImageView iv_mainImg = (ImageView) findViewById(R.id.main_img);
        Glide.with(this).load(R.drawable.img_family1).into(iv_mainImg);

    }

    // ButterKnife OnClick
    @OnClick(R.id.signup)
    void signupClick() {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }

    @OnClick(R.id.login)
    void loginClick() {
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                    while(child.hasNext()) {
                        User user = child.next().getValue(User.class);
                        if(user.getId().equals(currentUser.getUid())){
                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userId", currentUser.getUid());
                            editor.putString("groupId", user.getGroupId());
                            editor.commit();
                            if(user.getGroupId().equals("empty")){
                                Intent intent = new Intent(getApplicationContext(), Waiting.class);
                                startActivity(intent);
                                finish();
                            } else{
                                Intent intent = new Intent(getApplicationContext(), TimeLine.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                       return;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        else {


        }
    }

}
