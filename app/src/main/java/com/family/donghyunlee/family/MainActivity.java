package com.family.donghyunlee.family;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

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
//        RotatingTextWrapper rotatingTextWrapper = (RotatingTextWrapper) findViewById(R.id.text_switcher);
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
//
//
//        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/Reckoner_Bold.ttf");
//
        Glide.with(this).load(R.drawable.img_family).into(iv_mainImg);
//
//        // Raleway-Light
//        rotatingTextWrapper.setSize(80);
//        rotatingTextWrapper.setTypeface(typeface);
//
//        // Reckoner_Bold
//        Rotatable rotatable = new Rotatable(Color.parseColor("#000000"), 1600, "FAMILY", "WORLD");
//        rotatable.setSize(100);
//        rotatable.setAnimationDuration(500);
//        rotatable.setTypeface(typeface2);
//        rotatable.setInterpolator(new AccelerateInterpolator());
//
//        rotatingTextWrapper.setContent("WE ARE\n?", rotatable);
//        rotatingTextWrapper.bringToFront();
    }
    // ButterKnife OnClick
    @OnClick(R.id.signup)
    void signupClick(){
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }
    @OnClick(R.id.login)
    void loginClick(){
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}
