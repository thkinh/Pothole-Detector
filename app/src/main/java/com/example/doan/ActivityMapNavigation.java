package com.example.doan;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.BuildConfig;
import com.google.android.gms.maps.GoogleMap.CameraPerspective;
import com.google.android.libraries.navigation.ListenableResultFuture;
import com.google.android.libraries.navigation.NavigationApi;
import com.google.android.libraries.navigation.Navigator;
import com.google.android.libraries.navigation.RoutingOptions;
import com.google.android.libraries.navigation.SimulationOptions;
import com.google.android.libraries.navigation.SupportNavigationFragment;
import com.google.android.libraries.navigation.Waypoint;

/**
 * An activity that displays a map and a navigation UI, guiding the user from their current location
 * to a single, given destination.
 */
public class ActivityMapNavigation extends AppCompatActivity {

    private static final String TAG = ActivityMapNavigation.class.getSimpleName();
    private Navigator mNavigator;
    private SupportNavigationFragment mNavFragment;
    private RoutingOptions mRoutingOptions;

    // Define the Sydney Opera House by specifying its place ID.
    private static final String SYDNEY_OPERA_HOUSE = "ChIJ3S-JXmauEmsRUcIaWtf4MzE";

    // Set fields for requesting location permission.
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_mapnavigation);

        // Initialize the Navigation SDK.
        initializeNavigationSdk();
    }

    /**
     * Starts the Navigation SDK and sets the camera to follow the device's location. Calls the
     * navigateToPlace() method when the navigator is ready.
     */
    private void initializeNavigationSdk() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (!mLocationPermissionGranted) {
            displayMessage(
                    "Error loading Navigation SDK: " + "The user has not granted location permission.");
            return;
        }

        // Get a navigator.
        NavigationApi.getNavigator(
                this,
                new NavigationApi.NavigatorListener() {
                    /** Sets up the navigation UI when the navigator is ready for use. */
                    @Override
                    public void onNavigatorReady(Navigator navigator) {
                        displayMessage("Navigator ready.");
                        mNavigator = navigator;
                        mNavFragment =
                                (SupportNavigationFragment)
                                        getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);

                        // Set the last digit of the car's license plate to get route restrictions
                        // in supported countries. (optional)
                        // mNavigator.setLicensePlateRestrictionInfo(getLastDigit(), "BZ");

                        // Set the camera to follow the device location with 'TILTED' driving view.
                        if (ActivityCompat.checkSelfPermission(ActivityMapNavigation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityMapNavigation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mNavFragment.getMapAsync(
                                googleMap -> googleMap.followMyLocation(CameraPerspective.TILTED));

                        // Set the travel mode (DRIVING, WALKING, CYCLING, or TWO_WHEELER).
                        mRoutingOptions = new RoutingOptions();
                        mRoutingOptions.travelMode(RoutingOptions.TravelMode.DRIVING);

                        // Navigate to a place, specified by Place ID.
                        navigateToPlace(SYDNEY_OPERA_HOUSE, mRoutingOptions);
                    }

                    /**
                     * Handles errors from the Navigation SDK.
                     *
                     * @param errorCode The error code returned by the navigator.
                     */
                    @Override
                    public void onError(@NavigationApi.ErrorCode int errorCode) {
                        switch (errorCode) {
                            case NavigationApi.ErrorCode.NOT_AUTHORIZED:
                                displayMessage(
                                        "Error loading Navigation SDK: Your API key is "
                                                + "invalid or not authorized to use the Navigation SDK.");
                                break;
                            case NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED:
                                displayMessage(
                                        "Error loading Navigation SDK: User did not accept "
                                                + "the Navigation Terms of Use.");
                                break;
                            case NavigationApi.ErrorCode.NETWORK_ERROR:
                                displayMessage("Error loading Navigation SDK: Network error.");
                                break;
                            case NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING:
                                displayMessage(
                                        "Error loading Navigation SDK: Location permission " + "is missing.");
                                break;
                            default:
                                displayMessage("Error loading Navigation SDK: " + errorCode);
                        }
                    }
                });
    }

    /**
     * Requests directions from the user's current location to a specific place (provided by the
     * Google Places API).
     */
    private void navigateToPlace(String placeId, RoutingOptions travelMode) {
        Waypoint destination;
        try {
            destination = Waypoint.builder().setPlaceIdString(placeId).build();
        } catch (Waypoint.UnsupportedPlaceIdException e) {
            displayMessage("Error starting navigation: Place ID is not supported.");
            return;
        }

        // Create a future to await the result of the asynchronous navigator task.
        ListenableResultFuture<Navigator.RouteStatus> pendingRoute =
                mNavigator.setDestination(destination, travelMode);

        // Define the action to perform when the SDK has determined the route.
        pendingRoute.setOnResultListener(
                new ListenableResultFuture.OnResultListener<Navigator.RouteStatus>() {
                    @Override
                    public void onResult(Navigator.RouteStatus code) {
                        switch (code) {
                            case OK:
                                // Hide the toolbar to maximize the navigation UI.
                                if (getActionBar() != null) {
                                    getActionBar().hide();
                                }

                                // Enable voice audio guidance (through the device speaker).
                                mNavigator.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE);

                                // Simulate vehicle progress along the route for demo/debug builds.
                                if (BuildConfig.DEBUG) {
                                    mNavigator
                                            .getSimulator()
                                            .simulateLocationsAlongExistingRoute(
                                                    new SimulationOptions().speedMultiplier(5));
                                }

                                // Start turn-by-turn guidance along the current route.
                                mNavigator.startGuidance();
                                break;
                            // Handle error conditions returned by the navigator.
                            case NO_ROUTE_FOUND:
                                displayMessage("Error starting navigation: No route found.");
                                break;
                            case NETWORK_ERROR:
                                displayMessage("Error starting navigation: Network error.");
                                break;
                            case ROUTE_CANCELED:
                                displayMessage("Error starting navigation: Route canceled.");
                                break;
                            default:
                                displayMessage("Error starting navigation: " + String.valueOf(code));
                        }
                    }
                });
    }

    /** Handles the result of the request for location permissions. */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                // If request is canceled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    /**
     * Shows a message on screen and in the log. Used when something goes wrong.
     *
     * @param errorMessage The message to display.
     */
    private void displayMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.d(TAG, errorMessage);
    }
}