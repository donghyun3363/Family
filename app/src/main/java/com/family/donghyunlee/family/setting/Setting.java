package com.family.donghyunlee.family.setting;

/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.MainActivity;
import com.family.donghyunlee.family.Profile;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.timeline.ProfileChange;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.appinvite.AppInviteInvitation;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Date;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */

public class Setting extends AppCompatActivity {

    private static final int REQUESTCODE_PHOTOSEL_PROFILE_MODIFY = 101;
    private static final String TAG = Setting.class.getSimpleName();
    private static final int REQUEST_INVITE = 100;
    @BindView(R.id.setting_logout)
    TextView settingLogOut;
    @BindView(R.id.setting_profile)
    ImageView settingProfile;
    @BindView(R.id.setting_avi)
    AVLoadingIndicatorView image_avi;

    private String groupId;

    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private DatabaseReference changeReference;

    private DatabaseReference inviteReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;

    private String storageProfileFolder;
    private User curUser;
    private String filename;
    private String userId;
    private SharedPreferences pref;
    private Uri uri;

    private ViewGroup snackContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setInit();
        //image_avi.show();
    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_b));
        }
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
                    if(currentUser.getUid().equals(curUser.getId())){
                        profile_pathRef = storageRef.child(storageProfileFolder + "/" + curUser.getUserImage());
                        Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                                .crossFade().bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                .into(settingProfile);

                        //image_avi.hide();
                        return;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    @OnClick(R.id.setting_profile)
    void onProfileClick(){
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("userId", curUser.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.y_slide_in, R.anim.y_step_back);
    }
    @OnClick(R.id.setting_back)
    void backClick(){
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
    @OnClick(R.id.setting_modify)
    void onModifyClick(){
        Intent intent = new Intent(this, ProfileChange.class);
        intent.putExtra("userId", curUser.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.y_slide_in, R.anim.y_step_back);
    }
    @OnClick(R.id.setting_license)
    void onLicenseClick(){
        showMessage("현재 작업중인 서비스 입니다.");
    }
    @OnClick(R.id.setting_invite)
    void onInviteClick(){
        onInviteClicked();
    }
    @OnClick(R.id.setting_exit)
    void onExitClick(){
        showMessage("현재 작업중인 서비스 입니다.");
    }
    @OnClick(R.id.setting_logout)
    void onLogOutClick(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.step_in, R.anim.slide_out);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESTCODE_PHOTOSEL_PROFILE_MODIFY){
            if(resultCode == RESULT_OK) {
                uri = data.getParcelableExtra("IMAGE_URI");

                uploadFile(uri);
                Glide.with(getApplicationContext()).load(uri).centerCrop().crossFade()
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(settingProfile);
            }
        }
        if(requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                inviteReference = database.getReference().child("invite");
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                    inviteReference.setValue(id);
                    inviteReference.child(id).setValue(groupId);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                showMessage(getString(R.string.send_failed));
                // [END_EXCLUDE]
            }
        }
    }
    private void uploadFile(final Uri filePath) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(Setting.this, SweetAlertDialog.PROGRESS_TYPE);
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
                    showMessage("프로필 사진 업로드에 실패하였습니다.");
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



    private void showMessage(String msg) {
        ViewGroup snackContainer = (ViewGroup) findViewById(R.id.snackbar_layout);
        Snackbar.make(snackContainer, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void onInviteClicked() {

        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setEmailHtmlContent("<html><body>" +
                        "<h1>Fam</h1>" +
                        "<h2>회원가입 후에 가족의 공간을 즐겁게 만들어가세요! 시작해볼까요?</h2>" +
                        "<a href=\"%%APPINVITE_LINK_PLACEHOLDER%%\">지금 시작하기!</a>" +
                        "<body></html>")
                .setEmailSubject("FAM")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
}
