package com.example.doan;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.auth.AuthManager;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;

import java.util.List;

public class DemoActivity extends AppCompatActivity {

    private Button button_get;
    private Button button_add;
    private Button btn_getALL;
    private Button btn_addDistance;
    private PotholeManager potholeManager;
    private AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_demoapipotholes);
        EdgeToEdge.enable(this);

        button_get = findViewById(R.id.btn_getPotholes);
        button_add = findViewById(R.id.add_ph);
        btn_getALL = findViewById(R.id.get_globalPotholes);
        btn_addDistance =findViewById(R.id.add_distance);

        button_get.setOnClickListener(view -> handleGet());
        button_add.setOnClickListener(view -> handleAdd());
        btn_getALL.setOnClickListener(view -> handle_getALL());
        btn_addDistance.setOnClickListener(view -> handle_addDistance());

        potholeManager = PotholeManager.getInstance();

    }

    private void handle_addDistance(){
        Long distance = 100L;
        authManager = AuthManager.getInstance();
        authManager.updateDistance(authManager.getAccount().getId(), distance, new AuthManager.UpdateDistanceCallBack() {
            @Override
            public void onSuccess(Integer id) {
                runOnUiThread(() ->{
                    Toast.makeText(DemoActivity.this, "Updated for user "+id, Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(DemoActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show());
                Log.e("FAILED: ", errorMessage);
            }
        });
    }

    //Handle get for this single user
    private void handleGet(){
        AppUser user = AuthManager.getInstance().getAccount();
        potholeManager.getPotholes(user, new PotholeManager.GetPotholeCallBack() {
            @Override
            public void onSuccess(List<Pothole> potholes) {
                runOnUiThread(() ->{
                    Toast.makeText(DemoActivity.this, "Fetched " + potholes.size(), Toast.LENGTH_SHORT).show();
                });
                for (Pothole pothole : potholes)
                {
                    Log.d("List potholes: ", pothole.getLocation().getLatitude()+"/"+pothole.getLocation().getLongitude());
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(DemoActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show());
                Log.e("FAILED: ", errorMessage);
            }
        });
    }

    private void handleAdd(){
        Pothole pothole = new Pothole();
        AppUser currentUser = AuthManager.getInstance().getAccount();
        pothole.setAppUser(currentUser);
        pothole.setSeverity("Normal");


        Pothole.Location location = new Pothole.Location();
        location.setLatitude(0.0);  location.setLongitude(0.0);
        location.setCity("None");   location.setCountry("None");
        pothole.setLocation(location);

        potholeManager.addPothole(pothole, new PotholeManager.AddPotholeCallBack() {
            @Override
            public void onSuccess(Pothole message) {
                runOnUiThread(()->{
                    Toast.makeText(DemoActivity.this, "Hell yeah " , Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(DemoActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show());
                Log.e("FAILED: ", errorMessage);
            }
        });
    }

    private void handle_getALL(){
        potholeManager.getALLPotholes(new PotholeManager.GetPotholeCallBack() {
            @Override
            public void onSuccess(List<Pothole> potholes) {
                Toast.makeText(DemoActivity.this, "Fetched " + potholes.size(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(DemoActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show());
                Log.e("FAILED: ", errorMessage);
            }
        });
    }
}
