package com.family.donghyunlee.family.push;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.bucket.WishListRecyclerAdapter;
import com.family.donghyunlee.family.data.PushItem;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-27.
 */

public class PushRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = WishListRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<PushItem> items;
    private int item_layout;
    private String groupId;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;
    private String storageProfileFolder;

    private DatabaseReference pushReference;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private ChildEventListener mChildEventListener;

    private List<String> mPushIds;

    public PushRecyclerAdapter(Context context, final List<PushItem> items, int item_layout, final String groupId) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.groupId = groupId;

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageProfileFolder = context.getResources().getString(R.string.storage_profiles_folder);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(context.getResources().getString(R.string.firebase_storage));
        pushReference = database.getReference().child("groups").child(groupId).child("push").child(currentUser.getUid());

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                //mPushIds.add(0, dataSnapshot.getKey());
                final PushItem pushItem = dataSnapshot.getValue(PushItem.class);
                items.add(0, pushItem);
                notifyItemInserted(items.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        pushReference.addChildEventListener(mChildEventListener);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_push, null);
        return new ViewHolder(v);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.push_data)
        TextView pushData;
        @BindView(R.id.push_profile)
        ImageView pushProfile;
        @BindView(R.id.push_date)
        TextView pushDate;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) { // view position
        PushItem item = items.get(position);
        profile_pathRef = storageRef.child(storageProfileFolder + "/" + item.getPushProfileImage());
        Glide.with(context).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(((ViewHolder) holder).pushProfile);
        ((ViewHolder) holder).pushData.setText(item.getPushData());
        ((ViewHolder) holder).pushDate.setText(item.getPushDate());

    }


    public void cleanupListener() {
        if (mChildEventListener != null) {
            pushReference.removeEventListener(mChildEventListener);
        }
    }
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public PushItem getCurItem(int position) {
        PushItem item = items.get(position);
        return item;
    }
}