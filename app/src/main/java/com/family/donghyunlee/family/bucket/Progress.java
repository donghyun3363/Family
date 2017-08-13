package com.family.donghyunlee.family.bucket;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class Progress extends AppCompatActivity {

    @BindView(R.id.progress_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_share)
    RecyclerView recyclerViewShare;
    @BindView(R.id.rv_individual)
    RecyclerView recyclerViewIndividaul;



    private ArrayList<ToProgressItem> indi_items;
    private ArrayList<ToProgressItem> shar_items;
    private IndividualRecyclerAdapter Indi_recyclerAdapter;
    private ShareRecyclerAdapter shar_recyclerAdapter;
    private LinearLayoutManager indi_linearLayoutManager;
    private LinearLayoutManager shar_linearLayoutManager;
    private String groupId;
    private SharedPreferences pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        ButterKnife.bind(this);

        setInit();

    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("등록된 버킷");
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        indi_linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        Indi_recyclerAdapter = new IndividualRecyclerAdapter(getBaseContext(), indi_items, R.layout.fragment_wishlist, groupId);
        recyclerViewShare.setHasFixedSize(true);
        recyclerViewShare.setLayoutManager(indi_linearLayoutManager);
        recyclerViewShare.setAdapter(Indi_recyclerAdapter);

        shar_linearLayoutManager = new LinearLayoutManager(getBaseContext(),  LinearLayoutManager.HORIZONTAL, false);
        shar_recyclerAdapter = new ShareRecyclerAdapter(getBaseContext(), shar_items, R.layout.fragment_wishlist, groupId);
        recyclerViewShare.setHasFixedSize(true);
        recyclerViewShare.setLayoutManager(shar_linearLayoutManager);
        recyclerViewShare.setAdapter(shar_recyclerAdapter);
    }
}
 