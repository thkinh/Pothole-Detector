package com.example.doan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.doan.R;
import com.example.doan.RouteFragment;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

public class FindRouteActivity extends AppCompatActivity {

    private Button findRouteButton;
    private EditText editTextStart;
    private EditText editTextDestination;

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_map);

        findRouteButton = findViewById(R.id.btn_findRoute);
        editTextStart = findViewById(R.id.editText_startingPoint);
        editTextDestination = findViewById(R.id.editText_destination);

        mapView = findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

        findRouteButton.setOnClickListener(v -> {
            String startingPoint = editTextStart.getText().toString();
            String destination = editTextDestination.getText().toString();

            // Fetch route information
            fetchRouteInfo(startingPoint, destination);
        });
    }

    private void fetchRouteInfo(String startingPoint, String destination) {
        // Use Google Maps API or Directions API to fetch route data
        // For demonstration, let's assume we have the data
        String distance = "8.3 km";
        String time = "13 minutes";

        // Display the RouteFragment
        displayRouteFragment(distance, time);
    }

    private void displayRouteFragment(String distance, String time) {
        RouteFragment routeFragment = new RouteFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, routeFragment);
        fragmentTransaction.commit();

        // Update the fragment with route information
        fragmentManager.executePendingTransactions();
        routeFragment.updateRouteInfo(distance, time);
    }
}
