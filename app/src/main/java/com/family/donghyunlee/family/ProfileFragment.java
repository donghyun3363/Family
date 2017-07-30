package com.family.donghyunlee.family;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-26.
 */

public class ProfileFragment extends Fragment{


    @BindView(R.id.profile_name)
    EditText profileName;
    @BindView(R.id.profile_phone)
    EditText profilePhone;
    @BindView(R.id.profile_type)
    EditText profileType;
    @BindView(R.id.profile_done)
    Button profileDone;
    @BindView(R.id.profile_image)
    ImageButton profileImage;
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private String email;
    private String password;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private User userData;
    // Fragment newInstance by adding Bundle Object
    public static ProfileFragment newInstance(String email, String password) {

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putString("email", email);
        args.putString("password", password);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        setInit();
        return v;
    }

    private void setInit() {
        setListener();
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        Glide.with(this).load(R.drawable.ic_profileblack).into(profileImage);
    }
    // EditText Listener Watcher Function (완료 및 다음 키 활성화)
    private void setListener(){
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( profileName.length() > 0 && profilePhone.length() > 0 && profileType.length() > 0){
                    profileDone.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                } else{
                    profileDone.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhGray));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        profileName.addTextChangedListener(watcher);
        profilePhone.addTextChangedListener(watcher);
        profileType.addTextChangedListener(watcher);
    }


    @OnClick(R.id.profile_done)
    void doneClick(){
        if(validateForm() == false)
            return;
        signUpUser();

    }

    private void signUpUser(){
        Log.d(TAG, "createAccount:" + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // TODO profile image 구현하기.
                            userData = new User(email, password, profileName.getText().toString()
                                    , profilePhone.getText().toString()
                                    , profileType.getText().toString(), 0);
                            databaseReference.child(userData.getId()).setValue(userData);
                            sendEmailVerification();
                            updateUI(user);
                            Toast.makeText(getContext(), "등록한 메일에서 인증 클릭을 해주세요.", Toast.LENGTH_SHORT).show();
                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                            updateUI(null);
                        }
                    }
                });
    }
    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(), "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

        } else{


        }
    }
    // 유효성 검사
    private boolean validateForm() {
        boolean valid = true;

        String name = profileName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            profileName.setError(getResources().getString(R.string.text_error));
            profileName.requestFocus();
            valid = false;
        }
        String phone = profilePhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            profilePhone.setError(getResources().getString(R.string.text_error));
            profilePhone.requestFocus();
            valid = false;
        }
        String type = profileType.getText().toString();
        if (TextUtils.isEmpty(type)) {
            profileType.setError(getResources().getString(R.string.text_error));
            profileType.requestFocus();
            valid = false;
        }
        return valid;
    }


    @OnClick(R.id.profile_back)
    void backClick(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(ProfileFragment.this).commit();
        fragmentManager.popBackStack();
    }
    @OnClick(R.id.profile_image)
    void profileClick(){

        Intent intent = new Intent(getActivity(), PhotoSel.class);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscribe
    public void FinishLoad(DataEvent dataEvent) {
// 이벤트가 발생한뒤 수행할 작업
        Bitmap photo = dataEvent.getPhoto();
        profileImage.setBackground(new ShapeDrawable(new OvalShape()));
        profileImage.setClipToOutline(true);
        profileImage.setImageBitmap(photo);

    }

}


