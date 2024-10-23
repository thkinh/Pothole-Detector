package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

// Implement OnMapReadyCallback.
public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase database;
    DatabaseReference currentReference;
    DatabaseReference potholeReference;

    private final int FINE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_maps);

        currentReference = FirebaseDatabase.getInstance().getReference("Current Location");
        potholeReference = FirebaseDatabase.getInstance().getReference("Pothole Location");
        database= FirebaseDatabase.getInstance();

//         Initialize Firebase Realtime Database
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
    }

    private void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},FINE_PERMISSION_CODE );
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    WriteCurrentLocation(location);
                    WritePotholeLocation(location);
                    // Get a handle to the fragment and register the callback.

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(Maps.this);
                }
            }
        });
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // and move the map's camera to the same location.
        map = googleMap;
        LoadNewPotholesFromFirebase();
        LoadCurrentLocationFromFirebase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(this,"Location Permission to denied ",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void WriteCurrentLocation(Location location){
        HashMap<String,Double> quoteHashmap= new HashMap<>();
        quoteHashmap.put("Latitude",(location.getLatitude()));
        quoteHashmap.put("Longitude",location.getLongitude());
        DatabaseReference quoteRef = database.getReference("Current Location");

        quoteRef.child("Current").setValue(quoteHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Maps.this, "Added",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void WritePotholeLocation(Location location){
        HashMap<String,Double> quoteHashmap= new HashMap<>();
        quoteHashmap.put("Latitude",(location.getLatitude()));
        quoteHashmap.put("Longitude",location.getLongitude());
        DatabaseReference quoteRef = database.getReference("Pothole Location");
        quoteRef.child("1").setValue(quoteHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Maps.this, "Added",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadNewPotholesFromFirebase() {
        potholeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String region = snapshot.child("Region").getValue(String.class);
                    Double lat = snapshot.child("Latitude").getValue(Double.class);
                    Double lng = snapshot.child("Longitude").getValue(Double.class);

                    LatLng potholeLocation = new LatLng(lat, lng);
                    map.addMarker(new MarkerOptions().position(potholeLocation).title("Pothole"));

                    LatLng potholeLocation1= new LatLng(0, 0);
                    map.addMarker(new MarkerOptions().position(potholeLocation1).title("Pothole 1"));

                    map.moveCamera(CameraUpdateFactory.newLatLng(potholeLocation));
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(potholeLocation, 8));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }


    private void LoadCurrentLocationFromFirebase() {
        currentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double lat = snapshot.child("Latitude").getValue(Double.class);
                    Double lng = snapshot.child("Longitude").getValue(Double.class);

                    LatLng potholeLocation = new LatLng(lat, lng);
                    map.addMarker(new MarkerOptions().position(potholeLocation).title("Current"));

                    map.moveCamera(CameraUpdateFactory.newLatLng(potholeLocation));
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(potholeLocation, 14));

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }
}