package com.family.donghyunlee.family.bucketpage;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.BucketListRecyclerItem;
import com.family.donghyunlee.family.data.MakeBucketList;
import com.family.donghyunlee.family.data.MyBucketList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */

public class BucketListFragment extends Fragment {

    private static final Integer GETBUCKETLISTINFO = 0;
    private static final Integer SENDBUCKETLIST = 1;
    @BindView(R.id.rv_bucketlist)
    RecyclerView recyclerView;

    private static final String TAG = BucketListFragment.class.getSimpleName();
    private BucketListRecyclerAdapter recyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<BucketListRecyclerItem> items;
    private FirebaseDatabase database;
    private SharedPreferences pref;
    private String groupId;
    private String bucketList_key;

    public static BucketListFragment newInstance() {
        BucketListFragment bucketAnswerFragment = new BucketListFragment();
        return bucketAnswerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucketlist, container, false);
        ButterKnife.bind(this, v);
        //adminSetAnswer();
        setInit();

        return v;
    }

    private void setInit() {

        items = new ArrayList();
        pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        database = FirebaseDatabase.getInstance();

        new AccessDatabaseTask().execute(GETBUCKETLISTINFO);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new com.family.donghyunlee.family.bucketpage.BucketListRecyclerAdapter(getActivity(), items, R.layout.fragment_bucketlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @OnClick(R.id.bucketanswer_done)
    void onDoneClick() {
        Log.i(TAG,">>>>> Click done");
        new AccessDatabaseTask().execute(SENDBUCKETLIST);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(BucketListFragment.this).commit();
        fragmentManager.popBackStack();

    }

    @OnClick(R.id.bucketanswer_cancel)
    void onCancelClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(BucketListFragment.this).commit();
        fragmentManager.popBackStack();
    }


    public class AccessDatabaseTask extends AsyncTask<Integer, Integer, Long> {

        public Long result = null;
        private DatabaseReference bucketlistReference;
        private DatabaseReference userReference;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;
        private String[] answerData;
        private String[] questionData;
        private String key;
        private long now;
        private Date date;
        private SimpleDateFormat CurDateFormat;
        private BucketListRecyclerItem bucketListRecyclerItem;
        List<String> getAnswer;
        List<String> getAnswerHint;
        List<String> sendAnswer;
        List<String> sendQuestion;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAuth = FirebaseAuth.getInstance();
            bucketList_key = getResources().getString(R.string.bucketlist_key);
            bucketlistReference = database.getReference().child("bucketList").child(bucketList_key);
            currentUser = mAuth.getCurrentUser();
            userReference = database.getReference().child("users").child(currentUser.getUid()).child("myBucketAnswer");
            key = userReference.push().getKey();
            userReference.setValue(key);
            getAnswer = new ArrayList<String>();
            getAnswerHint = new ArrayList<String>();
            sendQuestion = new ArrayList<>();
            sendAnswer = new ArrayList<>();
            now = System.currentTimeMillis();
            date = new Date(now);
            CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        }

        @Override
        protected Long doInBackground(Integer... params) {

            if (params[0].intValue() == GETBUCKETLISTINFO) {
                bucketlistReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        MakeBucketList makeBucketList = dataSnapshot.getValue(MakeBucketList.class);
                        for (int i = 0; i < makeBucketList.getAnswer().size(); i++) {
                            getAnswer.add(makeBucketList.getAnswer().get(i));
                            getAnswerHint.add(makeBucketList.getAnswerhint().get(i));
                        }
                        for (int i = 0; i < getAnswer.size(); i++) {
                            bucketListRecyclerItem = new BucketListRecyclerItem(getAnswer.get(i), getAnswerHint.get(i));
                            items.add(bucketListRecyclerItem);
                        }
                        recyclerAdapter.setItems_size(getAnswer.size());
                        recyclerAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return result;
            } else if (params[0].intValue() == SENDBUCKETLIST) {

                answerData = recyclerAdapter.getAnswer();
                questionData = recyclerAdapter.getQuestion();
                for(int i = 0 ; i < answerData.length ; i++){
                    sendAnswer.add(answerData[i]);
                    sendQuestion.add(questionData[i]);
                }
                MyBucketList myBucketList = new MyBucketList(currentUser.getUid(), CurDateFormat.format(date), sendAnswer, sendQuestion);
                userReference.setValue(myBucketList);
                userReference = database.getReference().child("users").child(currentUser.getUid()).child("isBucket");
                userReference.setValue(1); // 1이 있는 것을 말함.
                return result;
            }

            if (this.isCancelled()) {
                return null;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled() {
            Log.i("TAG", ">>>>> doItBackground 취소");
            super.onCancelled();
        }


    }

}