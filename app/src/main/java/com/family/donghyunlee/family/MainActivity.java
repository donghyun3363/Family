package com.family.donghyunlee.family;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private SharedPreferences pref;
    private String key;
    private DatabaseReference bucketlistReference;
    SharedPreferences.Editor editor;
    @BindView(R.id.vp_main)
    ViewPager vp;
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setInit();
        setViewPager();
        //adminSetAnswer();
    }

    private void setViewPager() {
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.main_indicator);
        vp.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);
        indicator.setViewPager(vp);
    }

    private void setInit() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


    }


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

            databaseReference = database.getReference("users").child(currentUser.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    pref = getSharedPreferences("pref", MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putString("userId", currentUser.getUid());
                    editor.putString("groupId", user.getGroupId());
                    editor.commit();

                    if (user.getGroupId().equals("empty")) {
                        Intent intent = new Intent(getApplicationContext(), Waiting.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), TimeLine.class);
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
            Log.i(TAG, "USER가 NULL");

        }
    }
    // 설문조사 생성지
//    void adminSetAnswer(){
//
//        String[] listAnswer = getResources().getStringArray(R.array.list_answer);
//        String[] listAnswerHint = getResources().getStringArray(R.array.list_answerhint);
//        List<String> answer = new ArrayList<>();
//        List<String> answerHint = new ArrayList<>();
//
//        for(int i = 0 ; i < listAnswer.length ; i++){
//            answer.add(listAnswer[i]);
//            answerHint.add(listAnswerHint[i]);
//        }
//        MakeBucketList makBuketList = new MakeBucketList(answer, answerHint);
//        bucketlistReference = database.getReference().child("bucketList");
//        key = bucketlistReference.push().getKey();
//        bucketlistReference.child(key).setValue(makBuketList);
//
//    }
}
