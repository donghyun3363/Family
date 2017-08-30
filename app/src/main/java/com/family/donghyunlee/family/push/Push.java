package com.family.donghyunlee.family.push;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.PushItem;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-08-27.
 */

public class Push extends AppCompatActivity {



    @BindView(R.id.rv_push)
    RecyclerView recyclerView;

    private static final String TAG = Push.class.getSimpleName();
    private PushRecyclerAdapter recyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PushItem> items;
    private FirebaseDatabase database;
    private SharedPreferences pref;
    private String groupId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        ButterKnife.bind(this);

        setInit();
    }
    private void setInit() {

        items = new ArrayList();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        database = FirebaseDatabase.getInstance();

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAdapter = new PushRecyclerAdapter(getApplicationContext(), items, R.layout.activity_push, groupId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @OnClick(R.id.push_back)
    void onBackClick() {
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
}
