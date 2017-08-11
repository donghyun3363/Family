package com.family.donghyunlee.family.bucket;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.family.donghyunlee.family.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DONGHYUNLEE on 2017-08-11.
 */

public class Bucket extends AppCompatActivity{

    @BindView(R.id.bucket_viewpager)
    ViewPager viewPager;
    @BindView(R.id.bucket_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bucket_tabs)
    TabLayout tabLayout;

    private static final String TAG = Bucket.class.getSimpleName();
    private BucketPagerAdapter adapter;
    private static final int numberOfTabs = 2;
    private CharSequence titles[] = {"WishList", "Progressing"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("버킷 공간");


        adapter =  new BucketPagerAdapter(getSupportFragmentManager(), titles, numberOfTabs);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                //onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
