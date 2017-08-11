package com.family.donghyunlee.family.bucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.WishListRecyclerItem;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-09.
 */
public class WishListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WishListRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<WishListRecyclerItem> items;
    private int item_layout;
    private String groupId;
    private ArrayList<StorageReference> storageitem;

    public WishListRecyclerAdapter(Context context, List<WishListRecyclerItem> items, int item_layout, String groupId) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.groupId = groupId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_wishlist, null);
        return new ViewHolder(v);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.wishlist_profile)
        ImageView wishlistProfile;
        @BindView(R.id.wishlist_date)
        TextView wishlistDate;
        @BindView(R.id.wishlist_nickname)
        TextView wishlistNickname;
        @BindView(R.id.wishlist_question)
        TextView wishlistQuestion;
        @BindView(R.id.wishlist_answer)
        TextView wishlistAnswer;

        @BindView(R.id.wishlist_delete)
        ImageButton wishlistDelete;
        @BindView(R.id.wishlist_register)
        ImageButton wishlistRegister;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { // view position
        WishListRecyclerItem item = items.get(position);

        Glide.with(context).using(new FirebaseImageLoader()).load(storageitem.get(position)).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(((ViewHolder)holder).wishlistProfile);
        ((ViewHolder)holder).wishlistDate.setText(item.getDate());
        ((ViewHolder)holder).wishlistNickname.setText(item.getNickName());
        ((ViewHolder)holder).wishlistQuestion.setText(item.getQuestion());
        ((ViewHolder)holder).wishlistAnswer.setText(item.getAnswer());

        ((ViewHolder)holder).wishlistDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public WishListRecyclerItem getCurItem(int position) {
        WishListRecyclerItem item = items.get(position);
        return item;
    }

    public void setStorageitem(ArrayList<StorageReference> storageitem){
        this.storageitem = storageitem;

    }
}