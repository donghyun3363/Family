package com.family.donghyunlee.family.photoalbum;

/**
 * Created by DONGHYUNLEE on 2017-08-05.
 */

public class TravelAlbumRecyclerAdapter {
//    private static final String TAG = MemoryAlbumRecyclerAdapter.class.getSimpleName();
//    private Context context;
//    private List<TravelAlbum> items;
//    private int item_layout;
//    private static final int TYPE_HEADER = 0;
//    private static final int TYPE_ITEM = 1;
//    private ProfileFragment profileFragment;
//    FirebaseDatabase database;
//    DatabaseReference userReference;
//    DatabaseReference groupReference;
//    FirebaseAuth mAuth ;
//    FirebaseStorage storage;
//    FirebaseUser currentUser;
//    TravelAlbum item;
//
//    // 어뎁터 생성자
//    public TravelAlbumRecyclerAdapter(Context context, List<TravelAlbum> items, int item_layout) {
//        this.context = context;
//        this.items = items;
//        this.item_layout = item_layout;
//        database = FirebaseDatabase.getInstance();
//        userReference = database.getReference("users");
//        groupReference = database.getReference("groups");
//        mAuth = FirebaseAuth.getInstance();
//        storage = FirebaseStorage.getInstance();
//        currentUser = mAuth.getCurrentUser();
//    }
//
//    // 뷰홀더 생성(인플레터 후)
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_HEADER) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_oneofitems, null);
//            Log.d(TAG, "Header");
//            return new HeaderViewHolder(v);
//        } else if (viewType == TYPE_ITEM) {   // TYPE_BODY
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_travelphoto, null);
//            Log.d(TAG, "Body");
//            return new ViewHolder(v);
//        }
//        return null;
//    }
//
//    public class HeaderViewHolder extends RecyclerView.ViewHolder {
//        // 뷰홀더에서 각 뷰들을 참조 함수
//        public HeaderViewHolder(View itemView) {
//            super(itemView);
//
//        }
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        // 뷰홀더에서 각 뷰들을 참조 함수
//
//        @BindView(R.id.img_travel_album)
//        ImageView imgTravelAlbum;
//        @BindView(R.id.title_travel_album)
//        TextView titleTravelAlbum;
//        @BindView(R.id.date_travel_album)
//        TextView dateTravelAlbum;
//        @BindView(R.id.profile_travel_album)
//        ImageView profileTravelAlbum;
//
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//        }
//
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { // view position
//
//        if (holder instanceof HeaderViewHolder) {
//
//        } else if (holder instanceof ViewHolder) {
//            position--;
//            item = items.get(position);
//
//            // TODO 메인 사진 정리
//            if ((item.getMainImgPath()).equals("empty") == false) {
//                Glide.with(context).load(R.drawable.img_temp2).into(((ViewHolder) holder).imgTravelAlbum);
//            }
//            imgDownload(((ViewHolder) holder).profileTravelAlbum);
//            ((ViewHolder) holder).titleTravelAlbum.setText(item.getAlbumTitle());
//            ((ViewHolder) holder).dateTravelAlbum.setText(item.getAlbumDate());
//        }
//    }
//
//    private void imgDownload(final ImageView profileTravelAlbum) {
//        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
//                String storageFolder = context.getString(R.string.storage_profiles_folder);
//                String groupId = null;
//                while (child.hasNext()) {
//
//                    User user = child.next().getValue(User.class);
//                    // 데이터베이스에서 사용자의 정보를 가져오며 profile loading
//                    if (user.getId().equals(currentUser.getUid())) {
//                        groupId = user.getGroupId();
//
//                        StorageReference storageRef = storage.getReferenceFromUrl(context.getString(R.string.firebase_storage));
//                        StorageReference pathRef = storageRef.child(storageFolder + "/" + user.getUserImage());
//
//                        Glide.with(context).using(new FirebaseImageLoader()).load(pathRef).centerCrop()
//                                .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(profileTravelAlbum);
//                    }
//                }
//                try {
//                    // travel 앨범 내 사진 가져오기!
//                    groupReference = database.getReference("groups").child(groupId)
//                            .child("travelPhoto").child(item.getId()).child("imagePackage");
//                    groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
//                            if (child.hasNext()) {
//                                TravelAlbum travelAlbum = child.next().getValue(TravelAlbum.class);
////                                Glide.with(context).using(new FirebaseImageLoader()).load(pathRef).centerCrop()
////                                        .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(profileMemoryAlbum);
//                            }
//
//                        }
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                } catch (NullPointerException e) {
//                    Log.e(TAG, ">>>>>   imgDownLoad Null Exception!");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    // header 여부 체크 메소드
//    private boolean isPositionHeader(int position) {
//        return position == 0;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        if (isPositionHeader(position))
//            return TYPE_HEADER;
//        else
//            return TYPE_ITEM;
//    }
//
//    // 아이템 수 카운트
//    @Override
//    public int getItemCount() {
//        return this.items.size() + 1;
//    }
//

}