package com.family.donghyunlee.family.bucket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.family.donghyunlee.family.R;

import butterknife.ButterKnife;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class RegisterToProgressFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registertoprogress, container, false);
        ButterKnife.bind(this, v);


        setInit();

        return v;
    }

    private void setInit() {
    }
}
