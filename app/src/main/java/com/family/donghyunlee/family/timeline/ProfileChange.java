package com.family.donghyunlee.family.timeline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.PhotoSel;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-25.
 */

public class ProfileChange extends AppCompatActivity {
    private static final String TAG = ProfileChange.class.getSimpleName();
    private static final int REQUESTCODE_PHOTOSEL_PROFILEFRAGMENT = 55;
    @BindView(R.id.profilechagne_image)
    ImageView profileImage;
    @BindView(R.id.profilechange_nickname)
    EditText profileNicname;
    @BindView(R.id.profilechange_state)
    EditText profileState;

    private String groupId;

    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private DatabaseReference changeReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;

    private String storageProfileFolder;
    private User curUser;
    private String userId;
    private SharedPreferences pref;
    private Uri uri;
    private String filename;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilechange);
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
        uri = null;

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
                        profileNicname.setText(curUser.getUserNicname());
                        return;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    @OnClick(R.id.profilechange_camera)
    void onCameraClick(){
        Intent intent = new Intent(ProfileChange.this, PhotoSel.class);
        //intent.putExtra("WHATCALL", "PROFILE");
        startActivityForResult(intent, REQUESTCODE_PHOTOSEL_PROFILEFRAGMENT);
    }

    @OnClick(R.id.profilechange_confirm)
    void onConfirmClick(){
        if(!validateForm())
            return;
        if(uri != null) {
            uploadFile(uri);
        }
        changeReference = database.getReference().child("users").child(currentUser.getUid());
        changeReference.child("userNicname").setValue(profileNicname.getText().toString());
        changeReference.child("userState").setValue(profileState.getText().toString());


        changeReference = database.getReference().child("groups").child(groupId).child("members")
                .child(currentUser.getUid());
        changeReference.child("userImage").setValue(profileState.getText().toString());
        changeReference.child("userImage").setValue(profileNicname.getText().toString());

        finish();
        overridePendingTransition(R.anim.y_step_in, R.anim.y_slide_out);

    }

    @OnClick(R.id.profilechange_back)
    void onBackClick(){
        finish();
        overridePendingTransition(R.anim.y_step_in, R.anim.y_slide_out);
    }
    @OnClick(R.id.profilechange_cancel)
    void onCancelClick(){
        finish();
        overridePendingTransition(R.anim.y_step_in, R.anim.y_slide_out);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESTCODE_PHOTOSEL_PROFILEFRAGMENT){
            if(resultCode == RESULT_OK) {
                uri = data.getParcelableExtra("IMAGE_URI");

                Glide.with(getApplicationContext()).load(uri).centerCrop().crossFade()
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(profileImage);
            }
        }
    }
    private void uploadFile(final Uri filePath) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(ProfileChange.this, SweetAlertDialog.PROGRESS_TYPE);
       if (filePath != null) {

            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(getResources().getString(R.string.date_format));
            Date now = new Date();
            filename = formatter.format(now) + getResources().getString(R.string.file_extension);
            StorageReference storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage))
                    .child(getResources().getString(R.string.storage_profiles_folder) + "/" + filename);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pDialog.hide();
                    changeReference = database.getReference().child("users").child(currentUser.getUid()).child("userImage");
                    changeReference.setValue(filename);
                    changeReference = database.getReference().child("groups").child(groupId).child("members")
                            .child(currentUser.getUid()).child("userImage");
                    changeReference.setValue(filename);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        } else {
            Log.d(TAG, ">>>>>>      filePath is null");
        }

    }
    private boolean validateForm() {
        boolean valid = true;

        String nickName = profileNicname.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            profileNicname.setError(getResources().getString(R.string.text_error));
            profileNicname.requestFocus();
            valid = false;
        }
        return valid;
    }
}
