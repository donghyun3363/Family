package com.family.donghyunlee.family.photoalbum;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
// */
//
//public class TravelFragment extends Fragment implements OnMapReadyCallback {
////
////    @BindView(R.id.mapview)
////    MapView mapView;
////    @BindView(R.id.expandText)
////    TextView expandText;
////    @BindView(R.id.expandableLayout)
//    ExpandableRelativeLayout expandableLayout;
//    @BindView(R.id.expandAllow)
//    ImageView expandAllow;
//
//
//    EditText travelName;
//    EditText travelLocation;
//
//    private Geocoder mGeocoder;
//    private LatLng mLatLng;
//    private double lat;
//    private double lon;
//    private static final int ADD_ALBUM = 0;
//    private static final int GET_ALBUM_TOSERVER = 1;
//
//    TravelAlbumRecyclerAdapter recyclerAdapter;
//    LinearLayoutManager layoutManager;
//    RecyclerView recylerView;
//    List<TravelAlbum> items = new ArrayList<>();
//    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    FirebaseUser currentUser = mAuth.getCurrentUser();
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference userReference = database.getReference("users");
//    DatabaseReference groupReference = database.getReference("groups");
//
//
//    public TravelFragment() {
//
//    }
//
//    public static TravelFragment newInstance() {
//        Bundle args = new Bundle();
//
//        TravelFragment fragment = new TravelFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_travel, container, false);
//        ButterKnife.bind(this, v);
//        setInit(savedInstanceState);
//        // 서버에서 앨범을 얻음
//        setAlbum(GET_ALBUM_TOSERVER);
//
//        // 어뎁터 set
//        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//        recyclerAdapter = new TravelAlbumRecyclerAdapter(getActivity(), items, R.layout.fragment_travel);
//        recylerView = (RecyclerView) v.findViewById(R.id.rv_travel);
//        recylerView.setHasFixedSize(true);
//        recylerView.setLayoutManager(layoutManager);
//        recylerView.setAdapter(recyclerAdapter);
//
//        recylerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recylerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//                // 길게 터치할 경우
//            }
//        }));
//
//
//        return v;
//    }
//
//    @OnClick(R.id.fab_travel)
//    void onFabClick() {
//        AlertDialog.Builder dialog = createDialog();
//
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.dialog_make_travel_album, (ViewGroup) getActivity().findViewById(R.id.popup_travel_root));
//        travelName = (EditText) layout.findViewById(R.id.popup_travel_name);
//        travelLocation = (EditText) layout.findViewById(R.id.popup_travel_location);
//        dialog.setView(layout);
//        dialog.show();
//
//    }
//
//    private AlertDialog.Builder createDialog() {
//        AlertDialog.Builder insertDialog = new AlertDialog.Builder(getContext());
//        insertDialog.setTitle(R.string.dialog_title)
//                .setMessage(R.string.dialog_message)
//                .setPositiveButton(R.string.dialog_posi, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // user 그룹 아이디와 프로필 사진 가져오기
//                        if (travelName.length() <= 0 || travelLocation.length() <= 0) {
//
//                            //TODO 다이얼로그 꺼지는 것 처리
//                            Toast.makeText(getActivity(), "앨범명과 위치를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            setAlbum(ADD_ALBUM);
//                        }
//                    }
//                })
//                .setNegativeButton(R.string.dialog_negati, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .create();
//        return insertDialog;
//    }
//
//    public void setAlbum(int flag) {
//        userReference = database.getReference("users");
//        groupReference = database.getReference("groups");
//        if (flag == ADD_ALBUM) {
//            Log.e(TAG, "앨범 추가~~~~~~~~~~~~~~~~");
//            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String groupId = null;
//                    String profileimg = null;
//                    long now = System.currentTimeMillis();
//                    Date date = new Date(now);
//                    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
//                    String key;
//                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
//                    while (child.hasNext()) {
//                        //찾고자 하는 ID값은 key로 존재하는 값
//                        User user = child.next().getValue(User.class);
//                        if (user.getId().equals(currentUser.getUid())) {
//                            groupId = user.getGroupId();
//                            profileimg = user.getUserImage();
//                        }
//                    }
//                    // 키 생성 후 데이터 베이스에 저장
//                    groupReference = groupReference.child(groupId)
//                            .child("travelPhoto");
//                    key = groupReference.push().getKey();
//                    //  String id, String albumTitle, String albumDate, String mainImgPath, String profileImgPath, String albumLocation
//                    TravelAlbum albumData = new TravelAlbum(key, travelName.getText().toString(), CurDateFormat.format(date),
//                            "empty", profileimg, travelLocation.getText().toString());
//                    groupReference.child(key).setValue(albumData);
//                    items.add(0, albumData);
//                    recyclerAdapter.notifyDataSetChanged();
//
//                    Toast.makeText(getActivity(), "새 앨범이 추가되었습니다.", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        } else if (flag == GET_ALBUM_TOSERVER) {
//            Log.e(TAG, "앨범불러오기~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(final DataSnapshot dataSnapshot) {
//                    String groupId = null;
//                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
//                    while (child.hasNext()) {
//                        //찾고자 하는 ID값은 key로 존재하는 값
//                        User user = child.next().getValue(User.class);
//                        if (user.getId().equals(currentUser.getUid())) {
//                            groupId = user.getGroupId();
//                        }
//                    }
//                    Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>.. groupID; " + groupId);
//
//                    try {
//                        groupReference = database.getReference("groups");
//
//                        groupReference.child(groupId).child("travelPhoto").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
//                                while (child.hasNext()) {
//
//                                    TravelAlbum albumData = child.next().getValue(TravelAlbum.class);
//                                    items.add(0, albumData);
//                                    recyclerAdapter.notifyDataSetChanged();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                Log.e(TAG, ">>>>> onCancelled // database Error");
//                            }
//                        });
//                    } catch (NullPointerException e) {
//                        Log.e(TAG, ">>>>>>      " + "travel 앨범 불러올 때 데이터베이스에 널값이 뜸.");
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.e(TAG, ">>>>> onCancelled // database Error");
//                }
//            });
//
//        }
//    }
//
//    @OnClick(R.id.expandText)
//    void onExpandClick() {   // toggle expand and collapsing
//        expandableLayout.toggle();
//    }
//
//    @OnClick(R.id.expandAllow)
//    void onAllowClick() {
//        expandableLayout.toggle();
//    }
//
//    private void setInit(Bundle savedInstanceState) {
//
//        expandableLayout.setListener(new ExpandableLayoutListener() {
//            @Override
//            public void onAnimationStart() {
//
//            }
//
//            @Override
//            public void onAnimationEnd() {
//            }
//
//            // You can get notification that your expandable layout is going to open or close.
//            // So, you can set the animation synchronized with expanding animation.
//            @Override
//            public void onPreOpen() {
//            }
//
//            @Override
//            public void onPreClose() {
//            }
//
//            @Override
//            public void onOpened() {
//                expandAllow.setImageResource(R.drawable.ic_up_black);
//            }
//
//            @Override
//            public void onClosed() {
//                expandAllow.setImageResource(R.drawable.ic_down_black);
//            }
//        });
//
//        mapView.onCreate(savedInstanceState);
//        mapView.onResume();
//        mapView.getMapAsync(this);
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public void onMapReady(final GoogleMap map) {
//        // 내위치 퍼미션 확인
//        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // 내 위치를 가져오는 것을 허가하며, 버튼을 Enable
//            map.setMyLocationEnabled(true);
//            map.getUiSettings().setMyLocationButtonEnabled(true);
//            Log.i(TAG, "Permission success");
//        } else {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION} , 1);
//            map.setMyLocationEnabled(false);
//            map.getUiSettings().setMyLocationButtonEnabled(false);
//            Log.i(TAG, "Permission fail");
//        }
//
//        // 현재 등록한 카메라 셋팅
//        mLatLng = new LatLng(lat, lon);
////        map.addMarker(new MarkerOptions().position(mLatLng)
////                .title(name).snippet(address).draggable(true));
//        map.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
//        map.getUiSettings().setZoomControlsEnabled(true);
//        map.getUiSettings().setCompassEnabled(true);
//        map.animateCamera(CameraUpdateFactory.zoomTo(14));
//
////    }

//
//
//}
