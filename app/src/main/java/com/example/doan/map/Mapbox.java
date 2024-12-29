package com.example.doan.map;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doan.R;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.model.Pothole;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

import java.util.ArrayList;
import java.util.List;

public class Mapbox extends Fragment {

    private  FloatingActionButton myMapButton;
    private MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapbox, container, false);
        myMapButton=view.findViewById(R.id.myMapboxButton);
        mapView= view.findViewById(R.id.mapbox);
        setMylocationButton();
        return view;
    }
    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(15.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            myMapButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };


    public void setMylocationButton() {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(10.0).build());
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPulsingEnabled(true);
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        myMapButton.setOnClickListener(viewButton -> {
            locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).addOnMoveListener(onMoveListener);
            myMapButton.hide();
        });
    }

    public void drawAllPothole(){
        PotholeManager.getInstance().getALLPotholes(new PotholeManager.GetPotholeCallBack() {
            @Override
            public void onSuccess(List<Pothole> potholes) {
                addListOfPotholesToMap(potholes);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    public void addPotholeToMap(Context context, Pothole pothole) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pothole_waning_map);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        PointAnnotationOptions options = new PointAnnotationOptions().withIconImage(resizedBitmap)
                .withPoint(Point.fromLngLat(pothole.getLocation().getLongitude(), pothole.getLocation().getLatitude()));

        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

        requireActivity().runOnUiThread(()->{
            pointAnnotationManager.create(options);
        });
    }

    private void addListOfPotholesToMap(List<Pothole> potholes) {
        Bitmap bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.ic_pothole_waning_map);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        // Create a single instance of AnnotationPlugin and PointAnnotationManager
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

        // Add all potholes to the map
        List<PointAnnotationOptions> optionsList = new ArrayList<>();
        for (Pothole pothole : potholes) {
            PointAnnotationOptions options = new PointAnnotationOptions()
                    .withIconImage(resizedBitmap)
                    .withPoint(Point.fromLngLat(
                            pothole.getLocation().getLongitude(),
                            pothole.getLocation().getLatitude()));
            optionsList.add(options);
        }
        // Update the UI on the main thread
        requireActivity().runOnUiThread(() -> pointAnnotationManager.create(optionsList));
    }
}
