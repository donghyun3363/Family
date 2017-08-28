package com.family.donghyunlee.family;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import cn.pedant.SweetAlert.SweetAlertDialog;

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
    private String inviteGroupId;
    private User curUser;
    private SweetAlertDialog pDialog;
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }
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
        pDialog = new SweetAlertDialog(LogIn.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(true);
        pDialog.show();
        Log.i(TAG, ">>>>>                    ?????");
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
                                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                                updateUI(user);
                            } else {
                                pDialog.hide();
                                new SweetAlertDialog(LogIn.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("로그인을 할 수 없습니다.")
                                        .setContentText("가입" +
                                                " 이메일 인증 후 로그인을 해주세요!")
                                        .setConfirmText("확인")
                                        .show();
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

    private void updateUI(final FirebaseUser user) {
        inviteGroupId = pref.getString("groupId", "");
        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>    inviteGroupId" + inviteGroupId);
        if(!TextUtils.isEmpty(inviteGroupId)){
            if (user == null) {
                new SweetAlertDialog(LogIn.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("로그인 실패")
                        .setContentText("초대 받은 회원도 회원 가입 후 로그인 해주세요!")
                        .setConfirmText("확인")
                        .show();
                return;
            }
            Log.i(TAG, ">>>>>>>>>>>>>>>>>>>> HERE invite: " + inviteGroupId);
            databaseReference = database.getReference().child("users").child(user.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    curUser = dataSnapshot.getValue(User.class);
                    databaseReference.child("groupId").setValue(inviteGroupId);
                    curUser.setGroupId(inviteGroupId);
                    databaseReference = database.getReference().child("groups").child(inviteGroupId).child("members");
                    databaseReference.child(curUser.getId()).setValue(curUser);

                    editor.putString("userId", user.getUid());
                    editor.putString("groupId", inviteGroupId);
                    editor.commit();
                    pDialog.hide();
                    Intent intent = new Intent(LogIn.this, TimeLine.class);
                    intent.putExtra("ISFIRSTTIME?", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (user != null) {
            currentUser = mAuth.getCurrentUser();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                    while(child.hasNext()) {
                        User curUser = child.next().getValue(User.class);
                        if(curUser.getId().equals(mAuth.getCurrentUser().getUid())){
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userId", curUser.getId());
                            editor.putString("groupId", curUser.getGroupId());
                            editor.commit();
                            pDialog.hide();
                            if(curUser.getGroupId().equals("empty")){
                                Intent intent = new Intent(getApplicationContext(), Waiting.class);
                                // 기존 스택에 있던 task를 초기화하고 새로 생성한 액티비티가 root가 된다.
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else{
                                // shared preference에 디비 정보 저장
                                editor.putString("userId", currentUser.getUid());
                                editor.putString("groupId", curUser.getGroupId());
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), TimeLine.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("ISFIRSTTIME?", false);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }
                    Log.i(TAG, "해당 사용자 존재하지만 로그인 실패.");
                    return;
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
