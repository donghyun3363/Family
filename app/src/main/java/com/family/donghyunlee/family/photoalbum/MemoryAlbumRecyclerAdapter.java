package com.family.donghyunlee.family.photoalbum;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.MemoryAlbum;
import com.family.donghyunlee.family.data.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
 */

public class MemoryAlbumRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MemoryAlbumRecyclerAdapter.class.getSimpleName();
    private static final int PROFILE_IMG = 0;
    private static final int MAIN_IMG = 1;
    private Context context;
    private List<MemoryAlbum> items;
    private int item_layout;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    FirebaseDatabase database;
    DatabaseReference userReference;
    DatabaseReference groupReference;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    FirebaseUser currentUser;
    MemoryAlbum item;
    private String groupId;

    // 어뎁터 생성자
    public MemoryAlbumRecyclerAdapter(Context context, List<MemoryAlbum> items, int item_layout, String groupId) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.groupId = groupId;
        database = FirebaseDatabase.getInstance();
        groupReference = database.getReference("groups");
        userReference = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    // 뷰홀더 생성(인플레터 후)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_HEADER) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_oneofitems, null);
//            Log.d(TAG, "Header");
//            return new HeaderViewHolder(v);
//        } else if (viewType == TYPE_ITEM) {   // TYPE_BODY
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_memoryphoto, null);
//            Log.d(TAG, "Body");
//            return new ViewHolder(v);
//        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_memoryphoto, null);
          Log.d(TAG, "Body");
        return new ViewHolder(v);
    }

//    public class HeaderViewHolder extends RecyclerView.ViewHolder {
//        // 뷰홀더에서 각 뷰들을 참조 함수
//        public HeaderViewHolder(View itemView) {
//            super(itemView);
//
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 뷰홀더에서 각 뷰들을 참조 함수

        @BindView(R.id.img_memory_album)
        ImageView imgMemoryAlbum;
        @BindView(R.id.title_memory_album)
        TextView titleMemoryAlbum;
        @BindView(R.id.date_memory_album)
        TextView dateMemoryAlbum;
        @BindView(R.id.profile_memory_album)
        ImageView profileMemoryAlbum;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        @OnClick(R.id.img_memory_album)
        void albumClick() {
            Toast.makeText(context, "앨범 클릭", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { // view position

//        if (holder instanceof HeaderViewHolder) {
//
//        } else if (holder instanceof ViewHolder) {
//            position--;
            item = items.get(position);
            ((ViewHolder) holder).imgMemoryAlbum.setTransitionName("memoryImg");

             //mainimg가 존재할 경우
            Log.e("22222222222", "item.get:"+item.getMainImgPath());
            if ((item.getMainImgPath()).equals("empty") == false) {
                mainImgDownload(((ViewHolder) holder).imgMemoryAlbum);
            }else{
                Glide.with(context).load(R.drawable.img_default).crossFade().into(((ViewHolder) holder).imgMemoryAlbum);
            }
            profileImgDownload(((ViewHolder) holder).profileMemoryAlbum);
            ((ViewHolder) holder).titleMemoryAlbum.setText(item.getAlbumTitle());
            ((ViewHolder) holder).dateMemoryAlbum.setText(item.getAlbumDate());
  //      }
    }

    private void mainImgDownload(final ImageView mainImgMemoryAlbum){

        //mainimg가 존재할 경우\
            final String storageMemoryFolder = context.getString(R.string.storage_albumimages_folder);
            StorageReference storageRef = storage.getReferenceFromUrl(context.getString(R.string.firebase_storage));
            StorageReference pathRef = storageRef.child(storageMemoryFolder + "/" + item.getMainImgPath());
            Log.e(TAG, "2222      item.getMain"+ item.getMainImgPath());

            Glide.with(context).using(new FirebaseImageLoader()).load(pathRef).crossFade().into(mainImgMemoryAlbum);

    }
    // 프로필 이미지 가져오기.
    private void profileImgDownload(final ImageView profileMemoryAlbum) {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                String storageProfileFolder = context.getString(R.string.storage_profiles_folder);

                while (child.hasNext()) {
                    // 유저 프로필 사진 가져오기
                    User user = child.next().getValue(User.class);
                    Log.e("CHECK", "user: " + user.getId() +"//// item user id:" + item.getUserId());
                    if (user.getId().equals(item.getUserId())) {

                        StorageReference storageRef = storage.getReferenceFromUrl(context.getString(R.string.firebase_storage));
                        StorageReference pathRef = storageRef.child(storageProfileFolder + "/" + user.getUserImage());

                        Glide.with(context).using(new FirebaseImageLoader()).load(pathRef).centerCrop()
                                .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(profileMemoryAlbum);
                    }
                }
                return;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // header 여부 체크 메소드
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position))
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }
    // 아이템 수 카운트
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public MemoryAlbum getCurItem(int position){
        Log.d("1111111111111","position: " + position);
        //position--;
        item = items.get(position);
        return item;
    }

}