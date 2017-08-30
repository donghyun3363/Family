package com.family.donghyunlee.family.bucket;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.PushItem;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.data.WishListRecyclerItem;
import com.family.donghyunlee.family.dialog.BucketContentDialogFragment;
import com.family.donghyunlee.family.dialog.IndividualContentDialogFragment;
import com.family.donghyunlee.family.dialog.ShareContentDialogFragment;
import com.family.donghyunlee.family.photoalbum.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by DONGHYUNLEE on 2017-08-11.
 */

public class Bucket extends AppCompatActivity{

    @BindView(R.id.bucket_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_wishlist)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ImageButton progress;
    @BindView(R.id.fab_bucket)
    FloatingActionButton fab;
    @BindView(R.id.bucket_sort)
    LinearLayout bucketSort;
    @BindView(R.id.buck_sort_text)
    TextView bucketSortText;

    EditText popupBucketQuestion;
    EditText popupBucketAnswer;



    private static final String TAG = Bucket.class.getSimpleName();
    private ArrayList<WishListRecyclerItem> items;
    private StaggeredGridLayoutManager layoutManager;
    private WishListRecyclerAdapter recyclerAdapter;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference pushReference;
    private DatabaseReference memberReference;
    private DatabaseReference wishListReference;
    private DatabaseReference userReference;
    private SharedPreferences pref;
    private String groupId;
    private User curUser;
    private ArrayList<WishListRecyclerItem> temp_items;
    private ArrayList<WishListRecyclerItem> entire_items;
    @OnClick(R.id.progress)
    void onProgressClick(){
        Intent intent = new Intent(Bucket.this, Progress.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.step_back);
    }

    @OnClick(R.id.bucket_back)
    void backClick(){
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }
        setInit();


    }
    @OnClick(R.id.bucket_sort)
    void onSortClick(){

        PopupMenu popupMenu = new PopupMenu(this, bucketSort);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.bucketsort_menu, menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.menu_sort_entire: // 모두
                        temp_items.clear();
                        entire_items.clear();
                        bucketSortText.setText("전체보기");
                        sort(4);
                        return true;

                    case R.id.menu_sort_s:      // 컬러 0
                        temp_items.clear();
                        entire_items.clear();
                        bucketSortText.setText("버킷's");
                        sort(0);
                        return true;

                    case R.id.menu_sort_ing:    // 컬러 1
                        temp_items.clear();
                        entire_items.clear();
                        bucketSortText.setText("버킷-ing");
                        sort(1);
                        return true;

                    case R.id.menu_sort_ed:     // 컬러 2
                        temp_items.clear();
                        entire_items.clear();
                        bucketSortText.setText("버킷-ed");
                        sort(2);
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    void sort(final int color){
        wishListReference = database.getReference().child("groups").child(groupId).child("wishList");
        wishListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    WishListRecyclerItem wishListRecyclerItem = child.next().getValue(WishListRecyclerItem.class);
                    if(wishListRecyclerItem.getColor() == color){
                        temp_items.add(wishListRecyclerItem);
                    } else if(wishListRecyclerItem.getColor() == color){
                        temp_items.add(wishListRecyclerItem);
                    } else if(wishListRecyclerItem.getColor() == color){
                        temp_items.add(wishListRecyclerItem);

                    }
                    if(color == 4){
                        entire_items.add(wishListRecyclerItem);
                    }

                }

                if(color == 4){
                    recyclerAdapter.setItems(entire_items);
                    recyclerAdapter.notifyDataSetChanged();
                } else{
                    recyclerAdapter.setItems(temp_items);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setInit() {
        items = new ArrayList<>();
        entire_items = new ArrayList<>();
        temp_items = new ArrayList<>();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        database = FirebaseDatabase.getInstance();
        actionLayoutShape();
        itemTouchListener();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userReference = database.getReference().child("groups").child(groupId).child("members");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    curUser = child.next().getValue(User.class);
                    if(currentUser.getUid().equals(curUser.getId())){
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fab.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fab.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerAdapter.cleanupListener();
    }

    private void actionLayoutShape() {
        // Linear 레이아웃매니저 setting

        // 리사이클러뷰 setting
        recyclerAdapter = new WishListRecyclerAdapter(this, items, R.layout.fragment_wishlist, groupId);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void itemTouchListener() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 등록 및 진행 상황 확인
                fab.hide();

                WishListRecyclerItem item = recyclerAdapter.getItems().get(position);
                if(item.getColor() == 0) {
                    BucketContentDialogFragment dialogFragment =
                            BucketContentDialogFragment.newInstance(item.getWishListKey(), item.getNickName(), item.getImgProfilePath(),
                                    item.getDate(), item.getQuestion(), item.getAnswer(), position);
                    fragmentManager.beginTransaction().commit();
                    dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                    dialogFragment.show(fragmentManager, "bucketContentDialog");
                }
                else if(item.getColor() == 1 || item.getColor() == 2){
                    // 진행 중
                   if(item.getShare() == false){
                      IndividualContentDialogFragment dialogFragment =
                               IndividualContentDialogFragment.newInstance(item.getWishListKey(), item.getShare(), item.getBucketKeyRegistered(), position);
                       fragmentManager.beginTransaction().commit();
                       dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                       dialogFragment.show(fragmentManager, "individualContentDialog");
                   }
                   else if(item.getShare() == true){
                       ShareContentDialogFragment dialogFragment =
                               ShareContentDialogFragment.newInstance(item.getWishListKey(), item.getShare(), item.getBucketKeyRegistered(), position);
                       fragmentManager.beginTransaction().commit();
                       dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                       dialogFragment.show(fragmentManager, "shareContentDialog");
                   }
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {
                // 이동 및 삭제
            }
        }));
    }
    @OnClick(R.id.fab_bucket)
    void onFabClick() {
        AlertDialog.Builder dialog = createDialog();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_make_bucket, (ViewGroup) findViewById(R.id.popup_bucket_root));
        popupBucketQuestion = (EditText) layout.findViewById(R.id.popup_bucket_question);
        popupBucketAnswer = (EditText) layout.findViewById(R.id.popup_bucket_answer);

        dialog.setView(layout);
        dialog.show();

    }

    private AlertDialog.Builder createDialog() {
        AlertDialog.Builder insertDialog = new AlertDialog.Builder(this);
        insertDialog.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.dialog_posi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user 그룹 아이디와 프로필 사진 가져오기

                        if (TextUtils.isEmpty(popupBucketQuestion.getText())) {

                            new SweetAlertDialog(Bucket.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("버킷 생성 실패")
                                    .setContentText("Qusetion을 입력하지 않았습니다.")
                                    .setConfirmText("확인")
                                    .show();
                            return;
                        }

                        if (TextUtils.isEmpty(popupBucketAnswer.getText())) {
                            new SweetAlertDialog(Bucket.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("버킷 생성 실패")
                                    .setContentText("Answer을 입력하지 않았습니다.")
                                    .setConfirmText("확인")
                                    .show();
                            return;
                        }
                        addWishList();

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

    private void addWishList() {
        String key;
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 EEEE", Locale.getDefault());

        wishListReference = database.getReference().child("groups").child(groupId).child("wishList");
        key = wishListReference.push().getKey();

        WishListRecyclerItem wishListRecylclerItem = new WishListRecyclerItem(key, curUser.getId(), curUser.getUserImage(),
                curUser.getUserNicname(), CurDateFormat.format(date),
                popupBucketQuestion.getText().toString(), popupBucketAnswer.getText().toString(), 0);


        wishListReference.child(key).setValue(wishListRecylclerItem);

        pushDatabasehAccess(curUser);
    }
    private void pushDatabasehAccess(final User userItem){

        pushReference = database.getReference().child("groups").child(groupId).child("push");
        memberReference = database.getReference().child("groups").child(groupId).child("members");
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        final SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

        memberReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    User curUser = child.next().getValue(User.class);
                    if(!curUser.getId().equals(currentUser.getUid())){
                        String data = userItem.getUserNicname() + "님이 새로운 버킷을 추가하였습니다. 버킷을 확인해보세요!";
                        String key;
                        PushItem item = new PushItem(userItem.getUserImage(), data ,CurDateFormat.format(date));
                        key = pushReference.child(curUser.getId()).push().getKey();
                        pushReference.child(curUser.getId()).child(key).setValue(item);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
