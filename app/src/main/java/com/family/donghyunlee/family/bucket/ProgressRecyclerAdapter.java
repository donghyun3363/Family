package com.family.donghyunlee.family.bucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-30.
 */

public class ProgressRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final String TAG = ProgressRecyclerAdapter.class.getSimpleName();
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
    public ProgressRecyclerAdapter(final Context context, final ArrayList<WishListRecyclerItem> items, int item_layout, String groupId) {
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


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_bucketcontent, null);
            return new ProgressRecyclerAdapter.ViewHolder(v);
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
}
