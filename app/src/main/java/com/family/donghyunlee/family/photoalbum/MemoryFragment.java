package com.family.donghyunlee.family.photoalbum;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.family.donghyunlee.family.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
 */



public class MemoryFragment extends Fragment {

    @BindView(R.id.fab_photo)
    FloatingActionButton fabPhoto;
    private EditText inputText;
    MemoryAlbumRecyclerAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;
    RecyclerView recylerView;
    final int ITEM_SIZE = 2;
    List<MemoryAlbumItem> items = new ArrayList<>();
    MemoryAlbumItem[] item = new MemoryAlbumItem[ITEM_SIZE];


    public static MemoryFragment newInstance() {
        Bundle args = new Bundle();

        MemoryFragment fragment = new MemoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory, container, false);
        ButterKnife.bind(this, v);

        setInit();

        // 어뎁터 set

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerAdapter= new MemoryAlbumRecyclerAdapter(getActivity(), items, R.layout.fragment_memory);
        recylerView = (RecyclerView) v.findViewById(R.id.rv_memory);
        recylerView.setHasFixedSize(true);
        recylerView.setLayoutManager(layoutManager);
        recylerView.setAdapter(recyclerAdapter);

        recylerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recylerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(), position+"번째 아이템 클릭", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Toast.makeText(getContext(), position+"번째 롱 아이템 클릭", Toast.LENGTH_SHORT).show();
            }
        }));
        return v;
    }

    private void setInit() {

        if(items.isEmpty()){
            //TODO 아무것 도 없을 때 처리.
        }

        // ArayList에 추가
        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(new MemoryAlbumItem("1111", "22222222", "333333333333"));
        }
    }

    @OnClick(R.id.fab_photo)
    void onFabClick() {
        AlertDialog.Builder dialog = createDialog();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_makealbum, (ViewGroup) getActivity().findViewById(R.id.popup_root));
        inputText = (EditText) layout.findViewById(R.id.popup_input);
        dialog.setView(layout);
        dialog.show();

    }
    private AlertDialog.Builder createDialog() {
        AlertDialog.Builder insertDialog = new AlertDialog.Builder(getContext());
        insertDialog.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.dialog_posi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월");

                        items.add(new MemoryAlbumItem(inputText.getText().toString(),CurDateFormat.format(date)));
                        recyclerAdapter.notifyDataSetChanged();

                        Toast.makeText(getActivity(), "새 앨범을 추가하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_negati, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        return insertDialog;
    }



}

