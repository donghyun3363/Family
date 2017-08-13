package com.family.donghyunlee.family.bucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class ShareRecyclerAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WishListRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<ToProgressItem> items;
    private int item_layout;
    private String groupId;
    private ArrayList<StorageReference> storageitem;

    public ShareRecyclerAdapter(Context context, List<ToProgressItem> items, int item_layout, String groupId) {
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



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) { // view position
        ToProgressItem item = items.get(position);

    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public ToProgressItem getCurItem(int position) {
        ToProgressItem item = items.get(position);
        return item;
    }

    public void setStorageitem(ArrayList<StorageReference> storageitem){
        this.storageitem = storageitem;

    }
}