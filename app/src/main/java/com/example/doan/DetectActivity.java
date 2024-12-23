package com.example.doan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.doan.feature.DetectEngine;
import com.example.doan.model.Pothole;
import com.example.doan.setting.FragmentSetting;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import java.io.IOException;

public class DetectActivity extends AppCompatActivity
{
    private DetectEngine detectEngine;
    ImageButton btn_exitDetect, btn_startDetect, btn_settingDetect;
    boolean isDetecting ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_detect);
        EdgeToEdge.enable(this);
        isDetecting = false;
        btn_exitDetect =  findViewById(R.id.exitDetect);
        btn_startDetect = findViewById(R.id.startDectect);
        btn_settingDetect = findViewById(R.id.settingDetect);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelerometer == null) {
            Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_LONG).show();
            return;
        }

        btn_exitDetect.setOnClickListener(view -> {
            Intent intent = new Intent(DetectActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btn_startDetect.setOnClickListener(view -> {
            if (!isDetecting) {
                StartDetect();  // Add this to initialize DetectEngine
                runOnUiThread(this::loadSensorDataFragment);
                isDetecting = true;
            } else {
                removeSensorDataFragment();
                detectEngine.close();
                isDetecting = false;
            }
        });

        btn_settingDetect.setOnClickListener(view -> {
            FragmentSetting fragment = new FragmentSetting();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.topFragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        AddFragmentMap();
    }


    public void AddFragmentMap(){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainlayout,new Mapbox());
        fragmentTransaction.commit();
    }

    private void StartDetect(){
        try {
            detectEngine = new DetectEngine(DetectActivity.this, 60, new DetectEngine.DetectionCallback() {
                @Override
                public void onPotholeDetected(int potholeCount) {
                    Log.d("Pothole count", ""+potholeCount);
                    addFoundedPothole();
                }
                @Override
                public void onSafe() {
                }
            });
            sensorManager.registerListener(detectEngine.getSensorEventListener(), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        catch (IOException e){
            Toast.makeText(this, "Failed to initialize model: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void addFoundedPothole(){
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(DetectActivity.this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                Pothole pothole = new Pothole();
                Pothole.Location location1 = new Pothole.Location();
                location1.setLatitude(location.getLatitude());
                location1.setLongitude(location.getLongitude());
                location1.setCountry("None");
                location1.setCity("None");
                pothole.setLocation(location1);
                Log.d("__DETECTION", "Pothole found! "+pothole.getLocation().getLongitude()+"/"
                        +pothole.getLocation().getLatitude());
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Location Failure", exception.getMessage());
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
        }
    }


    private void loadSensorDataFragment() {
        FragmentSensorData fragment = new FragmentSensorData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_in
        );

        transaction.replace(R.id.topFragmentContainer, fragment);
        transaction.commit();
    }
    private void removeSensorDataFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentSensorData fragment = (FragmentSensorData) fragmentManager.findFragmentById(R.id.topFragmentContainer);
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_out, // Enter animation (when fragment reappears)
                    R.anim.slide_out // Exit animation (when fragment disappears)
            );
            transaction.remove(fragment);
            transaction.commit();
        }
    }
}
