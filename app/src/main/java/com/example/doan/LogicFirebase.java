package com.example.doan;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;


import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class LogicFirebase {

    DocumentReference potholeReference;
    FirebaseFirestore database;
    Context Maps;

    public LogicFirebase(Context context){

        database=FirebaseFirestore .getInstance();
        Maps = context;
    }

    public void StorePotholesToFirebase(Location location) {

        FirestoreClass.Pothole pothole = new FirestoreClass.Pothole(location.getLatitude(), location.getLongitude(), "");

        database.collection("Pothole")
                .add(pothole);
    }


    public void LoadPotholesFromFirebase(GoogleMap map) {
        database.collection("Pothole")
                .addSnapshotListener((querySnapshot, error) -> {
                    assert querySnapshot != null;
                    for(QueryDocumentSnapshot item : querySnapshot) {
                        LatLng location = new LatLng(item.getDouble("latitude"),
                                item.getDouble("longitude"));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        Objects.requireNonNull(map.addMarker(markerOptions))
                                .setTitle("Pothole");

                    }
                });
    }

    private BitmapDescriptor bitmapDescriptor (Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0,
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
