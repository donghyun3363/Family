package com.family.donghyunlee.family.bucketpage;

import android.content.Context;
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
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.data.WishListRecyclerItem;
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
    private DatabaseReference bucketlistReference;
    public static BucketListFragment newInstance() {
        BucketListFragment bucketAnswerFragment = new BucketListFragment();

        Log.i(TAG,">>>>>>>>>>>>>        in BucketList Fragment Instance");
        return bucketAnswerFragment;
    }

    public interface OnMyListener{
        void onReceivedData(Boolean data);

    }
    private OnMyListener mOnMyListener;

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity()!=null && getActivity() instanceof OnMyListener){
            mOnMyListener = (OnMyListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucketlist, container, false);
        ButterKnife.bind(this, v);
        //adminSetAnswer();
        Log.i(TAG,">>>>>>>>>>>>>        in BucketList Fragment");
        setInit();

        return v;
    }

    private void setInit() {

        items = new ArrayList();
        pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");
        database = FirebaseDatabase.getInstance();
        //TODO KEY 개발자 설정
        bucketList_key = "-KsT0Z05y9DvE0SN7LY0";

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
        if(mOnMyListener != null){
            mOnMyListener.onReceivedData(true);
        }
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
        private DatabaseReference userReference;
        private DatabaseReference userBucketReference;
        private DatabaseReference wishlistReference;
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

            bucketlistReference = database.getReference().child("bucketList").child(bucketList_key);
            currentUser = mAuth.getCurrentUser();

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
                // 밖에 넣기
                userBucketReference = database.getReference().child("userBucketList").child(currentUser.getUid()).child("myBucketList");
                final MyBucketList myBucketList = new MyBucketList(currentUser.getUid(), CurDateFormat.format(date), sendAnswer, sendQuestion, 0);
                userBucketReference.setValue(myBucketList);
                userReference = database.getReference().child("users").child(currentUser.getUid()).child("isBucket");
                userReference.setValue(true); // 1이 있는 것을 말함.

                userReference = database.getReference().child("users").child(currentUser.getUid());
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String key;
                        if(!user.getGroupId().equals("empty")){
                            userBucketReference = database.getReference().child("groups").child(groupId).child("buckets")
                                    .child(currentUser.getUid()).child("myBucketList");
                            userBucketReference.setValue(myBucketList);


                            wishlistReference = database.getReference().child("groups").child(groupId)
                                    .child("wishList");
                            for (int i = 0; i < answerData.length; i++) {
                                //int imgProfilePath, String nickName, String date, String question, String answer
                                key = wishlistReference.push().getKey();
                                WishListRecyclerItem item = new WishListRecyclerItem(key, user.getId(),
                                        user.getUserImage(), user.getUserNicname(), myBucketList.getDate(),
                                        sendQuestion.get(i), sendAnswer.get(i), 0);

                                wishlistReference.child(key).setValue(item);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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