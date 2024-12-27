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
import com.example.doan.api.auth.AuthManager;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.dashboard.MainActivity;
import com.example.doan.map.*;
import com.example.doan.feature.DetectEngine;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;
import com.example.doan.setting.FragmentSetting;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_detect);
        isDetecting = false;
        btn_exitDetect =  findViewById(R.id.exitDetect);
        btn_startDetect = findViewById(R.id.startDectect);
        btn_settingDetect = findViewById(R.id.settingDetect);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        locationEngine = LocationEngineProvider.getBestLocationEngine(DetectActivity.this);
        calendar = Calendar.getInstance();
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
            }
            else if (isDetecting)
            {
                Log.d("__STOP MODEL", "Stopped Detecting");
                runOnUiThread(this::removeSensorDataFragment);
                sensorManager.unregisterListener(detectEngine.getSensorEventListener());
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
                    Log.d("__Pothole count", ""+potholeCount);
                    addFoundedPothole();
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
        java.util.Date utilDate = calendar.getTime();
        Date sqlDate = new Date(utilDate.getTime());
        Time sqlTime = new Time(utilDate.getTime());
        AppUser account = AuthManager.getInstance().getAccount();
        Pothole pothole = new Pothole(null, "High", new Pothole.Location(),account , account.getId());
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                Pothole.Location ph_location = new Pothole.Location();
                ph_location.setLatitude(location.getLatitude());
                ph_location.setLongitude(location.getLongitude());
                ph_location.setCountry("NONE");
                ph_location.setCity("NONE");
                ph_location.setStreet("NONE");
                pothole.setLocation(ph_location);
                pothole.setDateFound(sqlDate);
                pothole.setTimeFound(String.valueOf(sqlTime));
                runOnUiThread(()->{
                    Log.d("__DETECTION", "Pothole found! "+pothole.getLocation().getLongitude()+"/"
                            +pothole.getLocation().getLatitude());
                    Toast.makeText(DetectActivity.this, "Pothole found", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

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
        fragmentSensorData = new FragmentSensorData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
        );
        transaction.replace(R.id.topFragmentContainer, fragmentSensorData);
        transaction.commit();
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
        }
    }
}
