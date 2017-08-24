package com.family.donghyunlee.family.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import com.family.donghyunlee.family.data.CommentCountItem;
import com.family.donghyunlee.family.data.CommentItem;
import com.family.donghyunlee.family.data.IsCheck;
import com.family.donghyunlee.family.data.TimeLineItem;
import com.family.donghyunlee.family.dialog.CommentCardDialogFragment;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
 * Created by DONGHYUNLEE on 2017-08-18.
 */
public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.CommentViewHolder> {
    private static final String TAG = CommentRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<CommentItem> items;
    private LayoutInflater mInflater;
    // ViewHolder Type
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;
    private StorageReference timeline_pathRef;
    private StorageReference comment_pathRef;
    private String storageProfileFolder;
    private String storageCommentFolder;
    private String storageTimelineFolder;
    private DatabaseReference likeUserReference;
    private DatabaseReference likeReference;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseDatabase database;
    private ChildEventListener mChildEventListener;
    private DatabaseReference commentReference;
    private String groupId;
    private List<String> mCommentIds;
    String commentKey;

    private TimeLineItem timelineItem;
    private int addCommentCnt;
    private boolean isAdd;
    private RecyclerView recyclerView;
    private FragmentManager fragmentManager;

    public CommentRecyclerAdapter(Context context, final ArrayList<CommentItem> items, final String groupId,
                                  final TimeLineItem timelineItem, final RecyclerView recyclerView, FragmentManager fragmentManager) {

        this.mContext = context;
        this.items = items;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupId = groupId;
        this.timelineItem = timelineItem;
        this.recyclerView = recyclerView;
        this.fragmentManager = fragmentManager;
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        addCommentCnt = 0;
        this.isAdd = false;

        storageProfileFolder = mContext.getResources().getString(R.string.storage_profiles_folder);
        storageCommentFolder = mContext.getResources().getString(R.string.storage_comment_folder);
        storageTimelineFolder = mContext.getResources().getString(R.string.storage_timeline_folder);


        commentReference = database.getReference().child("groups").child(groupId)
                .child("commentCard").child(timelineItem.getTimeline_key());

        mCommentIds = new ArrayList<>();
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                mCommentIds.add(dataSnapshot.getKey());
                final CommentItem commentItem = dataSnapshot.getValue(CommentItem.class);
                String commentkey = commentItem.getCommentKey();
                likeUserReference = database.getReference().child("groups").child(groupId).child("likes")
                        .child("commentCard").child(timelineItem.getTimeline_key()).child(commentkey)
                    .child("users").child(currentUser.getUid());
                likeUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        IsCheck ischeck = dataSnapshot.getValue(IsCheck.class);
                        if (ischeck == null) {
                            Log.i(TAG, ">>>>>>>>>> 1");
                            commentItem.setIsCheck(false);
                            items.add(commentItem);
                        } else if (ischeck.getIsCheck()) {

                            Log.i(TAG, ">>>>>>>>>> 2");
                            commentItem.setIsCheck(true);
                            items.add(commentItem);
                        } else if (!ischeck.getIsCheck()) {

                            Log.i(TAG, ">>>>>>>>>> 3");
                            commentItem.setIsCheck(false);
                            items.add(commentItem);
                        }
                        if (isAdd) {
                            recyclerView.scrollToPosition(items.size());
                        }
                        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>> size: " + items.size());
                        notifyDataSetChanged();
                        return;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Toast.makeText(mContext, "변경", Toast.LENGTH_SHORT).show();
//                CommentItem newCommentItem = dataSnapshot.getValue(CommentItem.class);
//                String commentKey = dataSnapshot.getKey();
//
//                // [START_EXCLUDE]
//                int commentIndex = mCommentIds.indexOf(commentKey);
//                if (commentIndex > -1) {
//
//                    items.set(commentIndex, newCommentItem);
//
//                    notifyItemChanged(commentIndex);
//                } else {
//                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
//                }
                // [END_EXCLUDE]

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();
                // [START_EXCLUDE]

                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>   HERE:" + commentKey);
                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Remove data from the list
                    mCommentIds.remove(commentIndex);
                    Toast.makeText(mContext, "제거: + " + items.get(commentIndex).getCommentKey(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>     HERE IN ADAPTER:     " +items.get(commentIndex).getCommentKey() + "// index: "+commentIndex);
                    items.remove(commentIndex);
                    notifyDataSetChanged();
                    //notifyItemRemoved(commentIndex + 1);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, ">>>>>>>>>>>>>>>>>> onCancelled: " + databaseError.getDetails());
            }
        };
        commentReference.addChildEventListener(mChildEventListener);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // holder 의 viewType
        int mViewType;
        // body 의 view components
        ImageView commentProfileImage;
        TextView commentNickName;
        TextView commentDate;
        TextView commentContent;
        TextView commentLikeCnt;
        ImageButton commentLike;
        ImageView commentImage;
        LinearLayout commentContainer;
        // Header 의 view components
        TextView timelineNickname;
        TextView timelineDate;
        ImageView timelineProfileImage;
        TextView timelineContent;
        TextView timelineCommentCnt;
        TextView timelineLikeCnt;
        ImageView timelineContentImage;
        ImageButton timelineExpand;
        ImageButton timelineBottom;

        // viewType 에 따른 viewHolder 정의
        public CommentViewHolder(View itemView, int viewType) {
            super(itemView);
            storage = FirebaseStorage.getInstance();

            storageRef = storage.getReferenceFromUrl(mContext.getResources().getString(R.string.firebase_storage));
            mViewType = viewType;

            if (TYPE_HEADER == viewType) {
                timelineNickname = (TextView) itemView.findViewById(R.id.comment_incard_nickname);
                timelineDate = (TextView) itemView.findViewById(R.id.comment_incard_date);
                timelineProfileImage = (ImageView) itemView.findViewById(R.id.comment_incard_profile);
                timelineContent = (TextView) itemView.findViewById(R.id.comment_incard_content);
                timelineCommentCnt = (TextView) itemView.findViewById(R.id.comment_incard_comment_cnt);
                timelineLikeCnt = (TextView) itemView.findViewById(R.id.comment_incard_like_cnt);
                timelineContentImage = (ImageView) itemView.findViewById(R.id.comment_incard_contentimage);
                timelineBottom = (ImageButton) itemView.findViewById(R.id.comment_incard_comment_bottom);
                timelineExpand = (ImageButton) itemView.findViewById(R.id.comment_incard_expand);
                timelineBottom.setOnClickListener(this);
                timelineExpand.setOnClickListener(this);

            } else {
                commentImage = (ImageView) itemView.findViewById(R.id.comment_image);
                commentProfileImage = (ImageView) itemView.findViewById(R.id.comment_profile);
                commentNickName = (TextView) itemView.findViewById(R.id.comment_nickname);
                commentDate = (TextView) itemView.findViewById(R.id.comment_date);
                commentContent = (TextView) itemView.findViewById(R.id.comment_content);
                commentLikeCnt = (TextView) itemView.findViewById(R.id.comment_like_cnt);
                commentLike = (ImageButton) itemView.findViewById(R.id.comment_like);
                commentLike.setOnClickListener(this);
                commentContainer = (LinearLayout) itemView.findViewById(R.id.comment_container);


            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.comment_incard_expand:
                    Toast.makeText(mContext, "확장", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.comment_like:
                    Toast.makeText(mContext, "좋아요", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.comment_incard_comment_bottom:
                    recyclerView.scrollToPosition(items.size());
            }
        }

    }

    // header 여부 체크 메소드
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else
            return TYPE_ITEM;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {

            View headerView = mInflater.inflate(R.layout.comment_header_item, parent, false);
            return new CommentViewHolder(headerView, viewType);
        } else {

            View itemView = mInflater.inflate(R.layout.comment_body_item, parent, false);
            return new CommentViewHolder(itemView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        // 1. header data bind
        if (holder.mViewType == TYPE_HEADER) {
            bindHeaderItem(holder);
        }
        // 2. body data bind
        else {
            bindBodyItem(holder, position - 1);
        }
    }

    // head itemSet bind
    private void bindHeaderItem(CommentViewHolder holder) {
        holder.timelineNickname.setText(timelineItem.getTimeline_nickName());
        holder.timelineDate.setText(timelineItem.getTimeline_date());
        holder.timelineContent.setText(timelineItem.getTimeline_content());
        holder.timelineCommentCnt.setText(String.format("%d", ((timelineItem.getTimelineCountItem().getCommentCnt())) + addCommentCnt));
        holder.timelineLikeCnt.setText(String.format("%d", (timelineItem.getTimelineCountItem().getLikeCnt())));
        profile_pathRef = storageRef.child(storageProfileFolder + "/" + timelineItem.getTimeline_profileImage());
        timeline_pathRef = storageRef.child(storageTimelineFolder + "/" + timelineItem.getTimeline_contentImage());


        Glide.with(mContext).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(mContext)).into(holder.timelineProfileImage);

        if (!timelineItem.getTimeline_contentImage().equals("empty")) {
            holder.timelineContentImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).using(new FirebaseImageLoader()).load(timeline_pathRef).centerCrop()
                    .crossFade().into(holder.timelineContentImage);
        } else {
            holder.timelineContentImage.setVisibility(View.GONE);
        }


    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public void setAddCommentCnt(int addCommentCnt) {
        this.addCommentCnt = addCommentCnt;
    }

    public int getAddCommentCnt() {
        return addCommentCnt;
    }

    // body itemSet bind
    private void bindBodyItem(final CommentViewHolder holder, final int position) {

        holder.commentNickName.setText(items.get(position).getCommentNickName());
        holder.commentDate.setText(items.get(position).getCommentDate());
        holder.commentContent.setText(items.get(position).getCommentContent());
        holder.commentLikeCnt.setText(String.format("%d", (items.get(position).getCommentCountItem().getLikeCnt())));
        profile_pathRef = storageRef.child(storageProfileFolder + "/" + items.get(position).getCommentProfileImage());
        comment_pathRef = storageRef.child(storageCommentFolder + "/" + items.get(position).getCommentImage());

        Glide.with(mContext).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(mContext)).into(holder.commentProfileImage);

        // 버튼 set.
        if (items.get(position).getIsCheck() == false) {
            Log.i(TAG, ">>>>>>>>>       false");
            holder.commentLike.setSelected(false);

        } else {
            Log.i(TAG, ">>>>>>>>>       true");
            holder.commentLike.setSelected(true);
        }

        if (!items.get(position).getCommentImage().equals("empty")) {
            holder.commentImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).using(new FirebaseImageLoader()).load(comment_pathRef).centerCrop()
                    .crossFade().into(holder.commentImage);
        }

        isCheckLiker(holder, position);

        holder.commentLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLikeCount(holder, position);
            }
        });

        holder.commentContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG ,">>>>>>>>>>>>>>>>>>>>>>>>>>>>  HERE POSITION" + position);

                Log.i(TAG ,">>>>>>>>>>>>>>>>>>>>>>>>>>>>  HERE getCommentKey" +  items.get(position).getCommentKey());
                CommentCardDialogFragment dialogFragment = CommentCardDialogFragment.newInstance
                        (items.get(position).getTimelineKey()
                                , items.get(position).getCommentKey()
                                , items.get(position).getCommentImage());

                fragmentManager.beginTransaction().commit();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
                dialogFragment.show(fragmentManager, "commentCardDialog");
                return false;
            }
        });

    }

    private void isCheckLiker(final CommentViewHolder holder, final int position) {
        String key = items.get(position).getCommentKey();
        likeUserReference = database.getReference().child("groups").child(groupId).child("likes")
                .child("commentCard").child(key).child("users").child(currentUser.getUid());
        likeUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                IsCheck ischeck = dataSnapshot.getValue(IsCheck.class);
                if (ischeck == null)
                    return;
                if (ischeck.getIsCheck()) {
                    holder.commentLike.setSelected(true);
                } else {
                    holder.commentLike.setSelected(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeLikeCount(final CommentViewHolder holder, final int position) {
        String key = items.get(position).getCommentKey();
        likeUserReference = database.getReference().child("groups").child(groupId).child("likes")
                .child("commentCard").child(timelineItem.getTimeline_key()).child(key).child("users");
        likeReference = database.getReference().child("groups").child(groupId)
                .child("commentCard").child(timelineItem.getTimeline_key()).child(key).child("commentCountItem");


        IsCheck isCheck;


        if (!items.get(position).getIsCheck()) { // 좋아요 클릭
            items.get(position).setIsCheck(true);
            // 개인 사용자 좋아요 클릭 설정
            isCheck = new IsCheck(true);
            likeUserReference.child(currentUser.getUid()).setValue(isCheck).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    int cnt = Integer.parseInt(holder.commentLikeCnt.getText().toString());
                    items.get(position).setIsCheck(true);
                    // 좋아요 카운트
                    holder.commentLike.setSelected(true);
                    holder.commentLikeCnt.setText(String.format("%d", ++cnt));
                }
            });
            likeReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    CommentCountItem commentCountItem = mutableData.getValue(CommentCountItem.class);
                    if (commentCountItem == null) {
                        return Transaction.success(mutableData);
                    } else {

                        int cnt = commentCountItem.getLikeCnt();
                        commentCountItem.setLikeCnt(++cnt);
                    }
                    mutableData.setValue(commentCountItem);
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

            items.get(position).setIsCheck(false);
            // 개인 사용자 좋아요 클릭 설정

            isCheck = new IsCheck(false);
            likeUserReference.child(currentUser.getUid()).setValue(isCheck).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    int cnt = Integer.parseInt(holder.commentLikeCnt.getText().toString());
                    items.get(position).setIsCheck(false);
                    holder.commentLike.setSelected(false);
                    holder.commentLikeCnt.setText(String.format("%d", --cnt));
                }
            });

            likeReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    CommentCountItem commentCountItem = mutableData.getValue(CommentCountItem.class);
                    if (commentCountItem == null) {
                        return Transaction.success(mutableData);
                    } else {
                        Log.d(TAG, ">>>>>>>...... " + commentCountItem.getLikeCnt());
                        int cnt = commentCountItem.getLikeCnt();
                        commentCountItem.setLikeCnt(--cnt);
                    }
                    mutableData.setValue(commentCountItem);
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
        return items.size() + 1; // (body item count) + (header item count)
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            commentReference.removeEventListener(mChildEventListener);
        }
    }

}
