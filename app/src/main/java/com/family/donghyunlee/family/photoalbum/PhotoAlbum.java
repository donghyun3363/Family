package com.family.donghyunlee.family.photoalbum;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.family.donghyunlee.family.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-31.
 */

public class PhotoAlbum extends AppCompatActivity {

    private static final String TAG = PhotoAlbum.class.getSimpleName();
    private static final int REQUEST_CODE = 1;

    @BindView(R.id.viewpager_photo)
    ViewPager viewPager;
    @BindView(R.id.tablayout_photo)
    TabLayout tabLayout;
    @BindView(R.id.photoalbum_back)
    ImageButton photoAlbumBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoalbum);
        ButterKnife.bind(this);

        setInit();
    }

    @OnClick(R.id.photoalbum_back)
    void backClick() {
        finish();
    }

    private void setInit() {


        tabLayout.addTab(tabLayout.newTab().setText("추억 공간"));
        tabLayout.addTab(tabLayout.newTab().setText("여행 공간"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MemoryPagerAdapter adapter = new MemoryPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


}
