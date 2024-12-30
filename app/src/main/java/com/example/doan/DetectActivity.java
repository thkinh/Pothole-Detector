package com.example.doan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.dashboard.MainActivity;
import com.example.doan.map.*;
import com.example.doan.feature.DetectEngine;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;
import com.example.doan.setting.FragmentSetting;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetectActivity extends AppCompatActivity
{
    private DetectEngine detectEngine;
    ImageButton btn_exitDetect, btn_startDetect, btn_settingDetect;
    boolean isDetecting ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FragmentSensorData fragmentSensorData;
    private FragmentManager fragmentManager;
    private LocationEngine locationEngine;
    private Calendar calendar;
    private Mapbox mapbox;
    private FloatingActionButton btn_add_manually;
    private LinearLayout detectController;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_detect);
        isDetecting = false;
        btn_exitDetect =  findViewById(R.id.exitDetect);
        btn_startDetect = findViewById(R.id.startDectect);
        btn_settingDetect = findViewById(R.id.settingDetect);
        btn_add_manually = findViewById(R.id.add_ph_manually);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        locationEngine = LocationEngineProvider.getBestLocationEngine(DetectActivity.this);
        if (accelerometer == null) {
            Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_LONG).show();
            return;
        }
        btn_add_manually.setOnClickListener(view -> {
            addFoundedPothole();
        });

        btn_exitDetect.setOnClickListener(view -> {
            Intent intent = new Intent(DetectActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btn_startDetect.setOnClickListener(view -> {
            if (!isDetecting) {
                StartDetect();  // Add this to initialize DetectEngine
                runOnUiThread(this::loadSensorDataFragment);
                isDetecting = true;
            }
            else if (isDetecting) {
                Log.d("__STOP MODEL", "Stopped Detecting");
                runOnUiThread(this::removeSensorDataFragment);
                sensorManager.unregisterListener(detectEngine.getSensorEventListener());
                isDetecting = false;
            }
        });
        detectController = findViewById(R.id.ControlDetect);

        btn_settingDetect.setOnClickListener(view -> {
            FragmentSetting fragment = new FragmentSetting();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.topFragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });
        AddFragmentMap();
        mapbox.drawAllPothole();
    }




    private void AddFragmentMap(){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapbox = new Mapbox();
        fragmentTransaction.add(R.id.mainlayout, mapbox);
        fragmentTransaction.commit();
    }

    private void StartDetect(){
        try {
            detectEngine = new DetectEngine(DetectActivity.this, 60, new DetectEngine.DetectionCallback() {
                @Override
                public void onPotholeDetected(int potholeCount) {
                    Log.d("__Pothole count", ""+potholeCount);
                    fragmentSensorData.updateFound(potholeCount);
                    addFoundedPothole();
                    NotifyManager.showVibrate(DetectActivity.this);
                }
                @Override
                public void onSafe() {
                    double meanZ = detectEngine.getMeanZ();
                    double sdZ = detectEngine.getSdZ();
                    // Limit to 2 decimal places
                    String formattedMeanZ = String.format("%.5f", meanZ);
                    String formattedSdZ = String.format("%.5f", sdZ);
                    fragmentSensorData.updateMeanValue(formattedMeanZ);
                    fragmentSensorData.updateSdValue(formattedSdZ);
                }
            });
            sensorManager.registerListener(detectEngine.getSensorEventListener(), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        catch (IOException e){
            Toast.makeText(this, "Failed to initialize model: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("MissingPermission")
    private void addFoundedPothole(){
        calendar = Calendar.getInstance();
        java.util.Date utilDate = calendar.getTime();
        Date sqlDate = new Date(utilDate.getTime());
        Time sqlTime = new Time(utilDate.getTime());
        AppUser account = AuthManager.getInstance().getAccount();
        Pothole pothole = new Pothole(null, "High", new Pothole.Location(),account , account.getId());
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location == null) {
                    runOnUiThread(() -> {
                        finish();
                        Toast.makeText(DetectActivity.this,
                                "Unable to detect location. Please enable location services.",
                                Toast.LENGTH_LONG).show();
                    });
                    return;
                }
                Pothole.Location ph_location = new Pothole.Location();
                pothole.setDateFound(sqlDate);
                pothole.setTimeFound(sqlTime.toString());
                ph_location.setLatitude(location.getLatitude());
                ph_location.setLongitude(location.getLongitude());
                pothole.setLocation(ph_location);
                getPlaceFromPoint(Point.fromLngLat(ph_location.getLongitude(), ph_location.getLatitude()), pothole);
                runOnUiThread(()->{
                    Log.d("__DETECTION", "Pothole found! "+pothole.getLocation().getLongitude()+"/"
                            +pothole.getLocation().getLatitude());
                    Toast.makeText(DetectActivity.this, "Pothole found", Toast.LENGTH_SHORT).show();
                });

            }
            @Override
            public void onFailure(@NonNull Exception exception) {
                runOnUiThread(() -> {
                    Toast.makeText(DetectActivity.this,
                            "Failed to retrieve location: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    String place = "";
    private void getPlaceFromPoint(Point point, Pothole pothole){
        MapboxGeocoding client = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(point)
                .build();


        client.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call,
                                   Response<GeocodingResponse> response) {
                if (response.body() != null) {
                    List<CarmenFeature> results = response.body().features();
                    Pothole.Location location = new Pothole.Location();
                    // Get the first Feature from the successful geocoding response
                    CarmenFeature feature = results.get(0);
                    place = feature.placeName();
                    Log.e("__ADDRESS",place == null? feature.toString() :"NO");
                    if (place != null)
                    {
                        Log.e("__PLACE", place);
                        String[] fields = place.split(",");
                        location.setLongitude(point.longitude());
                        location.setLatitude(point.latitude());
                        location.setCountry(fields[4].trim());
                        location.setCity(fields[3].trim());
                        location.setStreet(fields[1].trim());
                        pothole.setLocation(location);
                        handle_addPothole(pothole);
                        mapbox.addPotholeToMap(DetectActivity.this,pothole);
                    }
                }else{
                    Toast.makeText(DetectActivity.this,"Error network",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                Log.e("GeocodingError", "Error during geocoding: " + t.getMessage());

                // Hiển thị thông báo với lỗi chi tiết
                Toast.makeText(DetectActivity.this, "Fail Search Place: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void handle_addPothole(Pothole pothole){
        PotholeManager.getInstance().addPothole(pothole, new PotholeManager.AddPotholeCallBack() {
            @Override
            public void onSuccess(Pothole pothole) {
                Log.d("__API_DEBUG", "Nice");
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.d("__API_DEBUG", errorMessage);
                Toast.makeText(DetectActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (detectEngine != null) {
            sensorManager.registerListener(detectEngine.getSensorEventListener(), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (detectEngine != null) {
            sensorManager.unregisterListener(detectEngine.getSensorEventListener());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (detectEngine != null) {
            detectEngine.close();
            sensorManager.unregisterListener(detectEngine.getSensorEventListener());
            detectEngine = null;
            mapbox = null;
        }
    }


    private void loadSensorDataFragment() {
        fragmentSensorData = new FragmentSensorData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
        );
        transaction.replace(R.id.topFragmentContainer, fragmentSensorData);
        transaction.commit();
        btn_startDetect.setImageResource(R.drawable.ic_close);
    }
    private void removeSensorDataFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentSensorData fragment = (FragmentSensorData) fragmentManager.findFragmentById(R.id.topFragmentContainer);
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in, // Enter animation (when fragment reappears)
                    R.anim.slide_out // Exit animation (when fragment disappears)
            );
            transaction.remove(fragment);
            transaction.commit();
            btn_startDetect.setImageResource(R.drawable.ic_play);
        }
    }
}
