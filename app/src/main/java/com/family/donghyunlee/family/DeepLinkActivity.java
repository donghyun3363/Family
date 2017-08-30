package com.family.donghyunlee.family;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.timeline.TimeLine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by DONGHYUNLEE on 2017-08-20.
 */

public class DeepLinkActivity extends AppCompatActivity {

    private static final String TAG = DeepLinkActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference inviteReference;
    private DatabaseReference userReference;
    private DatabaseReference groupReference;
    private SharedPreferences pref;
    private String invitationId;
    private String groupId;
    private User curUser;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deeplink);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        // Button click listener
        final SweetAlertDialog pDialog = new SweetAlertDialog(DeepLinkActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) {
                            Log.d(TAG, "getInvitation: no data");
                            return;
                        }
                        // Extract invite
                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                        if (invite != null) {
                            invitationId = invite.getInvitationId();
                            Log.d(TAG, "invitationId:" + invitationId);
                            inviteReference = database.getReference().child("invite").child(invitationId);
                            inviteReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    groupId = dataSnapshot.getValue(String.class);

                                    Log.i(TAG, ">>>>>>>>>>>>>>>>>         GROUPID:" + groupId);
                                    pDialog.hide();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
            // Check for link in intent
            if (getIntent() != null && getIntent().getData() != null) {
                Uri data = getIntent().getData();
                Log.d(TAG, "data:" + data);
        }
    }
    @OnClick(R.id.invite_cancel)
    void onCancelClick(){
       finish();
    }
    @OnClick(R.id.invite_ok)
    void onOkClick(){
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {

        if (user == null) {
            editor.putString("groupId", groupId);
            editor.commit();
            Intent intent = new Intent(DeepLinkActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            userReference = database.getReference().child("users").child(currentUser.getUid());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    curUser = dataSnapshot.getValue(User.class);
                    userReference.child("groupId").setValue(groupId);
                    curUser.setGroupId(groupId);
                    groupReference = database.getReference().child("groups").child(groupId).child("members");
                    groupReference.child(curUser.getId()).setValue(curUser);
                    editor.putString("userId", currentUser.getUid());
                    editor.putString("groupId", groupId);
                    editor.commit();
                    Intent intent = new Intent(DeepLinkActivity.this, TimeLine.class);
                    intent.putExtra("ISFIRSTTIME?", true);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}
