package com.family.donghyunlee.family;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.family.donghyunlee.family.data.BusProvider;
import com.family.donghyunlee.family.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DONGHYUNLEE on 2017-07-26.
 */

public class ProfileFragment extends Fragment {


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
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
    private static final int REQUESTCODE_PHOTOSEL_PROFILEFRAGMENT = 99;
    private String email;
    private String password;
    private Uri uri;
    private String filename;
    private ViewGroup snackContainer;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //  Root Ref
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
        snackContainer = (ViewGroup) v.findViewById(R.id.snackbar_layout);
        setInit();
        return v;
    }

    private void setInit() {
        setListener();
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        Glide.with(getContext()).load(R.drawable.ic_user).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(getContext())).into(profileImage);

    }

    // EditText Listener Watcher Function (완료 및 다음 키 활성화)
    private void setListener() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (profileName.length() > 0 && profilePhone.length() > 0 && profileType.length() > 0) {
                    profileDone.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                } else {
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

    private void signUpUser() {
        Log.d(TAG, "createAccount:" + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendEmailVerification();

                           // updateUI(user);

                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(getResources().getString(R.string.already_email_error))
                                        .setContentText("다시 한번 확인해주세요.")
                                        .setConfirmText("확인")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                getActivity().finish();
                                                //updateUI(null);
                                            }
                                        })
                                        .show();
                                Toast.makeText(getActivity().getApplication().getApplicationContext(),
                                        getResources().getString(R.string.already_email_error), Toast.LENGTH_SHORT).show();

                            } else {
                                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(getResources().getString(R.string.auth_failed))
                                        .setContentText("다시 한번 확인해주세요.")
                                        .setConfirmText("확인")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                getActivity().finish();
                                                //updateUI(null);
                                            }
                                        })
                                        .show();
                                Toast.makeText(getActivity().getApplication().getApplicationContext(),
                                        getResources().getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                            }

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


                            uploadFile(uri);

                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            showMessage( user.getEmail() + getResources().getString(R.string.sending_email_failed));
                            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(getResources().getString(R.string.auth_failed))
                                    .setContentText("다시 한번 확인해주세요.")
                                    .setConfirmText("확인")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            getActivity().finish();
                                            //updateUI(null);
                                        }
                                    })
                                    .show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
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

    @OnClick(R.id.profile_done)
    void doneClick() {
        if (validateForm() == false) {
            return;
        }
//        if(profileImage.getResources() == null) {
//            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("프로필 사진을 추가해주세요!")
//                    .setContentText("나중에 변경할 수 있습니다.")
//                    .setConfirmText("확인")
//                    .show();
//            return;
//        }
        // 회원가입
        signUpUser();
    }

    @OnClick(R.id.profile_back)
    void backClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(ProfileFragment.this).commit();
        fragmentManager.popBackStack();
    }

    @OnClick(R.id.profile_image)
    void profileClick() {
        //  Permission Check & Request Permission
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
        Intent intent = new Intent(getActivity(), PhotoSel.class);
        //intent.putExtra("WHATCALL", "PROFILE");
        startActivityForResult(intent, REQUESTCODE_PHOTOSEL_PROFILEFRAGMENT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESTCODE_PHOTOSEL_PROFILEFRAGMENT){
            if(resultCode == RESULT_OK) {
                uri = data.getParcelableExtra("IMAGE_URI");
                Glide.with(getContext()).load(uri).centerCrop().crossFade()
                        .bitmapTransform(new CropCircleTransformation(getContext())).into(profileImage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, ">>>>>리퀘스트 요청 Success     " + grantResults[0]);

                } else {
                    Log.d(TAG, ">>>>>리퀘스트 요청 Fail     " + grantResults[0]);
                }
                return;
            }
            // other case
        }
    }
    // TODO 어싱크나 서비스를 쓰자.
    private void uploadFile(Uri filePath) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        if (filePath == null){ // 프로필 사진을 선택 안한 경우
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + getResources().getResourcePackageName(R.drawable.ic_profileblack) + '/'
                    + getResources().getResourceTypeName(R.drawable.ic_profileblack) + '/'
                    + String.valueOf(R.drawable.ic_profileblack ));
            filePath = imageUri;
        }
        if (filePath != null) {

            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(getResources().getString(R.string.date_format));
            Date now = new Date();
            filename = formatter.format(now) + getResources().getString(R.string.file_extension);
            StorageReference storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage))
                    .child(getResources().getString(R.string.storage_profiles_folder) + "/" + filename);

            FirebaseUser user = mAuth.getCurrentUser();
            userData = new User(user.getUid(), email, password, profileName.getText().toString()
                    , profilePhone.getText().toString()
                    , profileType.getText().toString(), filename, "empty", false);
            databaseReference.child(userData.getId()).setValue(userData);

            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pDialog.hide();
                    showMessage( mAuth.getCurrentUser().getEmail() + getResources().getString(R.string.sending_email));

                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(profileName.getText().toString() + "님 회원가입을 축하드립니다. ")
                            .setContentText("가입 이메일 인증 후 로그인을 해주세요!")
                            .setConfirmText("확인")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    getActivity().finish();
                                }
                            })
                            .show();

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

        Snackbar.make(snackContainer, msg, Snackbar.LENGTH_SHORT).show();
    }
}


