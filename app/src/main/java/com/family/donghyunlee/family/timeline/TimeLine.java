package com.family.donghyunlee.family.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.photoalbum.PhotoAlbum;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-31.
 */

public class TimeLine extends AppCompatActivity {

    @BindView(R.id.timeline_back)
    ImageButton timelineBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.timeline_back)
    void backClick(){
        finish();
    }
    @OnClick(R.id.timeline_photo)
    void photoClick(){
        Intent intent = new Intent(TimeLine.this, PhotoAlbum.class);
        startActivity(intent);


    }
}
