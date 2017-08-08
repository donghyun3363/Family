package com.family.donghyunlee.family;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.enter_waiting)
    void onEnter() {
        String key;

        databaseReference = database.getReference("groups");
        key = databaseReference.push().getKey();
        databaseReference.setValue(key);

        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + currentUser.getUid());
        // TODO 파이어베이스 데이터베이스는 없는 키값일 경우 NULL을 만봔하여 죽는 경우가 생기므르 TRY로 모두 해결해 줄 것!!
        databaseReference = database.getReference("users").child(currentUser.getUid()).child("groupId");
        try {
            databaseReference.setValue(key);
        } catch (NullPointerException e){
            Log.e(TAG, ">>>>>     NullPointerException");
        }

        // TODO 반드시 LOGOUT할 때는 해당 디비 정보를 모두 삭제 해야함.
        // shared preference에 디비 정보 저장
        editor.putString("userId", currentUser.getUid());
        editor.putString("groupId", key);
        editor.commit();
    }
}
