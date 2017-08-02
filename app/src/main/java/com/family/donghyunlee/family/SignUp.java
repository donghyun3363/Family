package com.family.donghyunlee.family;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-26.
 */

public class SignUp extends AppCompatActivity {

    @BindView(R.id.signup_email)
    EditText signupEmail;
    @BindView(R.id.signup_password)
    EditText signupPassword;
    @BindView(R.id.signup_repassword)
    EditText signupRepassword;
    @BindView(R.id.signup_next)
    Button signupNext;

    private static final String TAG = SignUp.class.getSimpleName();
    private ProfileFragment profileFragment;

    // users라는 데이터 키를 참조한다.


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        setInit();
    }

    // Initializing Setup
    private void setInit() {
        setListener();
    }

    // ButterKnife OnClick
    @OnClick(R.id.signup_next)
    void nextClick() {
        // 유효성 검사
        if (validateForm() == false) {
            return;
        }
        startFragment(profileFragment.newInstance(signupEmail.getText().toString(), signupPassword.getText().toString()));
    }

    @OnClick(R.id.signup_back)
    void backClick() {
        finish();
    }

    // Start Fragment function (fragment replace)
    void startFragment(final ProfileFragment fragment) {
        Log.e(TAG, ">>>>>>>>>> profile 등록 fragment 입장");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.signup_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = signupEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            signupEmail.setError(getResources().getString(R.string.text_error));
            signupEmail.requestFocus();
            valid = false;
        }
        String password = signupPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            signupPassword.setError(getResources().getString(R.string.text_error));
            signupPassword.requestFocus();
            valid = false;
        }
        String rePassword = signupRepassword.getText().toString();
        if (TextUtils.isEmpty(rePassword)) {
            signupRepassword.setError(getResources().getString(R.string.text_error));
            signupRepassword.requestFocus();
            valid = false;
        }
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            signupEmail.setError(getResources().getString(R.string.email_error));
            signupEmail.requestFocus();
            valid = false;
        }
        if (signupPassword.length() < 6) {
            signupPassword.setError(getResources().getString(R.string.short_error));
            signupPassword.requestFocus();
            valid = false;
        }
        if (password.equals(rePassword) == false) {
            signupPassword.setError(getResources().getString(R.string.nomatech_error));
            signupPassword.requestFocus();
            valid = false;
        }
        return valid;
    }


    // EditText Listener Watcher Function (완료 및 다음 키 활성화)
    private void setListener() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (signupEmail.length() > 0 && signupPassword.length() > 0 && signupRepassword.length() > 0) {
                    signupNext.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                } else {
                    signupNext.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhGray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        signupEmail.addTextChangedListener(watcher);
        signupPassword.addTextChangedListener(watcher);
        signupRepassword.addTextChangedListener(watcher);
    }
}
