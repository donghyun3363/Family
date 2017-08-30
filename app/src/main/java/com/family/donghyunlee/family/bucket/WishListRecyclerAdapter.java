package com.family.donghyunlee.family.bucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.WishListRecyclerItem;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-09.
 */
public class WishListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WishListRecyclerAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<WishListRecyclerItem> items;
    private int item_layout;
    private String groupId;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference pathRef;
    private FirebaseDatabase database;
    private DatabaseReference wishListReference;
    private String storageProfileFolder;
    private ChildEventListener mChildEventListener;
    private ArrayList<String> wishlistId;
    public WishListRecyclerAdapter(final Context context, final ArrayList<WishListRecyclerItem> items, int item_layout, String groupId) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.groupId = groupId;
        wishlistId = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        storageProfileFolder = context.getResources().getString(R.string.storage_profiles_folder);
        storageRef = storage.getReferenceFromUrl(context.getResources().getString(R.string.firebase_storage));
        database = FirebaseDatabase.getInstance();
        wishListReference = database.getReference().child("groups").child(groupId).child("wishList");


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                wishlistId.add(dataSnapshot.getKey());
                final WishListRecyclerItem wishListRecyclerItem = dataSnapshot.getValue(WishListRecyclerItem.class);

                items.add(wishListRecyclerItem);
                notifyDataSetChanged();
                return;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                WishListRecyclerItem wishListRecyclerItem = dataSnapshot.getValue(WishListRecyclerItem.class);
                String wishListKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int wishListIndex = wishlistId.indexOf(wishListKey);
                if (wishListIndex > -1) {

                    items.set(wishListIndex, wishListRecyclerItem);

                    notifyItemChanged(wishListIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + wishListKey);
                }
                // [END_EXCLUDE]

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                String commentKey = dataSnapshot.getKey();
//                // [START_EXCLUDE]
//
//                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>   HERE:" + commentKey);
//                int commentIndex = mCommentIds.indexOf(commentKey);
//                if (commentIndex > -1) {
//                    // Remove data from the list
//                    mCommentIds.remove(commentIndex);
//                    Toast.makeText(mContext, "제거: + " + items.get(commentIndex).getCommentKey(), Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>     HERE IN ADAPTER:     " +items.get(commentIndex).getCommentKey() + "// index: "+commentIndex);
//                    items.remove(commentIndex);
//                    notifyDataSetChanged();
//                    //notifyItemRemoved(commentIndex + 1);
//                } else {
//                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
//                }
//                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, ">>>>>>>>>>>>>>>>>> onCancelled: " + databaseError.getDetails());
            }
        };
        wishListReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_bucketcontent, null);
        return new ViewHolder(v);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bucketcontent_profileimage)
        ImageView contentProfileImage;
        @BindView(R.id.bucketcontent_date)
        TextView contentData;
        @BindView(R.id.bucketcontent_nickname)
        TextView contentNickname;
        @BindView(R.id.bucketcontent_answer)
        TextView contentAnswer;
        @BindView(R.id.color_container)
        LinearLayout colorContainer;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) { // view position
        WishListRecyclerItem item = items.get(position);
        pathRef = storageRef.child(storageProfileFolder + "/" + item.getImgProfilePath());
        Glide.with(context).using(new FirebaseImageLoader()).load(pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(((ViewHolder)holder).contentProfileImage);
        ((ViewHolder)holder).contentData.setText(item.getDate());
        ((ViewHolder)holder).contentNickname.setText(item.getNickName());
        ((ViewHolder)holder).contentAnswer.setText(item.getAnswer());
        switch (item.getColor()){
            case 0:
                ((ViewHolder)holder).colorContainer.setBackgroundResource(R.color.main_color_light_b);
                break;
            case 1:
                ((ViewHolder)holder).colorContainer.setBackgroundResource(R.color.main_circle_blue);
                break;
            case 2:
                ((ViewHolder)holder).colorContainer.setBackgroundResource(R.color.main_circle_orange);
                break;
            default:
                ((ViewHolder)holder).colorContainer.setBackgroundResource(R.color.main_color_light_b);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public WishListRecyclerItem getCurItem(int position) {
        WishListRecyclerItem item = items.get(position);
        return item;
    }

    public ArrayList<WishListRecyclerItem> getItems(){
        return items;
    }
    public void setItems(ArrayList<WishListRecyclerItem> items){
        this.items = items;
    }
    public void cleanupListener() {
        if (mChildEventListener != null) {
            wishListReference.removeEventListener(mChildEventListener);
        }
    }
}