package com.example.doan;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LogicFirebase {

    DatabaseReference currentReference;
    DatabaseReference potholeReference;
    FirebaseDatabase database;

    public LogicFirebase(){

        currentReference =FirebaseDatabase.getInstance().getReference("Current Location");

        potholeReference =FirebaseDatabase.getInstance().getReference("Pothole Location");

        database=FirebaseDatabase.getInstance();
    }

    public void WriteCurrentLocation(Location location) {
        HashMap<String, Double> quoteHashmap = new HashMap<>();
        quoteHashmap.put("Latitude", (location.getLatitude()));
        quoteHashmap.put("Longitude", location.getLongitude());
        DatabaseReference quoteRef = database.getReference("Current Location");

        quoteRef.child("Current").setValue(quoteHashmap);
//        quoteRef.child("Current").setValue(quoteHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(Maps.this, "Added",Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void WritePotholeLocation(Location location) {
        HashMap<String, Double> quoteHashmap = new HashMap<>();
        quoteHashmap.put("Latitude", (location.getLatitude()));
        quoteHashmap.put("Longitude", location.getLongitude());
        DatabaseReference quoteRef = database.getReference("Pothole Location");
        quoteRef.child("1").setValue(quoteHashmap);
//        quoteRef.child("1").setValue(quoteHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(Maps.this, "Added",Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void LoadNewPotholesFromFirebase(GoogleMap map) {
        potholeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String region = snapshot.child("Region").getValue(String.class);
                    Double lat = snapshot.child("Latitude").getValue(Double.class);
                    Double lng = snapshot.child("Longitude").getValue(Double.class);

                    LatLng potholeLocation = new LatLng(lat, lng);
                    map.addMarker(new MarkerOptions().position(potholeLocation).title("Pothole"));

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


    public void LoadCurrentLocationFromFirebase(GoogleMap map) {
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
