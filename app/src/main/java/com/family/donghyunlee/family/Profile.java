package com.family.donghyunlee.family;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.timeline.ProfileChange;
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

/**
 * Created by DONGHYUNLEE on 2017-08-24.
 */

public class Profile extends AppCompatActivity {

    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.profile_phone)
    TextView profilePhone;
    @BindView(R.id.profile_type)
    TextView profileType;

    private String groupId;

    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;

    private String storageProfileFolder;
    private User curUser;
    private String userId;
    private SharedPreferences pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setInit();
    }

    private void setInit() {

        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_b));
        }
        // userId 받아야함.
        Intent intent = getIntent();
        userId = (String) intent.getStringExtra("userId");

        Log.i("TAG", ">>>>>>>>>>>>>>>>>>>>>> USERID: " + userId);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        groupReference = database.getReference().child("groups").child(groupId).child("members");
        currentUser = mAuth.getCurrentUser();


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
        storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);


        groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    curUser = child.next().getValue(User.class);
                    if(curUser.getId().equals(userId)){
                        profile_pathRef = storageRef.child(storageProfileFolder + "/" + curUser.getUserImage());
                        Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                                .crossFade().bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                .into(profileImage);
                        profileName.setText(curUser.getUserNicname());
                        profileType.setText(curUser.getUserType());
                        profilePhone.setText(curUser.getUserPhone());
                        return;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    @OnClick(R.id.profile_setting)
    void onSettingClick(){
        Intent intent = new Intent(this, ProfileChange.class);
        intent.putExtra("userId", curUser.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.y_slide_in, R.anim.y_step_back);
    }

    @OnClick(R.id.profile_bucket)
    void onBucketClick(){
        Toast.makeText(this, "현재 작업중인 서비스 입니다.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.profile_back)
    void onBackClick(){
        finish();
        overridePendingTransition(R.anim.y_step_in, R.anim.y_slide_out);
    }
}
