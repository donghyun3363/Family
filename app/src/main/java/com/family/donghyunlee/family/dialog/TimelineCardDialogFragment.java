package com.family.donghyunlee.family.dialog;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.family.donghyunlee.family.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGHYUNLEE on 2017-08-20.
 */

public class TimelineCardDialogFragment extends DialogFragment {

    private static final String TAG = TimelineCardDialogFragment.class.getSimpleName();
    private FirebaseStorage storage;
    private StorageReference storageRef;
    StorageReference desertRef;
    private String storageTimelineFolder;

    private DatabaseReference deleteReference;

    private SharedPreferences pref;
    private String groupId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private String curTimelineKey;
    private String contentImage;
    public TimelineCardDialogFragment() {}

    public static TimelineCardDialogFragment newInstance(String curTimelineKey, String contentImage) {

        TimelineCardDialogFragment timelineCardDialogFragment = new TimelineCardDialogFragment();
        Bundle args = new Bundle();
        args.putString("curTimelineKey", curTimelineKey);
        args.putString("contentImage", contentImage);
        timelineCardDialogFragment.setArguments(args);
        return timelineCardDialogFragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_cardoption, container);
        ButterKnife.bind(this, v);
        setInit();

        return v;
    }

    private void setInit() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        // 번들로 부터 받는 데이터
        curTimelineKey = getArguments().getString("curTimelineKey");
        contentImage = getArguments().getString("contentImage");
        storage = FirebaseStorage.getInstance();
        storageTimelineFolder = getContext().getResources().getString(R.string.storage_timeline_folder);
        storageRef = storage.getReferenceFromUrl(getContext().getResources().getString(R.string.firebase_storage));
        desertRef = storageRef.child(storageTimelineFolder + "/");
    }

    @OnClick(R.id.option_delete)
    void onDeleteClick(){

        final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("정말 삭제하시겠습니까?")
                .setContentText("삭제 후 다시 복구 할 수 없습니다!")
                .setCancelText("취소")
                .setConfirmText("삭제")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {

                        deleteReference = database.getReference().child("groups").child(groupId);
                        final Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/timelineCard/" + curTimelineKey, null);
                        childUpdates.put("/commentCard/" + curTimelineKey, null);
                        childUpdates.put("/likes/" + "/commentCard/" + curTimelineKey, null);
                        childUpdates.put("/likes/" + "/timelineCard/" + curTimelineKey, null);
                        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>> curTimelineKey OF ADAPTER!: " + curTimelineKey);

                        if(!contentImage.equals("empty")){
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Loading");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            desertRef.child(contentImage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    deleteReference.updateChildren(childUpdates);
                                    pDialog.hide();
                                    sweetAlertDialog
                                            .setTitleText("삭제 완료")
                                            .setContentText("새로운 이야기들을 다시 업로드해보세요!")
                                            .setCancelText(null)
                                            .setConfirmText("확인")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, ">>>>>>>          저장소 content 이미지 삭제가 실패했습니다.");
                                    pDialog.hide();
                                }
                            });
                        } else{
                            deleteReference.updateChildren(childUpdates);
                            sweetAlertDialog
                                    .setTitleText("삭제 완료")
                                    .setContentText("새로운 이야기들을 다시 업로드해보세요!")
                                    .setCancelText(null)
                                    .setConfirmText("확인")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }


                    }
                })
                .show();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(TimelineCardDialogFragment.this).commit();
        fragmentManager.popBackStack();
    }


    @OnClick(R.id.option_modify)
    void onModifyClick(){
        Toast.makeText(getContext(), "수정 준비중", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(TimelineCardDialogFragment.this).commit();
        fragmentManager.popBackStack();
    }
    @OnClick(R.id.option_cancel)
    void onCancelClick(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(TimelineCardDialogFragment.this).commit();
        fragmentManager.popBackStack();
    }

}
