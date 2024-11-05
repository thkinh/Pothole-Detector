package com.example.doan;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.firebase.database.core.Tag;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Implement OnMapReadyCallback.
public class Maps extends AppCompatActivity
        implements
        OnMapReadyCallback,
        OnRequestPermissionsResultCallback {
    ImageButton btn_current;
    private EditText et_search;
    LogicFirebase firebase;
    Location currentLocation;
    FusedLocationProviderClient fusedClient;
    private final int FINE_PERMISSION_CODE = 1;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     * #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;
    SupportMapFragment mapFragment;
    LatLng myLocation;
    GoogleMap googleMap;
    private static final String MAPFRAGMENT_BUNDLE_KEY = "Map Fragment bundle key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_maps);
        Bundle mapFragmentBundle = null;
        //onCreate() method is called whether the system is creating a new instance of your activity or recreating a previous one
        //If it is null, then the system is creating a new instance of the activity, instead of restoring a previous one that was destroyed.
        if(savedInstanceState != null){
            mapFragmentBundle = savedInstanceState.getBundle(MAPFRAGMENT_BUNDLE_KEY);
        }
        btn_current = findViewById(R.id.btn_current);
        et_search = (EditText)findViewById(R.id.input_search);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.onCreate(mapFragmentBundle);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        init();
    }

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;

                    firebase = new LogicFirebase(Maps.this);
//         Initialize Firebase Realtime Database
                    mapFragment.getMapAsync(Maps.this);
                }
            }
        });
    }

    private void init(){
        Log.d(TAG,"init:initializing");

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()== keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
                    geoLocate();

                }
                return false;
            }
        });
    }

    private void geoLocate(){
        String searchString = et_search.getText().toString();
        Geocoder geocoder = new Geocoder(Maps.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        }catch (IOException e){
            Log.d(TAG,"fail" + e.getMessage());
        }
        Address address =list.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        MarkerOptions markerOptions= new MarkerOptions()
                .position(latLng)
                .title("Point");
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    }

    //recover your saved state from the Bundle
    //call before destroy activity
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapFragmentBundle = outState.getBundle(MAPFRAGMENT_BUNDLE_KEY);
        if(mapFragmentBundle == null){
            mapFragmentBundle=new Bundle();
            outState.putBundle(MAPFRAGMENT_BUNDLE_KEY,mapFragmentBundle);
        }
    }

    // Get a handle to the GoogleMap object and display marker.
    //Reference for implements my location https://developers.google.com/maps/documentation/android-sdk/examples/my-location
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap=map;
        // and move the map's camera to the same location.

        enableMyLocation();
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        googleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();

            }
        });
        firebase.LoadPotholesFromFirebase(googleMap);
        init();
    }
    private void CurrentPlace(){
        Location location = currentLocation;
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            LatLng myPosition = new LatLng(latitude, longitude);


            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 16);
            googleMap.animateCamera(yourLocation);
        }
    }
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        CurrentPlace();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // ...
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    private Context getContext(){
        return this;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapFragment.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapFragment.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}