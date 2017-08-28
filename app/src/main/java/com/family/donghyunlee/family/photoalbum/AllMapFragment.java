package com.family.donghyunlee.family.photoalbum;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.MapItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by DONGHYUNLEE on 2017-08-24.
 */
/**
 * Google Map CallStack
 * 1. onCreate()
 * 2. onCreateView()
 * 3. onActivityCreated()
 * 4. onStart();
 * 5. onResume();
 * 5-2. onMapReady();
 * 6. onPause();
 * 7. onSaveInstanceState();
 * 8. onMapReady();
 */

public class AllMapFragment extends Fragment
        implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener
{


    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final String TAG = AllMapFragment.class.getSimpleName();
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 15000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 15000;

    private GoogleMap googleMap = null;
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private Geocoder mGeocoder;
    private final static int MAXENTRIES = 5;
    private LatLng[] LikelyLatLngs = null;
    private PlaceAutocompleteFragment autocompleteFragment;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference groupReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference profile_pathRef;
    private String storageMemoryFolder;

    private String groupId;
    private ArrayList<MapItem> items;
    private ViewGroup snackContainer;
    private boolean isCheck;


    private Marker marker;


    public static AllMapFragment newInstance() {
        Bundle args = new Bundle();

        AllMapFragment fragment = new AllMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCurrentLocation(Location location) {
        if ( location != null) {
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng( location.getLatitude(), location.getLongitude());
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_allmap , container, false);
        ButterKnife.bind(this, v);
        setInit();
        snackContainer = (ViewGroup) v.findViewById(R.id.snackbar_layout);
        mapView = (MapView)v.findViewById(R.id.allmap);
        mapView.getMapAsync(this);
        autocompleteFragment =
                (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);




        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Location location = new Location("");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                setCurrentLocation(location);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        return v;
    }
    @Override
    public boolean onMarkerClick(Marker marker) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        googleMap.animateCamera(center);

        return true;
    }


    private void setInit() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        // sharedreference에서 가져오기
        groupId = pref.getString("groupId", "");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getContext().getResources().getString(R.string.firebase_storage));
        storageMemoryFolder = getContext().getString(R.string.storage_albumimages_folder);
    }

    private Marker addMarker(MapItem mapItem, boolean isSelectedMarker) {

        Log.i(TAG, ">>>>>>>      2 ");
        profile_pathRef = storageRef.child(storageMemoryFolder + "/" + mapItem.getAlbumImage());
        LatLng position = new LatLng(mapItem.getMakerLat(), mapItem.getMakerLon());
        String title = mapItem.getAlbumTitle();
        Log.i(TAG, ">>>>>>>      3 position:   " + position +"title:    " + position.latitude +"/" + position.longitude);
//        markerTitle.setText(title);
//        Glide.with(getContext()).using(new FirebaseImageLoader()).load(profile_pathRef).centerCrop()
//                .crossFade().into(markerImage);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title);
        markerOptions.position(position);
        markerOptions.snippet(mapItem.getLocation());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_goal));
        Log.i(TAG, ">>>>>>>>>>>>>>>      " + markerOptions);
        marker = googleMap.addMarker(markerOptions);
        marker.setTag(mapItem);

        return marker;
    }
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void getAlbum(){
        items = new ArrayList<>();
        groupReference = database.getReference("groups").child(groupId).child("memoryPhotoMap");
        groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while(child.hasNext()){
                    MapItem mapItem = child.next().getValue(MapItem.class);
                    items.add(mapItem);
                        addMarker(mapItem, false);

                    Log.i(TAG, ">>>>>>>      1 items_size:" + items.size());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void onDestroyView()
    {
        super.onDestroyView();
        if(googleApiClient!=null) {
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
        }
        getActivity().getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
    }


    @OnClick(R.id.allmap_back)
    void backmapFun() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(AllMapFragment.this).commit();
        fragmentManager.popBackStack();
    }

    private void showMessage(String msg) {

        Snackbar.make(snackContainer, msg, Snackbar.LENGTH_SHORT).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

        if ( googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if ( googleApiClient != null)
            googleApiClient.connect();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if ( googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();

        if ( googleApiClient != null ) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if ( googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // OnMapReadyCallback implements 해야 mapView.getMapAsync(this); 사용가능. this 가 OnMapReadyCallback

        this.googleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에 지도의 초기위치를 서울로 이동
        setCurrentLocation(null);

        //나침반이 나타나도록 설정
        googleMap.getUiSettings().setCompassEnabled(true);
        // 매끄럽게 이동함
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        getAlbum();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {
                String text = "[마커 클릭 이벤트] latitude ="
                        + marker.getPosition().latitude + ", longitude ="
                        + marker.getPosition().longitude;
                MapItem temp_mapItem = (MapItem) marker.getTag();

                Intent intent = new Intent(getActivity(), InAlbum.class);
                intent.putExtra("TITLE", temp_mapItem.getAlbumTitle());
                intent.putExtra("ALBUM_ID", temp_mapItem.getAlbum_key());
                intent.putExtra("ALBUM_POSITION", 0);
                startActivity(intent);

                return false;
            }
        });
        //  API 23 이상이면 런타임 퍼미션 처리 필요
        if (Build.VERSION.SDK_INT >= M) {
            // 사용권한체크
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

            if ( hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                //사용권한이 없을경우
                //권한 재요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //사용권한이 있는경우
                if ( googleApiClient == null) {
                    buildGoogleApiClient();
                }

                if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        } else {

            if ( googleApiClient == null) {
                buildGoogleApiClient();
            }

            googleMap.setMyLocationEnabled(true);
        }


    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();
        googleApiClient.connect();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if ( !checkLocationServicesStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 수정하십시오.");
            builder.setCancelable(true);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if ( Build.VERSION.SDK_INT >= M) {
            if ( ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, locationRequest, this);

            this.googleMap.getUiSettings().setCompassEnabled(true);
            this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        if ( cause ==  CAUSE_NETWORK_LOST )
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED )
            Log.e(TAG,"onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Location location = new Location("");
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude((DEFAULT_LOCATION.longitude));

        setCurrentLocation(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged call..");
        searchCurrentPlaces();
    }

    private void searchCurrentPlaces() {
        @SuppressWarnings("MissingPermission")
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(googleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>(){

            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                int i = 0;
                LikelyLatLngs = new LatLng[MAXENTRIES];

                for(PlaceLikelihood placeLikelihood : placeLikelihoods) {
                    LikelyLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                    i++;
                    if(i > MAXENTRIES - 1 ) {
                        break;
                    }
                }
                placeLikelihoods.release();
                Location location = new Location("");
                location.setLatitude(LikelyLatLngs[0].latitude);
                location.setLongitude(LikelyLatLngs[0].longitude);

                setCurrentLocation(location);
            }
        });

    }
}

