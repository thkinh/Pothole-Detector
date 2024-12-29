package com.example.doan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.auth.AuthManager;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.feature.Storage;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;
import com.example.doan.setting.EditProfileActivity;
import com.example.doan.setting.ProfileActivity;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

    private Button button_get;
    private Button button_add;
    private Button btn_getALL;
    private Button btn_addDistance;
    private Button btn_delete;
    private EditText editText_phID;
    private PotholeManager potholeManager;
    private AuthManager authManager;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_demoapipotholes);
        EdgeToEdge.enable(this);

        button_get = findViewById(R.id.btn_getPotholes);
        button_add = findViewById(R.id.add_ph);
        btn_getALL = findViewById(R.id.get_globalPotholes);
        btn_addDistance = findViewById(R.id.add_distance);
        btn_delete = findViewById(R.id.demo_deletePH);
        editText_phID = findViewById(R.id.demo_edt_potholeid);


        button_get.setOnClickListener(view -> handleGet());
        button_add.setOnClickListener(view -> handleUpload());
        btn_getALL.setOnClickListener(view -> handle_getALL());
        btn_addDistance.setOnClickListener(view -> handle_addDistance());
        btn_delete.setOnClickListener(view -> handleDelte());

        potholeManager = PotholeManager.getInstance();

        registerResult();
    }

    private void handleDelte(){
        Integer pothole_id = Integer.valueOf(editText_phID.getText().toString());
        potholeManager.deleteDuplicatedPothole(pothole_id, new PotholeManager.DeletePotholeCallBack() {
            @Override
            public void onSuccess(String responseString) {
                runOnUiThread(()->Toast.makeText(DemoActivity.this,"Deleted", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(()->Toast.makeText(DemoActivity.this,errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void handleUpload(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }
    private void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            File image = Storage.getFileFromUri(imageUri, DemoActivity.this);;
                            Log.d("__PATH", imageUri.getPath());
                            potholeManager.uploadProtholeImage(706, image, new AuthManager.UploadImageCallBack() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(DemoActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.e("__IMAGE", errorMessage);
                                }
                            });
                        }
                        catch (Exception e){
                            Toast.makeText(DemoActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
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
        Pothole pothole = createPothole();
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

    @NonNull
    private Pothole createPothole() {
        AppUser currentUser = AuthManager.getInstance().getAccount();
        Calendar calendar = Calendar.getInstance();

        java.util.Date utilDate = calendar.getTime();
        Date sqlDate = new Date(utilDate.getTime());
        Time sqlTime = new Time(utilDate.getTime());

        Log.d("__Date:",sqlDate.toString());
        Log.d("__Time:",sqlTime.toString());
        Pothole pothole = new Pothole(sqlDate, "None", new Pothole.Location(), currentUser, 0);
        pothole.setAppUser(currentUser);
        pothole.setTimeFound(sqlTime.toString());
        pothole.setSeverity("Normal");
        Pothole.Location location = new Pothole.Location();
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        location.setCity("None");
        location.setCountry("None");
        pothole.setLocation(location);
        return pothole;
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
