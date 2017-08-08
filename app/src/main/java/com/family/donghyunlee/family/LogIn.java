package com.family.donghyunlee.family;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.timeline.TimeLine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-26.
 */

public class LogIn extends AppCompatActivity {

    @BindView(R.id.login_email)
    EditText loginEmail;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_done)
    Button loginDone;

    private static final String TAG = LogIn.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String email;
    private String password;


    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //Log.e(TAG, ">>>>>>          ID: " + currentUser.getUid());
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setInit();

    }

    // Initializing SetUp
    private void setInit() {
        // FireBase 인증 객체 생성
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference("users");
        settingListener();
    }

    @OnClick(R.id.login_back)
    void backClick() {
        //TODO 확인 작업 후 삭제.
        //FirebaseAuth.getInstance().signOut();
        finish();
    }
    @OnClick(R.id.login_done)
    void doneClick() {
        logInUser();
    }
    private void logInUser() {
        // 이메일, 패스퉈드 유효성 검사
        if (validateForm() == false)
            return;
        email = loginEmail.getText().toString();
        password = loginPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user.isEmailVerified()) {
                                Toast.makeText(LogIn.this, "로그인에 성공", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                                updateUI(user);
                            } else {
                                Toast.makeText(LogIn.this, "인증 처리가 되지 않았습니다.", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onAuthStateChanged: 이메일 처리 안됨:" + user.getUid());
                            }
                        } else {
                            Toast.makeText(LogIn.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    //TODO LOG OUT 할 때, 사용
    private void logOutUser() {
        mAuth.signOut();
        updateUI(null);
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
                            if(user.getGroupId() == null){
                                Intent intent = new Intent(getApplicationContext(), Waiting.class);
                                // 기존 스택에 있던 task를 초기화하고 새로 생성한 액티비티가 root가 된다.
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else{
                                // shared preference에 디비 정보 저장
                                editor.putString("userId", currentUser.getUid());
                                editor.putString("groupId", user.getGroupId());
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), TimeLine.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            Log.i(TAG, "사용자에 대한 Auth가 없으므로 로그인 및 회원가입을 직접해야함.");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = loginEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            loginEmail.setError(getResources().getString(R.string.text_error));
            loginEmail.requestFocus();
            valid = false;
        }
        String password = loginPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError(getResources().getString(R.string.text_error));
            loginPassword.requestFocus();
            valid = false;
        }
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            loginEmail.setError(getResources().getString(R.string.email_error));
            loginEmail.requestFocus();
            valid = false;
        }
        if (loginPassword.length() < 6) {
            loginPassword.setError(getResources().getString(R.string.short_error));
            loginPassword.requestFocus();
            valid = false;
        }
        return valid;
    }

    private void settingListener() {
        // EditText Listener Watcher Function (버튼 키 색 변경.)
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loginEmail.length() > 0 && loginPassword.length() > 0) {
                    loginDone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                    loginDone.setEnabled(true);
                } else {
                    loginDone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhGray));
                    loginDone.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        loginEmail.addTextChangedListener(watcher);
        loginPassword.addTextChangedListener(watcher);
    }
}
