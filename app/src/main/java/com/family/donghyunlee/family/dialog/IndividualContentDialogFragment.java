package com.family.donghyunlee.family.dialog;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGHYUNLEE on 2017-08-27.
 */

public class IndividualContentDialogFragment extends DialogFragment {

    @BindView(R.id.individual_profile)
    ImageView individualProfile;
    @BindView(R.id.individual_nickname)
    TextView individualNickname;
    @BindView(R.id.individual_date)
    TextView individualDate;
    @BindView(R.id.individual_title)
    TextView individualTitle;
    @BindView(R.id.individual_dump)
    TextView individualDump;
    @BindView(R.id.individual_startdate)
    TextView individualStartdate;
    @BindView(R.id.individual_starttime)
    TextView individualStarttime;
    @BindView(R.id.individual_enddate)
    TextView individualEnddate;
    @BindView(R.id.individual_endtime)
    TextView individualEndtime;
    @BindView(R.id.individual_location)
    TextView individualLocation;
    @BindView(R.id.individual_memo)
    TextView individualMemo;

    @BindView(R.id.individual_check)
    CheckBox individualCheck;
    @BindView(R.id.individual_alarm)
    TextView individualAlarm;

    @BindView(R.id.entire_container)
    RelativeLayout entireContainger;
    @BindView(R.id.in_container)
    LinearLayout inContainer;
    @BindView(R.id.complete_alarm)
    TextView completeAlarm;
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
    private DatabaseReference individualReference;
    private DatabaseReference wishListReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String wishListKey;
    public IndividualContentDialogFragment() {
    }

    public static IndividualContentDialogFragment newInstance(String wishListKey, Boolean share, String bucketKeyRegistered, int position) {

        IndividualContentDialogFragment individualContentDialogFragment = new IndividualContentDialogFragment();
        Bundle args = new Bundle();
        args.putString("wishListKey", wishListKey);
        args.putBoolean("share", share);
        args.putString("bucketKeyRegistered", bucketKeyRegistered);
        args.putInt("position", position);
        individualContentDialogFragment.setArguments(args);
        return individualContentDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_individualcontent, container);
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
        individualReference = database.getReference().child("groups").child(groupId)
                .child("individualBucket").child(bucketKeyRegistered);
        individualReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ToProgressItem toProgressItem = dataSnapshot.getValue(ToProgressItem.class);
                profile_pathRef = storageRef.child(storageProfileFolder + "/" + toProgressItem.getProfilePath());
                Glide.with(getContext()).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                        .crossFade().bitmapTransform(new CropCircleTransformation(getContext()))
                        .into(individualProfile);

                if(!currentUser.getUid().equals(toProgressItem.getUserId())){
                    individualAlarm.setVisibility(View.VISIBLE);
                    individualCheck.setVisibility(View.GONE);
                } else{
                    individualAlarm.setVisibility(View.GONE);
                    individualCheck.setVisibility(View.VISIBLE);
                }

                individualNickname.setText(toProgressItem.getNickName());
                individualDate.setText(toProgressItem.getDate());
                individualTitle.setText(toProgressItem.getTitle());
                if(toProgressItem.getEndDate() == null) {
                    individualDump.setText("");
                    individualEnddate.setText("");
                    individualEndtime.setText("");
                }
                else{
                    individualDump.setText("~");
                    individualEnddate.setText(toProgressItem.getEndDate());
                    individualEndtime.setText(toProgressItem.getEndTime());
                }
                individualStartdate.setText(toProgressItem.getStartDate());
                individualStarttime.setText(toProgressItem.getStartTime());
                individualLocation.setText(toProgressItem.getLocation());
                individualMemo.setText(toProgressItem.getMemo());

                if(!toProgressItem.getComplete()){
                    inContainer.setVisibility(View.INVISIBLE);
                    individualCheck.setVisibility(View.VISIBLE);
                } else{
                    inContainer.setVisibility(View.VISIBLE);
                    individualCheck.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        individualCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                individualReference = database.getReference().child("groups").child(groupId)
                        .child("individualBucket").child(bucketKeyRegistered).child("complete");
                wishListReference = database.getReference().child("groups").child(groupId)
                        .child("wishList").child(wishListKey).child("color");

                if(isChecked){
                    individualReference.setValue(true);
                    wishListReference.setValue(2);
                } else{

                    individualReference.setValue(false);
                    wishListReference.setValue(1);
                    individualCheck.setEnabled(false);

                }
            }
        });

    }
    @OnClick(R.id.individual_back)
    void onBackClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(IndividualContentDialogFragment.this).commit();
        fragmentManager.popBackStack();
    }

}