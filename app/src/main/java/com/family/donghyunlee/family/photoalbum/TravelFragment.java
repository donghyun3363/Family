package com.family.donghyunlee.family.photoalbum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.family.donghyunlee.family.R;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
 */

public class TravelFragment extends Fragment {

    public static TravelFragment newInstance() {
        Bundle args = new Bundle();

        TravelFragment fragment = new TravelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_travel, container, false);
        return v;
    }
}
