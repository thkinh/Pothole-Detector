package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Detect extends AppCompatActivity
{
    ImageButton btn_exitDetect, btn_startDetect, btn_settingDetect;
    boolean isDetecting ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_detect);

        isDetecting = false;
        btn_exitDetect =  findViewById(R.id.exitDetect);
        btn_startDetect = findViewById(R.id.startDectect);
        btn_settingDetect = findViewById(R.id.settingDetect);

        btn_exitDetect.setOnClickListener(view -> {
            Intent intent = new Intent(Detect.this, MainActivity.class);
            startActivity(intent);
        });

        btn_startDetect.setOnClickListener(view -> {
            if (!isDetecting) {
                loadSensorDataFragment();
                isDetecting = true;
            }
            else {
                removeSensorDataFragment();
                isDetecting = false;
            }
        });

        btn_settingDetect.setOnClickListener(view -> {
            FragmentSetting fragment = new FragmentSetting();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.topFragmentContainer, fragment);
            transaction.addToBackStack(null); // Optional: add to back stack to allow back navigation
            transaction.commit();
        });
    }

    private void loadSensorDataFragment() {
        FragmentSensorData fragment = new FragmentSensorData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,  // Enter animation
                R.anim.slide_in  // Exit animation
        );

        transaction.replace(R.id.topFragmentContainer, fragment);
        transaction.commit();
    }
    private void removeSensorDataFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentSensorData fragment = (FragmentSensorData) fragmentManager.findFragmentById(R.id.topFragmentContainer);
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Set custom animations for sliding out to the top
            transaction.setCustomAnimations(
                    R.anim.slide_out, // Enter animation (when fragment reappears)
                    R.anim.slide_out // Exit animation (when fragment disappears)
            );

            transaction.remove(fragment);
            transaction.commit();
        }
    }
}
