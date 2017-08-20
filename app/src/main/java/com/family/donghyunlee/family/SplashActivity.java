package com.family.donghyunlee.family;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by DONGHYUNLEE on 2017-08-20.
 */

public class SplashActivity extends Activity {
    int SPLASH_TIME=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView splashImage = (ImageView) findViewById(R.id.splash_image);


        Glide.with(getApplicationContext()).load(R.drawable.ic_icon_font).into(splashImage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                overridePendingTransition(0,android.R.anim.fade_in);
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },SPLASH_TIME);
    }
}
