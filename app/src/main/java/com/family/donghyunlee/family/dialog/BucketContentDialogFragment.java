package com.family.donghyunlee.family.dialog;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.bucket.RegisterToProgress;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGHYUNLEE on 2017-08-26.
 */

public class BucketContentDialogFragment extends DialogFragment {
    private static final int REQUESTCODE_BUCKETCONTENT = 104;
    @BindView(R.id.dialog_answer)
    TextView dialogAnswer;
    @BindView(R.id.dialog_question)
    TextView dialogQuestion;
    @BindView(R.id.dialog_nickname)
    TextView dialogNickname;
    @BindView(R.id.dialog_date)
    TextView dialogDate;
    @BindView(R.id.dialog_profile)
    ImageView dialogProfile;

    private static final String TAG = TimelineCardDialogFragment.class.getSimpleName();

    private SharedPreferences pref;
    private String groupId;

    private String nickName;
    private String profileImage;
    private String date;
    private String question;
    private String answer;
    private String wishListKey;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;
    private String storageProfileFolder;
    private int position;
    public BucketContentDialogFragment() {
    }

    public static BucketContentDialogFragment newInstance(String wishListKey, String nickName, String profileImage, String date
            , String question, String answer, int position) {

        BucketContentDialogFragment bucketContentDialogFragment = new BucketContentDialogFragment();
        Bundle args = new Bundle();
        args.putString("wishListKey", wishListKey);
        args.putString("nickName", nickName);
        args.putString("profileImage", profileImage);
        args.putString("date", date);
        args.putString("question", question);
        args.putString("answer", answer);
        args.putInt("position", position);
        bucketContentDialogFragment.setArguments(args);
        return bucketContentDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_bucketcontent, container);
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

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
        storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);

        // 번들로 부터 받는 데이터
        nickName = getArguments().getString("nickName");
        profileImage = getArguments().getString("profileImage");
        date = getArguments().getString("date");
        question = getArguments().getString("question");
        answer = getArguments().getString("answer");
        position = getArguments().getInt("position");
        wishListKey = getArguments().getString("wishListKey");
        dialogNickname.setText(nickName);
        dialogDate.setText(date);
        dialogAnswer.setText(answer);
        dialogQuestion.setText(question);
        profile_pathRef = storageRef.child(storageProfileFolder + "/" + profileImage);
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(getContext()))
                .into(dialogProfile);
    }


    @OnClick(R.id.dialog_back)
    void onBackClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(BucketContentDialogFragment.this).commit();
        fragmentManager.popBackStack();
    }
    @OnClick(R.id.dialog_share)
    void onShareClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(BucketContentDialogFragment.this).commit();
        fragmentManager.popBackStack();
        Intent intent = new Intent(getActivity(), RegisterToProgress.class);
        intent.putExtra("IMGPROFILE", profileImage);
        intent.putExtra("WISHLISTKEY", wishListKey);
        intent.putExtra("QUESTION", question);
        intent.putExtra("ANSWER", answer);
        intent.putExtra("POSITION", position);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUESTCODE_BUCKETCONTENT);
    }


}