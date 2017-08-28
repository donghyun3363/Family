package com.family.donghyunlee.family;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-21.
 */

public class MainSecondFragment extends Fragment {

    @BindView(R.id.main_second_image)
    ImageView secondImage;
    @BindView(R.id.main_second_logo)
    ImageView logo;

    public MainSecondFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main_second, container, false);
        ButterKnife.bind(this, v);
        Glide.with(getContext()).load(R.drawable.main_image2).bitmapTransform(new ColorFilterTransformation(getContext(), Color.argb(80, 0, 0, 0)))
                .crossFade().into(secondImage);
        Glide.with(getContext()).load(R.drawable.ic_album).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(getContext())).into(logo);
        return v;
    }
}