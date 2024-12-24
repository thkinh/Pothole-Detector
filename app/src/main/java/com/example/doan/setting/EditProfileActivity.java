package com.example.doan.setting;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.example.doan.R;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.feature.Setting;
import com.example.doan.feature.Storage;
import com.example.doan.model.UserDetails;
import com.google.android.gms.auth.api.Auth;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {
    private Button btn_save, btn_upload;
    private EditText tv_fullName,tv_phone, tv_birth, tv_job;
    private ImageView btn_back;
    private static final int PICK_IMAGE_REQUEST = 1;
    ActivityResultLauncher<Intent> resultLauncher;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_edit_profile);
        EdgeToEdge.enable(this);
        // Ensure permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
        } else {
        }
        UserDetails details = Setting.getInstance().getUserDetails();
        tv_fullName = findViewById(R.id.edt_profileName);
        tv_job = findViewById(R.id.edt_jobTitle);
        tv_phone = findViewById(R.id.edt_phone);
        tv_birth = findViewById(R.id.edt_birth);
        btn_save = findViewById(R.id.saveButton);
        btn_back = findViewById(R.id.btn_img_backFromEditP);
        btn_upload = findViewById(R.id.btn_uploadPhoto);

        tv_fullName.setText(details.getFullName());
        tv_job.setText(details.getJobTitle());
        tv_phone.setText(details.getPhoneNumber());
        if (details.getDateOfBirth() != null){
        tv_birth.setText(details.getDateOfBirth().toString());}

        btn_save.setOnClickListener(view -> saveProfile(new UserDetails()));
        btn_back.setOnClickListener(view -> finish());
        btn_upload.setOnClickListener(view -> handleUpload());
        authManager = AuthManager.getInstance();
        registerResult();
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
                        ImageView imageView = findViewById(R.id.edt_profileImage);
                        File image = Storage.getFileFromUri(imageUri, EditProfileActivity.this);;
                        Log.d("__PATH", imageUri.getPath());
                        authManager.uploadProfileImage(authManager.getAccount().getId(), image, new AuthManager.UploadImageCallBack() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(EditProfileActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(String errorMessage) {
                                Log.e("__IMAGE", errorMessage);
                            }
                        });
                        imageView.setImageURI(imageUri);
                    }
                    catch (Exception e){
                        Toast.makeText(EditProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }


    // Ensure proper permission request handling
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Storage.STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("__Permission", ""+requestCode);
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(File imageFile) {

    }


    private void saveProfile(UserDetails details){
        details.setFullName(tv_fullName.getText().toString());
        details.setJobTitle(tv_job.getText().toString());
        details.setPhoneNumber(tv_phone.getText().toString());
        try {
            String birthText = tv_birth.getText().toString();
            java.sql.Date dateOfBirth = java.sql.Date.valueOf(birthText);
            details.setDateOfBirth(dateOfBirth);
        }catch (IllegalArgumentException e){
            Toast.makeText(EditProfileActivity.this, "Wrong format: yy-mm-dd", Toast.LENGTH_SHORT).show();
            return;
        }
        AuthManager.getInstance().updateUserDetails(AuthManager.getInstance().getAccount().getId(),details, new AuthManager.UpdateDetailsCallBack() {
            @Override
            public void onSuccess(UserDetails details) {
                runOnUiThread(() ->{
                    Toast.makeText(EditProfileActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ValidateInput(){

    }
}
