package com.family.donghyunlee.family.photoalbum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.MemoryPhotoImage;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zanlabs.widget.infiniteviewpager.InfinitePagerAdapter;

import java.util.List;

/**
 * Created by DONGHYUNLEE on 2017-08-03.
 */

public class InAlbumPagerAdapter extends InfinitePagerAdapter {

    private String albumId;
    private String groupId;
    private LayoutInflater inflater;
    private Context context;
    private List<MemoryPhotoImage> items;
    private FirebaseDatabase database;
    private DatabaseReference groupReference;

    private FirebaseStorage storage;

    public void setDataList(List<MemoryPhotoImage> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    public InAlbumPagerAdapter(Context context, String albumId, String groupId) {
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.albumId = albumId;
        this.groupId = groupId;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private static class ViewHolder {
        public int position;
        ImageView inAlbumImage;

        public ViewHolder(View view) {

            inAlbumImage = (ImageView) view.findViewById(R.id.inalbum_image);

        }
    }
    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.viewpager_image_item, container, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        MemoryPhotoImage item = items.get(position);
        holder.position = position;
        // image 서버에서 가져오기

        imgGetInStorage((holder).inAlbumImage, item);

        return view;
    }
    @Override
    public int getItemCount() {
        return items==null ? 0 : items.size();
    }


    private void imgGetInStorage(final ImageView inalbumImage, MemoryPhotoImage item) {

        String storageMemoryImagesFolder = context.getString(R.string.storage_memoryimages_folder);
        StorageReference storageRef = storage.getReferenceFromUrl(context.getString(R.string.firebase_storage));
        StorageReference pathRef = storageRef.child(storageMemoryImagesFolder + "/" + item.getImgPath());
        Glide.with(context).using(new FirebaseImageLoader()).load(pathRef).crossFade().into(inalbumImage);

    }


}