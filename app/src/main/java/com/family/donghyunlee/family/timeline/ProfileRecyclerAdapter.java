package com.family.donghyunlee.family.timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-15.
 */

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder> {

    private static final String TAG = ProfileRecyclerAdapter.class.getSimpleName();
    static final int TYPE_HEADER = 0;
    static final int TYPE_BODY = 1;

    private Context mContext;
    private ArrayList<User> items = new ArrayList<>();
    private LayoutInflater mInflater;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference pathRef;
    private String storageProfileFolder;


    public ProfileRecyclerAdapter(Context context, final ArrayList<User> items, String groupId) {

        this.mContext = context;
        this.items = items;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {


        ImageView profileImage;
        TextView profileName;

        public ProfileViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            storage = FirebaseStorage.getInstance();
            storageProfileFolder = mContext.getResources().getString(R.string.storage_profiles_folder);
            storageRef = storage.getReferenceFromUrl(mContext.getResources().getString(R.string.firebase_storage));
            if (viewType == TYPE_HEADER) {

            } else {
                profileName = (TextView) itemView.findViewById(R.id.profile_name);
                profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_BODY;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == TYPE_HEADER) {
            itemView = mInflater.inflate(R.layout.profile_header_item, parent, false);
            return new ProfileViewHolder(itemView, viewType);
        } else {
            itemView = mInflater.inflate(R.layout.profile_body_item, parent, false);
            return new ProfileViewHolder(itemView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {

        // 1. header data bind
        if (holder.getItemViewType() == TYPE_HEADER) {
            bindHeaderItem(holder);
        }

        // 2. body data bind
        else {
            bindBodyItem(holder, position - 1);
        }
    }

    private void bindHeaderItem(ProfileViewHolder holder) {

    }

    private void bindBodyItem(ProfileViewHolder holder, int position) {
        // userName 이 세글자 이상 넘어가게 되면,
        String userName = items.get(position).getUserNicname();
        if (userName.length() > 3) {
            // 잘라내고 '...' 을 붙임
            userName = userName.substring(0, 3) + "...";
        }

        holder.profileName.setText(userName);
        pathRef = storageRef.child(storageProfileFolder + "/" + items.get(position).getUserImage());
        // image data bind
        Glide.with(mContext).using(new FirebaseImageLoader()).load(pathRef).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(mContext)).into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }
}
