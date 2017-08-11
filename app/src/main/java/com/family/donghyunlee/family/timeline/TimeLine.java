package com.family.donghyunlee.family.timeline;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.Toast;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.bucket.Bucket;
import com.family.donghyunlee.family.photoalbum.PhotoAlbum;
import com.family.donghyunlee.family.setting.Setting;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-31.
 */

public class TimeLine extends AppCompatActivity {

    @BindView(R.id.bar_photo)
    ImageButton barPhoto;
    @BindView(R.id.bar_calender)
    ImageButton barCalender;
    @BindView(R.id.bar_push)
    ImageButton barPush;
    @BindView(R.id.bar_setting)
    ImageButton barSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setInit();
    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
        }

    }
    @OnClick(R.id.bar_photo)
    void photoClick(){
        Intent intent = new Intent(TimeLine.this, PhotoAlbum.class);
        startActivity(intent);
    }
    @OnClick(R.id.bar_calender)
    void calenderClick(){
        Intent intent = new Intent(TimeLine.this, Bucket.class);
        startActivity(intent);
    }
    @OnClick(R.id.bar_push)
    void pushClick(){
        Toast.makeText(this, "알림", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.bar_setting)
    void settingClick(){
        Intent intent = new Intent(TimeLine.this, Setting.class);
        startActivity(intent);
    }



}