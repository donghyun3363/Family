package com.family.donghyunlee.family.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.family.donghyunlee.family.R;

/**
 * Created by DONGHYUNLEE on 2017-08-20.
 */

public class TimelineCardDialogFragment extends DialogFragment {

    // 각종 뷰 변수 선언
    public TimelineCardDialogFragment() {}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_cardoption, container);

        // 레이아웃 XML과 뷰 변수 연결
        // remove dialog title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // remove dialog background
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return view;
    }

}
