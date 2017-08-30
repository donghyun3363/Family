package com.family.donghyunlee.family.timeline;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.PhotoSel;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.bucket.Bucket;
import com.family.donghyunlee.family.bucketpage.BucketListFragment;
import com.family.donghyunlee.family.data.MyBucketList;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.TimelineCountItem;
import com.family.donghyunlee.family.data.TitleItem;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.data.WishListRecyclerItem;
import com.family.donghyunlee.family.photoalbum.PhotoAlbum;
import com.family.donghyunlee.family.push.Push;
import com.family.donghyunlee.family.setting.Setting;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

/**
 * Created by DONGHYUNLEE on 2017-07-31.
 */

public class TimeLine extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, BucketListFragment.OnMyListener{

    private static final String TAG = TimeLine.class.getSimpleName();
    private static final Integer GET_BUCKITANSWER_TO_SERVER = 0;
    private static final int GET_PROFILE_TO_SERVER = 1;
    private static final int TIMELINE_TITLE_REQUESTCODE = 101;
    private static final int REQUEST_INVITE = 3;
    private static final int WANT_TO_COMMENT_COUNT = 102;

    private boolean isFristTime;
    private SharedPreferences pref;
    private String groupId;

    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private DatabaseReference userBucketReference;
    private DatabaseReference exceptuserBucketReference;
    private DatabaseReference userReference;
    private DatabaseReference titleReference;
    private DatabaseReference pushReference;
    private DatabaseReference bucketReference;
    private DatabaseReference wishlistReference;
    private DatabaseReference inviteReference;
    private DatabaseReference commentReference;
    private ArrayList<MyBucketList> elseItems;
    private MyBucketList myItem;
    private MyBucketList tempItem;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private LinearLayoutManager linearLayoutManager;
    private TimelineRecyclerAdapter recyclerAdapter;
    private ArrayList<TimeLineItem> timeline_items;
    private ArrayList<User> profile_items;
    private ValueEventListener mValueEventListener;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String storageTitleFolder;
    private StorageReference titlepathRef;
    private FragmentManager fragmentManager;
    private EditText inputTitle;
    private DatabaseReference isBucketReference;
    private String inviteGroupId;
    private User curUser;

    //  private IOverScrollDecor mVertOverScrollEffect;
    @BindView(R.id.timeline_title)
    TextView timelineTitle;
    @BindView(R.id.timeline_title_image)
    ImageView timelineTitleImage;
    @BindView(R.id.timeline_title_edit)
    ImageButton titleEdit;

    @BindView(R.id.rv_timeline)
    RecyclerView recyclerView;
    private Uri uri;

    @Override
    protected void onRestart() {
        super.onRestart();
        // commentcnt 갱신
        String recent_timeline_key = pref.getString("recent_timeline_key", "");
        final int recent_timeline_position = pref.getInt("recent_timeline_position", 0);
        commentReference = database.getReference().child("groups").child(groupId).child("timelineCard")
                .child(recent_timeline_key).child("timelineCountItem");

        commentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TimelineCountItem timelineCountItem = dataSnapshot.getValue(TimelineCountItem.class);
                recyclerAdapter.notifyItemChanged(recent_timeline_position + 1, timelineCountItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TitleItem titleItem = dataSnapshot.getValue(TitleItem.class);
                if(titleItem == null){
                    titleItem = new TitleItem("empty", "empty");
                    titleReference.setValue(titleItem);
                }
                if (titleItem.getTitle().equals("empty") && titleItem.getTitleImage().equals("empty")) {
                    timelineTitle.setText("We are Fam!");
                    timelineTitleImage.setImageResource(0);
                } else if (!titleItem.getTitleImage().equals("empty") && titleItem.getTitleImage().equals("empty")) {
                    timelineTitle.setText(titleItem.getTitle());
                    timelineTitleImage.setImageResource(0);
                } else if (titleItem.getTitleImage().equals("empty") && !titleItem.getTitleImage().equals("empty")) {
                    timelineTitle.setText("We are Fam!");
                    titleImageDownload(titleItem.getTitleImage());

                } else {
                    timelineTitle.setText(titleItem.getTitle());
                    titleImageDownload(titleItem.getTitleImage());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(TimeLine.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        titleReference.addValueEventListener(mValueEventListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // timeline child listener clean in rv adapter
        //recyclerAdapter.cleanupListener();
        // timeline title listener clean
        if (mValueEventListener != null) {
            titleReference.removeEventListener(mValueEventListener);
        }
    }

    @OnClick(R.id.bar_push)
    void pushClick() {
        Intent intent = new Intent(TimeLine.this, Push.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.step_back);
    }

    @OnClick(R.id.bar_setting)
    void settingClick() {
        Intent intent = new Intent(TimeLine.this, Setting.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.step_back);
    }

    @OnClick(R.id.bar_photo)
    void photoClick() {
        Intent intent = new Intent(TimeLine.this, PhotoAlbum.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.step_back);
    }

    @OnClick(R.id.bar_bucket)
    void bucketClick() {
        Intent intent = new Intent(TimeLine.this, Bucket.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.step_back);
    }

    @OnClick(R.id.timeline_title_edit)
    void onEditClick() {

        PopupMenu popupMenu = new PopupMenu(this, titleEdit);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.title_menu, menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_edit_title:
                        AlertDialog.Builder dialog = createDialog();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialog_edit_title, (ViewGroup) findViewById(R.id.popup_edit_root));
                        inputTitle = (EditText) layout.findViewById(R.id.dialog_title);
                        dialog.setView(layout);
                        dialog.show();
                        return true;
                    case R.id.menu_edit_titleimage:
                        Intent intent = new Intent(TimeLine.this, PhotoSel.class);
                        startActivityForResult(intent, TIMELINE_TITLE_REQUESTCODE);
                        return true;
                    case R.id.menu_edit_reset:
                        TitleItem titleItem = new TitleItem("empty", "empty");
                        titleReference.setValue(titleItem);
                        showMessage("타이틀 명과 사진을 기본값으로 설정하였습니다.");
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();

    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setInit();
        settingRecycler();
        setFCM();
        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.

    }

    private void setFCM() {

        FirebaseInstanceId.getInstance().getToken();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_Token", token);

    }

    void titleImageDownload(String filename) {

        String filepath = storageTitleFolder + "/" + filename;

        storageRef.child(filepath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(getApplicationContext()).load(uri)
                        .bitmapTransform(new ColorFilterTransformation(getApplicationContext(), Color.argb(80, 0, 0, 0)))
                        .crossFade().into(timelineTitleImage);
                //TODO 로딩
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case TIMELINE_TITLE_REQUESTCODE: {
                if (resultCode == RESULT_OK) {
                    uri = data.getParcelableExtra("IMAGE_URI");

                    fileUpload(uri);

                } else {
                    Log.e(TAG, ">>>>> " + "TIMELINE_TITLE_REQUESTCODE don't result ");
                }
                break;
            }
            case REQUEST_INVITE:
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

                break;
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {


        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setMessage("정말 종료하시겠습니까?");
        d.setPositiveButton("예", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // process전체 종료
                finish();
            }
        });
        d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        d.show();
    }
    private void showMessage(String msg) {
        ViewGroup container = (ViewGroup) findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void fileUpload(Uri filePath) {
        final String filename;
        if (filePath != null) {
            // 파일이름 생성.

            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(getResources().getString(R.string.date_format));
            Date now = new Date();
            filename = formatter.format(now) + getResources().getString(R.string.file_extension);

            StorageReference storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage))
                    .child(getResources().getString(R.string.storage_timeline_title_folder) + "/" + filename);
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 실시간 데이터베이스에 저장
                    titleReference.child("titleImage").setValue(filename);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplication(), getResources().getString(R.string.uploading_failed), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }
    }

    private AlertDialog.Builder createDialog() {
        AlertDialog.Builder insertDialog = new android.support.v7.app.AlertDialog.Builder(this);
        insertDialog.setTitle("타이틀 명 변경")
                .setMessage("가족 들만의 타이틀 명을 변경해보세요!")
                .setPositiveButton(R.string.dialog_posi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (inputTitle.length() <= 0) {
                            showMessage("변경이 되지 않았습니다. 입력정보를 다시 입력해주세요.");

                        } else {

                            titleReference.child("title").setValue(inputTitle.getText().toString());
                            showMessage( "타이틀 명이 변경되었습니다..");
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_negati, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        return insertDialog;
    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
        }
        fragmentManager = getSupportFragmentManager();
        timeline_items = new ArrayList<>();
        profile_items = new ArrayList<>();
        elseItems = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        userReference = database.getReference().child("users");
        groupReference = database.getReference().child("groups").child(groupId).child("members");
        titleReference = database.getReference().child("groups").child(groupId).child("title");

        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
        storageTitleFolder = getResources().getString(R.string.storage_timeline_title_folder);
        // 초대받은 초대 id 삭제


        // 버킷 리스트 입력 처음 인 사용자.
        isBucketReference = database.getReference("users").child(currentUser.getUid());
        isBucketReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curUser = dataSnapshot.getValue(User.class);
                if (!curUser.getIsBucket()) {
                    startFragment(BucketListFragment.newInstance());

                } else {
                    new AccessDatabaseTask().execute(GET_BUCKITANSWER_TO_SERVER);
                }

                return;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // 버킷리스트가 처음인지 체크.
        Intent intent = getIntent();
        isFristTime = (boolean) intent.getSerializableExtra("ISFIRSTTIME?");
        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>   isFirstTime" + isFristTime);
        userBucketReference = database.getReference().child("userBucketList");
        pushReference = database.getReference().child("groups").child(groupId).child("push");
        bucketReference = database.getReference().child("groups").child(groupId).child("buckets");
        if (isFristTime) {
            //push settting
            pushReference.setValue(currentUser.getUid());
            //bucket setting
            userBucketReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    while (child.hasNext()) {
                        String userId = child.next().getKey();
                        if (currentUser.getUid().equals(userId)) {
                            myItem = dataSnapshot.child(userId).child("myBucketList").getValue(MyBucketList.class);
                            bucketReference.child(currentUser.getUid()).child("myBucketList").setValue(myItem);

                            wishlistReference = database.getReference().child("groups").child(groupId)
                                    .child("wishList");
                            for (int i = 0; i < myItem.getAnswer().size(); i++) {
                                String wishListKey = wishlistReference.push().getKey();
                                WishListRecyclerItem item = new WishListRecyclerItem(wishListKey, myItem.getUserId(),
                                        curUser.getUserImage(), curUser.getUserNicname(), myItem.getDate(),
                                        myItem.getAnswer().get(i),  myItem.getQuestion().get(i), 0);

                                wishlistReference.child(wishListKey).setValue(item);
                            }


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

        new AccessDatabaseTask().execute(GET_PROFILE_TO_SERVER);
    }
    void startFragment(final BucketListFragment fragment) {
        Log.i(TAG, ">>>>>>> HERE");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.timeline_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        Log.i(TAG, ">>>>>>> HERE");

        //  fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onReceivedData(Boolean data) {
        if(data == true) {
            Log.i(TAG, ">>>>>>>>>>>>>          onReceivedData" + data);

            new AccessDatabaseTask().execute(GET_BUCKITANSWER_TO_SERVER);
        }
    }

    private void settingRecycler() {


        linearLayoutManager = new LinearLayoutManager(this);
        // 리사이클러뷰 setting
        recyclerAdapter = new TimelineRecyclerAdapter(TimeLine.this, this, timeline_items, profile_items, groupId, fragmentManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        // Overscroll Vertical
//        mVertOverScrollEffect = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerView));


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showMessage("onConnectionFailed");
    }

    public class AccessDatabaseTask extends AsyncTask<Integer, String, Long> {

        public Long result = null;


        private ArrayList<StorageReference> storageItems;
        private String storageProfileFolder;
        private DatabaseReference timelineCardReference;
        private DatabaseReference profileReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            storage = FirebaseStorage.getInstance();
            storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);

            timelineCardReference = database.getReference().child("groups").child(groupId).child("timelineCard");
            profileReference = database.getReference().child("groups").child(groupId).child("members");

        }

        @Override
        protected Long doInBackground(Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.
            if (params[0].intValue() == GET_BUCKITANSWER_TO_SERVER) {
                userBucketReference = database.getReference().child("userBucketList");
                userBucketReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while (child.hasNext()) {
                           // String userId = child.next().getValue(String.class);
                            String userId = child.next().getKey();
                            Log.i(TAG, ">>>>>>>>>>>>>>>> USERID : " + userId);
                            if (currentUser.getUid().equals(userId)) {
                                myItem = dataSnapshot.child(userId).child("myBucketList").getValue(MyBucketList.class);
                                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>    111" + myItem.getUserId());
                            } else {
                                tempItem = dataSnapshot.child(userId).child("myBucketList").getValue(MyBucketList.class);

                                if(tempItem == null){
                                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>   33333");
                                    continue;
                                }
                                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>    222" + tempItem.getUserId());
                                elseItems.add(tempItem);
                                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>    544444" + elseItems.size());
                            }
                        }
                        for (int i = 0; i < elseItems.size(); i++) {
                            exceptuserBucketReference = database.getReference().child("userBucketList").child(currentUser.getUid()).child("elseBucketList");
                            exceptuserBucketReference.child(elseItems.get(i).getUserId()).setValue(elseItems.get(i));
                            Log.i(TAG, ">>>>>      tempItem: " + elseItems.get(0).getUserId());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                return result;
            } else if (params[0].intValue() == GET_PROFILE_TO_SERVER) {
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>> IN TIMELINE:");
                profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while (child.hasNext()) {

                            User item = child.next().getValue(User.class);
                            profile_items.add(item);
                        }
                        recyclerAdapter.setProfileItem(profile_items);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long s) {
            super.onPostExecute(s);

            Log.i(TAG, ">>>>>> " + s + " <<");
            return;
        }

        @Override
        protected void onCancelled() {
            Log.i("TAG", ">>>>> doItBackground 취소");
            super.onCancelled();
        }
    }

}