package com.family.donghyunlee.family.timeline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.IsCheck;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.data.TimelineCountItem;
import com.family.donghyunlee.family.data.User;
import com.family.donghyunlee.family.dialog.TimelineCardDialogFragment;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
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

    private DatabaseReference likeReference;
    private DatabaseReference likeUserReference;

    ProfileRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private ChildEventListener mChildEventListener;
    private DatabaseReference timelineReference;

    private String groupId;
    private List<String> mTimelineIds;
    //아이템 클릭시 실행 함수
    Activity activity;
    // like enable
    private int selectLike = 0;
    FragmentManager fragmentManager;
    public TimelineRecyclerAdapter(Activity activity, Context context, final ArrayList<TimeLineItem> items,
                                   ArrayList<User> profile_items, String groupId, FragmentManager fragmentManager) {
        this.activity = activity;
        this.mContext = context;
        this.items = items;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.profile_items = profile_items;
        this.groupId = groupId;
        this.fragmentManager = fragmentManager;
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        timelineReference = database.getReference().child("groups").child(groupId).child("timelineCard");
        mTimelineIds = new ArrayList<>();

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(mContext, "추가 + 길이" + items.size(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                mTimelineIds.add(0, dataSnapshot.getKey());
                TimeLineItem timeLineItem = dataSnapshot.getValue(TimeLineItem.class);
                items.add(0, timeLineItem);
                notifyDataSetChanged();
                //notifyItemInserted(items.size() - 1); ??
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(mContext, "변경", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(mContext, "제거 + " +items.size(), Toast.LENGTH_SHORT).show();
                String timelineCardKey = dataSnapshot.getKey();
                // [START_EXCLUDE]
                int timelineIndex = mTimelineIds.indexOf(timelineCardKey);
                if (timelineIndex > -1) {
                    // Remove data from the list
                    mTimelineIds.remove(timelineIndex);
                    items.remove(timelineIndex);
                    // Update the RecyclerView
                   // Log.d(TAG, ">>>>>      items: "  +items.get(timelineIndex).getTimeline_date() + "/ index:" + timelineIndex );

                    notifyItemRemoved(timelineIndex+1);
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
        ImageButton timelineLike;
        LinearLayout timelineLikeContainer;
        LinearLayout timelineCommentContainer;
        LinearLayout timelineContentContainer;
        // footer의 view
        ImageView timelineFooterIcon;

        // Header 의 view components
        RecyclerView rv_profile;
        TextView tlHeaderContainer;
        ImageButton tlHeaderAddimage;
        ImageButton timelineExpand;



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
                timelineFooterIcon = (ImageView) itemView.findViewById(R.id.timelinefootericon);

            } else {
                timelineLike = (ImageButton) itemView.findViewById(R.id.timeline_like);
                timelineNickname = (TextView) itemView.findViewById(R.id.timeline_nickname);
                timelineDate = (TextView) itemView.findViewById(R.id.timeline_date);
                timelineProfileImage = (ImageView) itemView.findViewById(R.id.timeline_profile);
                timelineContent = (TextView) itemView.findViewById(R.id.timeline_content);
                timelineCommentCnt = (TextView) itemView.findViewById(R.id.timline_comment_cnt);
                timelineLikeCnt = (TextView) itemView.findViewById(R.id.timline_like_cnt);
                timelineContentImage = (ImageView) itemView.findViewById(R.id.timeline_contentimage);
                timelineLikeContainer = (LinearLayout) itemView.findViewById(R.id.timeline_like_container);
                timelineCommentContainer = (LinearLayout) itemView.findViewById(R.id.timeline_comment_container);
                timelineContentContainer = (LinearLayout) itemView.findViewById(R.id.timeline_content_container);
                timelineExpand = (ImageButton) itemView.findViewById(R.id.timeline_expand);
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
    public void onBindViewHolder(TimelineViewHolder holder, final int position) {
        // 1. header data bind
        if (holder.mViewType == TYPE_HEADER) {
            bindHeaderItem(holder);
        } else if (holder.mViewType == TYPE_FOOTER) {
            Glide.with(mContext).load(R.drawable.ic_icon_font).crossFade().into(holder.timelineFooterIcon);
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
    private void bindBodyItem(final TimelineViewHolder holder, final int position) {

        // text data bind
        holder.timelineNickname.setText(items.get(position).getTimeline_nickName());
        holder.timelineDate.setText(items.get(position).getTimeline_date());
        holder.timelineContent.setText(items.get(position).getTimeline_content());
        holder.timelineCommentCnt.setText(String.format("%d", (items.get(position).getTimelineCountItem().getCommentCnt())));
        holder.timelineLikeCnt.setText(String.format("%d", (items.get(position).getTimelineCountItem().getLikeCnt())));
        profile_pathRef = storageRef.child(storageProfileFolder + "/" + items.get(position).getTimeline_profileImage());
        timeline_pathRef = storageRef.child(storageTimelineFolder + "/" + items.get(position).getTimeline_contentImage());
        // image data bind
        Glide.with(mContext).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(mContext)).into(holder.timelineProfileImage);

            isCheckLiker(holder, position);

        if (!items.get(position).getTimeline_contentImage().equals("empty")) {
            holder.timelineContentImage.setVisibility(View.VISIBLE);


            Glide.with(mContext).using(new FirebaseImageLoader()).load(timeline_pathRef).centerCrop()
                    .crossFade().into(holder.timelineContentImage);
        } else {
            holder.timelineContentImage.setVisibility(View.GONE);
        }

        holder.timelineLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLikeCount(holder, position);
            }
        });

        holder.timelineLikeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLikeCount(holder, position);
            }
        });
        holder.timelineCommentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Comment.class);
                intent.putExtra("TimelineItem", items.get(position));
                intent.putExtra("LikeCnt", items.get(position).getTimelineCountItem().getLikeCnt());
                intent.putExtra("commentCnt", items.get(position).getTimelineCountItem().getCommentCnt());
                mContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in, R.anim.step_back);

            }
        });
        holder.timelineContentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Comment.class);
                intent.putExtra("TimelineItem", items.get(position));
                intent.putExtra("LikeCnt", items.get(position).getTimelineCountItem().getLikeCnt());
                intent.putExtra("commentCnt", items.get(position).getTimelineCountItem().getCommentCnt());
                mContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in, R.anim.step_back);
            }
        });

        holder.timelineExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineCardDialogFragment dialogFragment = new TimelineCardDialogFragment();
                dialogFragment.show(fragmentManager, "fragment_dialog_test");
            }
        });

    }

    private void isCheckLiker(final TimelineViewHolder holder, final int position) {
        String key = items.get(position).getTimeline_key();
        likeUserReference = database.getReference().child("groups").child(groupId).child("likes")
                .child("timelineCard").child(items.get(position).getTimeline_key()).child("users").child(currentUser.getUid());

        likeUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                IsCheck ischeck = dataSnapshot.getValue(IsCheck.class);
                if(ischeck == null)
                    return;
                if(ischeck.getIsCheck()){
                    items.get(position).setIsSelect(1);
                    //selectLike = 1;
                    holder.timelineLike.setSelected(true);
                } else{
                    //selectLike = 0;
                    items.get(position).setIsSelect(0);
                    holder.timelineLike.setSelected(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void changeLikeCount(final TimelineViewHolder holder, final int position) {
        String key = items.get(position).getTimeline_key();
        int cnt = Integer.parseInt(holder.timelineLikeCnt.getText().toString());
        IsCheck isCheck;
        likeUserReference = database.getReference().child("groups").child(groupId).child("likes")
                .child("timelineCard").child(key).child("users");

        likeReference = database.getReference().child("groups").child(groupId)
                .child("timelineCard").child(key).child("timelineCountItem");

        if (items.get(position).getIsSelect() == 0) { // 좋아요 클릭
            items.get(position).setIsSelect(1);
            // 개인 사용자 좋아요 클릭 설정
            isCheck = new IsCheck(true);
            likeUserReference.child(currentUser.getUid()).setValue(isCheck);

            // 좋아요 카운트
            holder.timelineLike.setSelected(true);
            holder.timelineLikeCnt.setText(String.format("%d", ++cnt));

            Toast.makeText(mContext, "true", Toast.LENGTH_SHORT).show();
            likeReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    TimelineCountItem timelineCountItem = mutableData.getValue(TimelineCountItem.class);
                    if(timelineCountItem == null){
                        return Transaction.success(mutableData);
                    } else{
                        Log.d(TAG, ">>>>>>>...... "+  timelineCountItem.getLikeCnt());
                        int cnt = timelineCountItem.getLikeCnt();
                        timelineCountItem.setLikeCnt(++cnt);
                    }
                    mutableData.setValue(timelineCountItem);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    IsCheck isCheck = new IsCheck(true);
                    likeUserReference.child(currentUser.getUid()).setValue(isCheck);

                }
            });
        } else { // 좋아요 클릭 해제
            items.get(position).setIsSelect(0);
            // 개인 사용자 좋아요 클릭 설정
            isCheck = new IsCheck(false);
            likeUserReference.child(currentUser.getUid()).setValue(isCheck);

            holder.timelineLike.setSelected(false);
            holder.timelineLikeCnt.setText(String.format("%d", --cnt));

            Toast.makeText(mContext, "false", Toast.LENGTH_SHORT).show();
            likeReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    TimelineCountItem timelineCountItem = mutableData.getValue(TimelineCountItem.class);
                    if(timelineCountItem == null){
                        return Transaction.success(mutableData);
                    } else{
                        Log.d(TAG, ">>>>>>>...... "+  timelineCountItem.getLikeCnt());
                        int cnt = timelineCountItem.getLikeCnt();
                        timelineCountItem.setLikeCnt(--cnt);
                    }
                    mutableData.setValue(timelineCountItem);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    IsCheck isCheck = new IsCheck(false);
                    likeUserReference.child(currentUser.getUid()).setValue(isCheck);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 2; // header + body + footer;
    }

    public void setProfileItem(ArrayList<User> profile_items) {
        this.profile_items = profile_items;
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            timelineReference.removeEventListener(mChildEventListener);
        }
    }

}