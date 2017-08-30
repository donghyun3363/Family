package com.family.donghyunlee.family.dialog;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.OnSingleClickListener;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;
import com.family.donghyunlee.family.data.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGHYUNLEE on 2017-08-27.
 */

public class ShareContentDialogFragment extends DialogFragment {

    @BindView(R.id.share_profile)
    ImageView shareProfile;
    @BindView(R.id.share_nickname)
    TextView shareNickname;
    @BindView(R.id.share_date)
    TextView shareDate;
    @BindView(R.id.share_title)
    TextView shareTitle;
    @BindView(R.id.share_dump)
    TextView shareDump;
    @BindView(R.id.share_startdate)
    TextView shareStartdate;
    @BindView(R.id.share_starttime)
    TextView shareStarttime;
    @BindView(R.id.share_enddate)
    TextView shareEnddate;
    @BindView(R.id.share_endtime)
    TextView shareEndtime;
    @BindView(R.id.share_location)
    TextView shareLocation;
    @BindView(R.id.share_memo)
    TextView shareMemo;

    @BindView(R.id.share_check)
    CheckBox shareCheck;
    @BindView(R.id.complete_check)
    CheckBox completeCheck;
    @BindView(R.id.entire_container)
    RelativeLayout entireContainger;
    @BindView(R.id.in_container)
    LinearLayout inContainer;
    @BindView(R.id.complete_alarm)
    TextView completeAlarm;
    @BindView(R.id.shar_particishow_text)
    TextView shareText;
    @BindView(R.id.share_particishow)
    LinearLayout sharePartici;
    private static final String TAG = TimelineCardDialogFragment.class.getSimpleName();
    private static final int REQUESTCODE_BUCKETCONTENT = 104;
    private SharedPreferences pref;
    private String groupId;

    private boolean share;
    private String bucketKeyRegistered;
    private int position;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;
    private String storageProfileFolder;

    private FirebaseDatabase database;
    private DatabaseReference wishListReference;
    private DatabaseReference shareReference;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String wishListKey;
    private User curUser;
    public ShareContentDialogFragment() {
    }

    public static ShareContentDialogFragment newInstance(String wishListKey, Boolean share, String bucketKeyRegistered, int position) {

        ShareContentDialogFragment shareContentDialogFragment = new ShareContentDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("share", share);
        args.putString("bucketKeyRegistered", bucketKeyRegistered);
        args.putInt("position", position);
        args.putString("wishListKey", wishListKey);
        shareContentDialogFragment.setArguments(args);
        return shareContentDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_sharecontent, container);
        ButterKnife.bind(this, v);
        setInit();

        return v;
    }

    private void setInit() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
        storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);


        // 번들로 부터 받는 데이터
        share = getArguments().getBoolean("share");
        bucketKeyRegistered = getArguments().getString("bucketKeyRegistered");
        position = getArguments().getInt("position");
        wishListKey = getArguments().getString("wishListKey");

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
//        shareReference = database.getReference().child("groups").child(groupId).child("sharePart");
//        shareReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
//                while(child.hasNext()){
//                    String nickName = dataSnapshot.child(bucketKeyRegistered).getKey();
//                    if(nickName.equals(curUser.getUserNicname())){
//                        shareCheck.setChecked(true);
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        shareReference = database.getReference().child("groups").child(groupId).child("shareBucket").child(bucketKeyRegistered);
        shareReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ToProgressItem toProgressItem = dataSnapshot.getValue(ToProgressItem.class);
                profile_pathRef = storageRef.child(storageProfileFolder + "/" + toProgressItem.getProfilePath());
                Glide.with(getContext()).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                        .crossFade().bitmapTransform(new CropCircleTransformation(getContext()))
                        .into(shareProfile);
                 shareNickname.setText(toProgressItem.getNickName());
                 shareDate.setText(toProgressItem.getDate());
                 shareTitle.setText(toProgressItem.getTitle());
                if(toProgressItem.getEndDate() == null) {
                    shareDump.setText("");
                    shareEnddate.setText("");
                    shareEndtime.setText("");
                }
                else{
                    shareDump.setText("~");
                    shareEnddate.setText(toProgressItem.getEndDate());
                    shareEndtime.setText(toProgressItem.getEndTime());
                }
                shareStartdate.setText(toProgressItem.getStartDate());
                 shareStarttime.setText(toProgressItem.getStartTime());
                 shareLocation.setText(toProgressItem.getLocation());
                 shareMemo.setText(toProgressItem.getMemo());

                if(!toProgressItem.getComplete()){
                    Log.i(TAG,  ">>>>>>>>>>>>>>>>>>>>>           123");

                    shareCheck.setVisibility(View.VISIBLE);
                    inContainer.setVisibility(View.GONE);
                    completeCheck.setVisibility(View.VISIBLE);

                } else {

                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>           234");
                    inContainer.setVisibility(View.VISIBLE);
                    shareCheck.setVisibility(View.GONE);
                    completeCheck.setVisibility(View.GONE);
                    shareText.setText("참여자");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        completeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                shareReference = database.getReference().child("groups").child(groupId).child("shareBucket").child(bucketKeyRegistered)
                .child("complete");
                wishListReference = database.getReference().child("groups").child(groupId)
                        .child("wishList").child(wishListKey).child("color");
                if(isChecked){
                    shareReference.setValue(true);
                    wishListReference.setValue(2);
                } else{

                    shareReference.setValue(false);
                    wishListReference.setValue(1);
                }
            }
        });
        shareCheck.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                shareReference = database.getReference().child("groups").child(groupId).child("sharePart").child(bucketKeyRegistered)
                .child(currentUser.getUid());


               if(shareCheck.isChecked()){
                   shareReference.setValue(curUser);
               } else{
                   shareReference.child("userNicname").setValue("");
               }
            }
        });

    }
    @OnClick(R.id.share_back)
    void onBackClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(ShareContentDialogFragment.this).commit();
        fragmentManager.popBackStack();
    }
    @OnClick(R.id.share_particishow)
    void onParticiShowClick(){
        final PopupMenu popupMenu = new PopupMenu(getContext(), sharePartici);
        final Menu menu = popupMenu.getMenu();
        shareReference = database.getReference().child("groups").child(groupId).child("sharePart").child(bucketKeyRegistered);
        shareReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    User user = child.next().getValue(User.class);
                    if(!user.getUserNicname().equals("")) {
                        Log.i(TAG, ">>>>>>>>>>>   " + user.getId());
                        menu.add(curUser.getUserNicname());
                    }
                    // menu id는 user id
                }
                popupMenu.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}