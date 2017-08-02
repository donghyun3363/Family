package com.family.donghyunlee.family;

import android.content.Intent;
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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("users");
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        Log.e(TAG, ">>>>>>          ID: " + currentUser.getUid());
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
        mAuth = FirebaseAuth.getInstance();

        ImageView iv_mainImg = (ImageView) findViewById(R.id.main_img);

        Glide.with(this).load(R.drawable.img_family).into(iv_mainImg);

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
//
//        Intent intent = new Intent(getApplicationContext(), PhotoAlbum.class);
//        startActivity(intent);
//
        if (user != null) {
            //---- HERE YOU CHECK IF EMAIL IS VERIFIED
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                    while(child.hasNext()) {
                        //찾고자 하는 ID값은 key로 존재하는 값
                        User user = child.next().getValue(User.class);
                        if(user.getId().equals(currentUser.getUid())){
                            Log.e(TAG,"222222222222222222222222222222222");
                            Log.e(TAG,"??: " + user.getGroupId());

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
        } else {


        }
    }

}
