package com.example.doan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.doan.model.Pothole;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import androidx.annotation.NonNull;

import java.util.List;

public class MapManager {
    private final MapView mapView;
    private final Context context;
    private FloatingActionButton myLocationButton, navigationButton;
    private PointAnnotationManager pointAnnotationManager;
    private List<Pothole> potholesList;
    private LocationComponentPlugin locationComponentPlugin;
    private boolean ignoreNextQueryUpdate = false;
    private Point originSearch, destinationSearch;
    private boolean firstSearchOrigin = true;

    public MapManager(MapView mapView, Context context) {
        this.mapView = mapView;
        this.context = context;
        initialize();
    }

    private void initialize() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
            setupLocationComponent();
            setupAnnotationManager();
            setupListeners();
        });
    }

    private void setupLocationComponent() {
        locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPulsingEnabled(true);
    }

    private void setupAnnotationManager() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_location_pin);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, true);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
    }

    private void setupListeners() {
        // Listen for location updates and center the camera
        locationComponentPlugin.addOnIndicatorPositionChangedListener(point -> {
            if (!ignoreNextQueryUpdate) { // Ignore if the user has moved the map manually
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
                        .center(point)
                        .zoom(15.0)
                        .build());
            }
        });

        // Add a move listener to detect manual map movements
        getGestures(mapView).addOnMoveListener(onMoveListener);
    }


    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            //getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            ignoreNextQueryUpdate = true;
        }
        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }
        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
        }
    };

    public void setMyLocationButton(FloatingActionButton myLocationButton) {
        this.myLocationButton = myLocationButton;
        myLocationButton.setOnClickListener(view -> resetToCurrentLocation());
        myLocationButton.show();
    }

    public void setNavigationButton(FloatingActionButton navigationButton) {
        this.navigationButton = navigationButton;
        navigationButton.setOnClickListener(view -> navigateToDestination(destinationSearch));
    }

    public void addPotholesToMap(List<Pothole> potholeList) {
        potholesList = potholeList;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pothole_waning_map);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        for (Pothole pothole : potholeList) {
            PointAnnotationOptions options = new PointAnnotationOptions()
                    .withIconImage(resizedBitmap)
                    .withPoint(Point.fromLngLat(pothole.getLocation().getLongitude(), pothole.getLocation().getLatitude()));
            pointAnnotationManager.create(options);
        }
    }

    private void resetToCurrentLocation() {
        ignoreNextQueryUpdate = false;
        locationComponentPlugin.setEnabled(true);
        myLocationButton.hide();
    }

    private void navigateToDestination(Point destination) {
        // Navigation logic goes here
    }

    public MapView getMapView() {
        return mapView;
    }
}
