package com.family.donghyunlee.family.bucket;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.family.donghyunlee.family.R;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DONGHYUNLEE on 2017-08-09.
 */

public class WishListFragment extends Fragment {

    private static final String TAG = WishListFragment.class.getSimpleName();
    @BindView(R.id.rv_wishlist)
    RecyclerView recyclerView;

    @BindView(R.id.bucket_avi)
    AVLoadingIndicatorView avLoadingIndicatorView;

    private static final int GETDATAFROMSERVER= 0;
    private static final int SETDATATOSERVER= 1;
    private ArrayList<WishListRecyclerItem> items;
    private LinearLayoutManager linearLayoutManager;
    private WishListRecyclerAdapter recyclerAdapter;
    private FirebaseDatabase database;
    private SharedPreferences pref;
    private String groupId;
    private List<String> answerList;
    private List<String> questionList;
    public WishListFragment() {
    }

    public static WishListFragment newInstance(){
        WishListFragment fragment = new WishListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wishlist, container, false);
        ButterKnife.bind(this,v);

        setInit();

        return v;
    }

    private void setInit() {
        items = new ArrayList<>();
        pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        groupId = pref.getString("groupId", "");

        database = FirebaseDatabase.getInstance();

        new WishListFragment.AccessDatabaseTask().execute(GETDATAFROMSERVER);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new WishListRecyclerAdapter(getContext(), items, R.layout.fragment_wishlist, groupId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
    public class AccessDatabaseTask extends AsyncTask<Integer, String, Long> {

        public Long result = null;
        private DatabaseReference groupReference;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;
        private MyBucketList myBucketList;
        private WishListRecyclerItem item;
        private FirebaseStorage storage;
        private StorageReference storageRef;
        private StorageReference pathRef;
        private ArrayList<StorageReference> storageItems;
        private String storageProfileFolder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            groupReference = database.getReference().child("groups").child(groupId).child("members");
            items = new ArrayList<>();
            storage = FirebaseStorage.getInstance();
            storageProfileFolder = getContext().getString(R.string.storage_profiles_folder);
            storageRef = storage.getReferenceFromUrl(getContext().getString(R.string.firebase_storage));
            storageItems = new ArrayList<>();
            avLoadingIndicatorView.show();
        }

        @Override
        protected Long doInBackground(Integer... params) {
            // members user id를 가져와서 user에 대한 qeustion과 answer을 가져온다.
            if (params[0].intValue() == GETDATAFROMSERVER) {
                groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while(child.hasNext()){
                            User user = child.next().getValue(User.class);
                            if(!user.getId().equals(currentUser.getUid())){

                                myBucketList = child.next().child("myBucketAnswer").getValue(MyBucketList.class);
                                questionList = myBucketList.getQuestion();
                                answerList = myBucketList.getAnswer();
                                //int imgProfilePath, String nickName, String date, String question, String answer
                                for(int i = 0 ; i < questionList.size() ; i++){
                                    item = new WishListRecyclerItem(user.getUserImage(), user.getUserNicname(), myBucketList.getDate(),
                                            questionList.get(i), answerList.get(i));
                                    pathRef = storageRef.child(storageProfileFolder + "/" + user.getUserImage());
                                    storageItems.add(pathRef);
                                    items.add(item);
                                }
                                // 이미지 스토리지 접근
                            }
                        }
                        recyclerAdapter.setStorageitem(storageItems);
                        recyclerAdapter.notifyDataSetChanged();
                        return;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return result;
            }
//            else if (params[0].intValue() == SETDATAINTOSERVER)   {
//                List<String> answer = new ArrayList<>();
//                answerData = recyclerAdapter.getAnswer();
//                for(int i = 0 ; i < answerData.length ; i++){
//                    answer.add(answerData[i]);
//                }
//                userReference.setValue(answer);
//                return result;
//            }

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

            avLoadingIndicatorView.hide();
            return;
        }
        @Override
        protected void onCancelled() {
            Log.i("TAG", ">>>>> doItBackground 취소");
            super.onCancelled();
        }
    }
}
