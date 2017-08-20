package com.family.donghyunlee.family.photoalbum;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }

    private void setInit() {

        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.tea_grean));
        }
        startFragment(MemoryFragment.newInstance());
    }
    private void startFragment(final MemoryFragment fragment) {
        Log.e("CHECK","?");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.memoryphoto_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
