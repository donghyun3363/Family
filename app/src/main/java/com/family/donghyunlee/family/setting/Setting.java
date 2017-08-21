package com.family.donghyunlee.family.setting;

/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.family.donghyunlee.family.MainActivity;
import com.family.donghyunlee.family.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */

public class Setting extends AppCompatActivity {

    @BindView(R.id.setting_logout)
    TextView settingLogOut;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setInit();

    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }

        mAuth = FirebaseAuth.getInstance();
    }
    @OnClick(R.id.setting_back)
    void backClick(){
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }

    @OnClick(R.id.setting_logout)
    void onLogOutClick(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
}
