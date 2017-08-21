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

/**
 * Created by DONGHYUNLEE on 2017-08-21.
 */

public class MainFirstFragment extends Fragment {

    @BindView(R.id.main_first_image)
    ImageView firstImage;


    public MainFirstFragment(){
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_first, container, false);
        ButterKnife.bind(this, v);

        Glide.with(getContext()).load(R.drawable.main_image1).bitmapTransform(new ColorFilterTransformation(getContext(), Color.argb(80, 0, 0, 0)))
                .crossFade().into(firstImage);

        return v;
    }
}
