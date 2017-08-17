package com.family.donghyunlee.family.photoalbum;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.MemoryAlbum;
import com.family.donghyunlee.family.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
 */


public class MemoryFragment extends Fragment {


    private static final String TAG = MemoryFragment.class.getSimpleName();
    @BindView(R.id.fab_photo)
    FloatingActionButton fabPhoto;
    @BindView(R.id.rv_memory)
    RecyclerView recyclerView;
    @BindView(R.id.change_card)
    ImageButton changeCard;
    private String groupId;
    private EditText inputTitle;
    private EditText inputLocation;



    private MemoryAlbumRecyclerAdapter recyclerAdapter;
    private StaggeredGridLayoutManager staggeredLayoutManager;
    private LinearLayoutManager linearLayoutManger;
    List<MemoryAlbum> items = new ArrayList<>();
    private int willChange_flag = 3;
    private static final int GRID_LAYOUT = 0;
    private static final int LIST_LAYOUT = 1;
    private int position;
    private String albumIdBeforeSel;
    private static final int REQUESTCODEFROMINALBUM = 100;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private DatabaseReference groupReference;
    private DatabaseReference albumChangingReference;
    ValueEventListener valueEventListener;
    public static MemoryFragment newInstance() {
        Bundle args = new Bundle();

        MemoryFragment fragment = new MemoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        fabPhoto.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUESTCODEFROMINALBUM: {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    albumIdBeforeSel = bundle.getString("ALBUMID");
                    position = bundle.getInt("ALBUMPOSITION");
                    Log.e("!!!!!!!!!!", "albumIdBeforeSel: " + albumIdBeforeSel);
                    albumChangingReference = database.getReference("groups").child(groupId).child("memoryPhoto")
                            .child(albumIdBeforeSel);


                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            MemoryAlbum memoryAlbum = dataSnapshot.getValue(MemoryAlbum.class);
                            Log.e("!!", "positiong: "+ position);
                            //items.set(position-1 ,memoryAlbum);
                            items.set(position ,memoryAlbum);
                            recyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    albumChangingReference.addValueEventListener(valueEventListener);

                } else {
                    Log.e(TAG, ">>>>> " + "REQUESTCODEFROMINALBUM don't result ");
                }
                break;
            }
        }
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
        // 서버에서 앨범을 얻음

        actionLayoutShape();    // 리사이클러뷰 생성 및 레이아웃 셋팅(Item 등)
        itemTouchListener();
        getAlbum();
        return v;
    }

    private void setInit() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        // sharedreference에서 가져오기
        groupId = pref.getString("groupId", "");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        userReference = database.getReference("users");
        groupReference = database.getReference("groups");
    }

    private void itemTouchListener() {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //imgMemoryAlbum = (ImageView) getActivity().findViewById(R.id.img_memory_album);
                Intent intent = new Intent(getActivity(), InAlbum.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // View를 연결
                    String transitionName = getString(R.string.transitionName);
                    View transitionView = view.findViewById(R.id.img_memory_album);
                    ViewCompat.setTransitionName(transitionView, transitionName);

                    String transitionName2 = getString(R.string.transitionName2);
                    View transitionView2 = view.findViewById(R.id.title_memory_album);
                    ViewCompat.setTransitionName(transitionView2, transitionName2);

                    Pair<View, String> p1 = Pair.create(transitionView, transitionName);
                    Pair<View, String> p2 = Pair.create(transitionView2, transitionName2);

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), p1, p2);
                    intent.putExtra("TITLE", recyclerAdapter.getCurItem(position).getAlbumTitle());
                    intent.putExtra("ALBUM_ID", recyclerAdapter.getCurItem(position).getId());
                    intent.putExtra("ALBUM_POSITION", position);
                    startActivityForResult(intent, REQUESTCODEFROMINALBUM, options.toBundle());
                    fabPhoto.hide();
                }
                else {
                    intent.putExtra("TITLE", recyclerAdapter.getCurItem(position).getAlbumTitle());

                    startActivityForResult(intent, REQUESTCODEFROMINALBUM);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // 길게 터치할 경우
            }
        }));
    }

    private void actionLayoutShape() {

        // Staggered 레이아웃매니저 setting
        staggeredLayoutManager = new StaggeredGridLayoutManager(2,1);
        staggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        staggeredLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);


        // Linear 레이아웃매니저 setting
        linearLayoutManger = new LinearLayoutManager(getActivity().getApplicationContext());


        // 리사이클러뷰 setting
        recyclerAdapter = new MemoryAlbumRecyclerAdapter(getActivity(), items, R.layout.fragment_memory, groupId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManger);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @OnClick(R.id.change_card)
    void settingLayoutButton(){
        if(willChange_flag == LIST_LAYOUT|| willChange_flag == 3){
            willChange_flag = GRID_LAYOUT ;
        }
        else{
            willChange_flag = LIST_LAYOUT;
        }
        if(willChange_flag == GRID_LAYOUT) {
            recyclerView.setLayoutManager(staggeredLayoutManager);
            changeCard.setImageResource(R.drawable.ic_viewstream);
        }
        else{
            recyclerView.setLayoutManager(linearLayoutManger);
            changeCard.setImageResource(R.drawable.ic_dashbutton);
        }
    }

    @OnClick(R.id.fab_photo)
    void onFabClick() {
        AlertDialog.Builder dialog = createDialog();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_make_meomory_album, (ViewGroup) getActivity().findViewById(R.id.popup_memory_root));
        inputTitle = (EditText) layout.findViewById(R.id.popup_memory_input);
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
                        // user 그룹 아이디와 프로필 사진 가져오기
                        if (inputTitle.length() <= 0 ) {
//                            inputText.requestFocus();
//                            inputText.setError("앨범명을 입력해주세요!");
                            //TODO 다이얼로그 꺼지는 것 처리
                            Toast.makeText(getActivity(), "정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show();

                        } else {

                            addAlbum();
                        }
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

    public void addAlbum(){
        userReference = database.getReference("users");
        groupReference = database.getReference("groups");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupId = null;
                String profileimg = null;
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                String key;
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while (child.hasNext()) {
                    //찾고자 하는 ID값은 key로 존재하는 값
                    User user = child.next().getValue(User.class);
                    if (user.getId().equals(currentUser.getUid())) {
                        groupId = user.getGroupId();
                        profileimg = user.getUserImage();
                    }
                }
                // 키 생성 후 데이터 베이스에 저장
                groupReference = groupReference.child(groupId)
                        .child("memoryPhoto");
                key = groupReference.push().getKey();
                MemoryAlbum albumData = new MemoryAlbum(key, currentUser.getUid(), inputTitle.getText().toString(), CurDateFormat.format(date), "empty", profileimg);
                groupReference.child(key).setValue(albumData);
                items.add(0, albumData);
                recyclerAdapter.notifyDataSetChanged();

                Toast.makeText(getActivity(), "새 앨범이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // 실시간 데이터베이스에서 inAlbum의 image를 가져온다.
    public void getAlbum(){
        groupReference = database.getReference("groups")
                .child(groupId).child("memoryPhoto");
        groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while (child.hasNext()) {
                    MemoryAlbum memoryAlbum = child.next().getValue(MemoryAlbum.class);
                    items.add(0, memoryAlbum);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

