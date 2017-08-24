package com.family.donghyunlee.family.bucket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;
import com.family.donghyunlee.family.data.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class RegisterToProgress extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.toprogress_profile)
    ImageView toprogressProfile;
    @BindView(R.id.toprogress_question)
    TextView toprogressQuestion;
    @BindView(R.id.toprogress_answer)
    TextView toprogressAnswer;
    @BindView(R.id.toprogress_title)
    EditText toprogressTitle;
    @BindView(R.id.toprogress_location)
    EditText toprogressLocation;
    @BindView(R.id.toprogress_map)
    ImageButton toprogressMap;
    @BindView(R.id.toprogress_startdate)
    TextView toprogressStartDate;
    @BindView(R.id.toprogress_starttime)
    TextView toprogressStartTime;
    @BindView(R.id.toprogress_enddate)
    TextView toprogressEndDate;
    @BindView(R.id.toprogress_endtime)
    TextView toprogressEndTime;
    @BindView(R.id.toprogress_shareswitch)
    Switch toprogressShareSwitch;
    @BindView(R.id.toprogress_withswitch)
    Switch toprogressWithSwitch;
    @BindView(R.id.toprogress_memo)
    EditText toprogressMemo;
    @BindView(R.id.toprogress_cancel)
    TextView toprogressCancel;
    @BindView(R.id.toprogress_done)
    TextView toprogressDone;

    private boolean STARTDATECLICK_flag = false;
    private boolean ENDDATECLICK_flag = false;
    private boolean STARTTIMECLICK_flag = false;
    private boolean ENDTIMECLICK_flag = false;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int INDIVIDUALTOSERVER= 1;
    private static final int SHARETOSERVER= 0;
    private static final String TAG = RegisterToProgress.class.getSimpleName();
    private SharedPreferences pref;
    private String groupId;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference pathRef;
    private String storageProfileFolder;
    private String imgProfile;
    private String question;
    private String answer;
    private int item_position;
    private long now;
    private Date date;
    private SimpleDateFormat CurDateFormat;

    @OnClick(R.id.toprogress_map)
    void onMapClick(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @OnClick(R.id.toprogress_startdate)
    void onStartDateClick(){
        STARTDATECLICK_flag = true;
        ENDDATECLICK_flag = false;
        getDatePicker();
    }
    @OnClick(R.id.toprogress_enddate)
    void onEndDateClick(){
        STARTDATECLICK_flag = false;
        ENDDATECLICK_flag = true;
        getDatePicker();
    }
    @OnClick(R.id.toprogress_starttime)
    void onStartTimeClick(){
        STARTTIMECLICK_flag = true;
        ENDTIMECLICK_flag = false;
        getTimePicker();
    }
    @OnClick(R.id.toprogress_endtime)
    void onEndTimeClick(){
        STARTTIMECLICK_flag = false;
        ENDTIMECLICK_flag = true;
        getTimePicker();
    }
    @OnClick(R.id.toprogress_done)
    void onDoneClick(){
        // Bucket 액티비티에 보낼 번들
        Intent sendIntent = new Intent(this, Bucket.class);
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", item_position);
        sendIntent.putExtras(bundle);
        setResult(RESULT_OK, sendIntent);
        if(!validateForm()){
            return;
        }
        if(toprogressShareSwitch.isChecked()){
            new RegisterToProgress.AccessDatabaseTask().execute(SHARETOSERVER);
        }else{
            new RegisterToProgress.AccessDatabaseTask().execute(INDIVIDUALTOSERVER);
        }
        finish();
    }
    @OnClick(R.id.toprogress_cancel)
    void onCancelClick(){

        finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registertoprogress);
        ButterKnife.bind(this);
        setInit();

        Glide.with(getBaseContext()).using(new FirebaseImageLoader()).load(pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(getBaseContext())).into(toprogressProfile);
        toprogressQuestion.setText(question);
        toprogressAnswer.setText(answer);

    }

    private void setInit() {
        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색상 변경
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color_dark_c));
        }

        Intent intent = getIntent();
        imgProfile =(String) intent.getSerializableExtra("IMGPROFILE");
        question = (String) intent.getSerializableExtra("QUESTION");
        answer = (String) intent.getSerializableExtra("ANSWER");
        item_position = (int) intent.getSerializableExtra("POSITION");
        storage = FirebaseStorage.getInstance();
        storageProfileFolder = getResources().getString(R.string.storage_profiles_folder);
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebase_storage));
        pathRef = storageRef.child(storageProfileFolder + "/" + imgProfile);
        now = System.currentTimeMillis();
        date = new Date(now);
        CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 EEEE", Locale.getDefault());
        toprogressStartDate.setText(CurDateFormat.format(date));
        CurDateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        toprogressStartTime.setText(CurDateFormat.format(date));
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        toprogressShareSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   toprogressWithSwitch.setEnabled(true);
               } else{
                   toprogressWithSwitch.setEnabled(false);
               }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                toprogressLocation.setText(place.getName());
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    private void getDatePicker(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }
    private void getTimePicker(){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }
    private boolean validateForm() {
        boolean valid = true;

        String email = toprogressTitle.getText().toString();
        if (TextUtils.isEmpty(email)) {
            toprogressTitle.setError(getResources().getString(R.string.text_error));
            toprogressTitle.requestFocus();
            valid = false;
        }
        String password = toprogressLocation.getText().toString();
        if (TextUtils.isEmpty(password)) {
            toprogressLocation.setError(getResources().getString(R.string.text_error));
            toprogressLocation.requestFocus();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        date = new Date(year, monthOfYear, dayOfMonth-1);
        String dayOfWeek = simpledateformat.format(date);
        String getDate = +year+"년 "+(monthOfYear+1)+"월 "+ dayOfMonth+"일 ";

        if(STARTDATECLICK_flag){

            toprogressStartDate.setText(getDate + dayOfWeek);
        }
        if(ENDDATECLICK_flag){

            toprogressEndDate.setText(getDate + dayOfWeek);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = hourOfDay+":"+minute;
        Toast.makeText(this, time, Toast.LENGTH_SHORT).show();
    }
    public class AccessDatabaseTask extends AsyncTask<Integer, String, Long> {
        private FirebaseDatabase database;
        public Long result = null;
        private DatabaseReference shareReference;
        private DatabaseReference individualReference;
        private DatabaseReference userReference;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;
        private String key;
        private ToProgressItem toProgressItem;
        private User curUser;

        private long now;
        private Date date;
        private SimpleDateFormat CurDateFormat;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            currentUser = mAuth.getCurrentUser();
            userReference = database.getReference().child("groups").child(groupId).child("members");
            shareReference = database.getReference().child("groups").child(groupId).child("shareBucket");
            individualReference = database.getReference().child("groups").child(groupId).child("members")
                    .child(currentUser.getUid()).child("individualBucket");
            now = System.currentTimeMillis();
            date = new Date(now);
            CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
            //String userId, String profilePath, String nickName, String date
            // String title, String location, String startDate, String endDate, String startTime,
            // String endTime, String memo, boolean shareCheck, boolean withCheck)

            toProgressItem = new ToProgressItem(null, null, null
                    , CurDateFormat.format(date),toprogressTitle.getText().toString(), toprogressLocation.getText().toString()
                    , toprogressStartDate.getText().toString(), toprogressEndDate.getText().toString()
                    , toprogressStartTime.getText().toString(), toprogressEndTime.getText().toString()
                    , toprogressMemo.getText().toString(), toprogressShareSwitch.isChecked(), toprogressWithSwitch.isChecked());
        }

        @Override
        protected Long doInBackground(final Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    while(child.hasNext()){
                        User user = child.next().getValue(User.class);
                        if(currentUser.getUid().equals(user.getId())){
                            curUser = user;
                            toProgressItem.setUserId(curUser.getId());
                            toProgressItem.setProfilePath(curUser.getUserImage());
                            toProgressItem.setNickName(curUser.getUserNicname());
                            break;
                        }
                    }
                    if (params[0].intValue() == SHARETOSERVER) {
                        key = shareReference.push().getKey();
                        shareReference.child(key).setValue(toProgressItem);
                        return;
                    }
                    else if(params[0].intValue() == INDIVIDUALTOSERVER){
                        key = individualReference.push().getKey();
                        individualReference.child(key).setValue(toProgressItem);
                        return;
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            if (this.isCancelled()) {
                return null;
            }

            return null;
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
