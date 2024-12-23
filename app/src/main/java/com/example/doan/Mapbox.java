package com.example.doan;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.maps.MapView;

public class Mapbox extends Fragment {
    private MapView mapView;
    private MapManager mapManager;
    private FloatingActionButton myLocationButton, navigationButton;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapbox);
        mapManager = new MapManager(mapView, requireContext());
        myLocationButton = view.findViewById(R.id.mylocationButton);

        mapManager.setMyLocationButton(myLocationButton);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
