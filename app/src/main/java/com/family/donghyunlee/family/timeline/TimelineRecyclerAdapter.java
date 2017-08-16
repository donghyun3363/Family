package com.family.donghyunlee.family.timeline;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-15.
 */
public class TimelineRecyclerAdapter extends RecyclerView.Adapter<TimelineRecyclerAdapter.TimelineViewHolder> {

    private static final String TAG = TimelineRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<TimeLineItem> items;
    private LayoutInflater mInflater;
    private ArrayList<User> profile_items;

    // ViewHolder Type
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;
    private StorageReference timeline_pathRef;
    private String storageProfileFolder;
    private String storageTimelineFolder;

    ProfileRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private ChildEventListener mChildEventListener;
    private DatabaseReference timelineReference;

    private String groupId;
    private List<String> mTimelineIds;

    public TimelineRecyclerAdapter(Context context, final ArrayList<TimeLineItem> items, ArrayList<User> profile_items, String groupId) {

        this.mContext = context;
        this.items = items;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.profile_items = profile_items;
        this.groupId = groupId;

        database = FirebaseDatabase.getInstance();
        timelineReference = database.getReference().child("groups").child(groupId).child("timelineCard");
        mTimelineIds = new ArrayList<>();

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                mTimelineIds.add(dataSnapshot.getKey());
                TimeLineItem timeLineItem = dataSnapshot.getValue(TimeLineItem.class);
                items.add(0, timeLineItem);
                notifyDataSetChanged();
                //notifyItemInserted(items.size() - 1); ??
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                TimeLineItem newTimeLineItem = dataSnapshot.getValue(TimeLineItem.class);
                String timelineCardKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int timelineIndex = mTimelineIds.indexOf(timelineCardKey);
                if (timelineIndex > -1) {

                    items.set(timelineIndex, newTimeLineItem);

                    notifyItemChanged(timelineIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + timelineCardKey);
                }
                // [END_EXCLUDE]

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String timelineCardKey = dataSnapshot.getKey();
                // [START_EXCLUDE]
                int timelineIndex = mTimelineIds.indexOf(timelineCardKey);
                if (timelineIndex > -1) {
                    // Remove data from the list
                    mTimelineIds.remove(timelineIndex);
                    items.remove(timelineIndex);
                    // Update the RecyclerView
                    notifyItemRemoved(timelineIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + timelineCardKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        timelineReference.addChildEventListener(mChildEventListener);
    }

    public class TimelineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // holder 의 viewType
        int mViewType;
        // body 의 view components
        TextView timelineNickname;
        TextView timelineDate;
        ImageView timelineProfileImage;
        TextView timelineContent;
        TextView timelineCommentCnt;
        TextView timelineLikeCnt;
        ImageView timelineContentImage;

        // Header 의 view components
        RecyclerView rv_profile;
        TextView tlHeaderContainer;
        ImageButton tlHeaderAddimage;

        // viewType 에 따른 viewHolder 정의
        public TimelineViewHolder(View itemView, int viewType) {
            super(itemView);
            storage = FirebaseStorage.getInstance();
            storageProfileFolder = mContext.getResources().getString(R.string.storage_profiles_folder);
            storageTimelineFolder = mContext.getResources().getString(R.string.storage_timeline_folder);
            storageRef = storage.getReferenceFromUrl(mContext.getResources().getString(R.string.firebase_storage));
            mViewType = viewType;

            if (TYPE_HEADER == viewType) {
                rv_profile = (RecyclerView) itemView.findViewById(R.id.rv_profile);
                tlHeaderContainer = (TextView) itemView.findViewById(R.id.tl_header_container);
                tlHeaderContainer.setOnClickListener(this);
                tlHeaderAddimage = (ImageButton) itemView.findViewById(R.id.tl_header_addimage);
                tlHeaderAddimage.setOnClickListener(this);

            }
            if (TYPE_FOOTER == viewType) {

            } else {
                timelineNickname = (TextView) itemView.findViewById(R.id.timeline_nickname);
                timelineDate = (TextView) itemView.findViewById(R.id.timeline_date);
                timelineProfileImage = (ImageView) itemView.findViewById(R.id.timeline_profile);
                timelineContent = (TextView) itemView.findViewById(R.id.timeline_content);
                timelineCommentCnt = (TextView) itemView.findViewById(R.id.timline_comment_cnt);
                timelineLikeCnt = (TextView) itemView.findViewById(R.id.timline_like_cnt);
                timelineContentImage = (ImageView) itemView.findViewById(R.id.timeline_contentimage);
            }

        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {

                case R.id.tl_header_container:

                    intent = new Intent(mContext, TimelineWrite.class);
                    intent.putExtra("FLAG", 0);
                    mContext.startActivity(intent);
                    break;

                case R.id.tl_header_addimage:

                    intent = new Intent(mContext, TimelineWrite.class);
                    intent.putExtra("FLAG", 1);
                    mContext.startActivity(intent);

                    break;
            }
        }
    }
    // header 여부 체크 메소드
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    // header 여부 체크 메소드
    private boolean isPositionFooter(int position) {
        return position == items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else
            return TYPE_ITEM;
    }
    // header, body 의 view 정의
    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {

            View headerView = mInflater.inflate(R.layout.timeline_header_item, parent, false);
            return new TimelineViewHolder(headerView, viewType);
        } else if (viewType == TYPE_FOOTER) {
            View footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_footer_item, parent, false);
            return new TimelineViewHolder(footerView, viewType);
        } else {

            View itemView = mInflater.inflate(R.layout.timeline_body_item, parent, false);
            return new TimelineViewHolder(itemView, viewType);
        }
    }

    // header, body 에 data bind
    @Override
    public void onBindViewHolder(TimelineViewHolder holder, int position) {
        // 1. header data bind
        if (holder.mViewType == TYPE_HEADER) {
            bindHeaderItem(holder);
        } else if (holder.mViewType == TYPE_FOOTER) {

        }
        // 2. body data bind
        else {
            bindBodyItem(holder, position - 1);
        }
    }

    // head itemSet bind
    private void bindHeaderItem(TimelineViewHolder holder) {
        adapter = new ProfileRecyclerAdapter(mContext, profile_items, groupId);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.rv_profile.setLayoutManager(layoutManager);
        holder.rv_profile.setAdapter(adapter);
        // Layout Manager 를 이용해 Horizontal Option 을 적용


    }
    // body itemSet bind
    private void bindBodyItem(TimelineViewHolder holder, int position) {

        // text data bind
        holder.timelineNickname.setText(items.get(position).getTimeline_nickName());
        holder.timelineDate.setText(items.get(position).getTimeline_date());
        holder.timelineContent.setText(items.get(position).getTimeline_content());
        holder.timelineCommentCnt.setText("" + items.get(position).getTimeline_commend_cnt());
        holder.timelineLikeCnt.setText("" + items.get(position).getTimeline_like_cnt());
        profile_pathRef = storageRef.child(storageProfileFolder + "/" + items.get(position).getTimeline_profileImage());
        timeline_pathRef = storageRef.child(storageTimelineFolder + "/" + items.get(position).getTimeline_contentImage());
        // image data bind
        Glide.with(mContext).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(mContext)).into(holder.timelineProfileImage);

        if (items.get(position).getTimeline_contentImage().equals("empty") == false) {
            holder.timelineContentImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).using(new FirebaseImageLoader()).load(timeline_pathRef).centerCrop()
                    .crossFade().into(holder.timelineContentImage);
        } else {
            holder.timelineContentImage.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return items.size() + 2; // (body item count) + (header item count)
    }

    public void setProfileItem(ArrayList<User> profile_items) {
        this.profile_items = profile_items;
        //adapter.notifyDataSetChanged();

    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            timelineReference.removeEventListener(mChildEventListener);
        }
    }

}