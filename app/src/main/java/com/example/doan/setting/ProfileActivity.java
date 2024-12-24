package com.example.doan.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.login.SignupActivity;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.feature.Setting;
import com.example.doan.model.AppUser;
import com.example.doan.model.UserDetail;
import com.example.doan.model.UserDetails;

public class ProfileActivity extends AppCompatActivity {

    private Button btn_edit;
    private TextView tv_fullName,tv_email, tv_phone, tv_birth, tv_job;
    private ImageView btn_back;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_profile);
        EdgeToEdge.enable(this);

        authManager = AuthManager.getInstance();
        Refresh();

        btn_edit = findViewById(R.id.editProfileButton);
        btn_edit.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });
        btn_back = findViewById(R.id.img_backFromProfile);
        btn_back.setOnClickListener(view -> finish());

        tv_fullName = findViewById(R.id.profileName);
        tv_job = findViewById(R.id.profileJobTitle);
        tv_email = findViewById(R.id.profileEmail);
        tv_phone = findViewById(R.id.profilePhone);
        tv_birth = findViewById(R.id.profileDateOfBirth);
    }

    private void Refresh(){
        if (authManager.getAccount() == null){
            return;
        };
        authManager.getUserDetails(authManager.getAccount().getDetails().getId(), new AuthManager.GetDetailsCallBack() {
            @Override
            public void onSuccess(UserDetails details) {
                Setting.getInstance().setUserDetails(details);
                runOnUiThread(()->{
                    tv_fullName.setText(details.getFullName());
                    tv_job.setText(details.getJobTitle());
                    tv_email.setText(details.getEmail());
                    tv_phone.setText(details.getPhoneNumber());
                    tv_birth.setText(details.getDateOfBirth().toString());
                });
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        authManager.fetchProfileImage(authManager.getAccount().getId(), new AuthManager.FetchImageCallBack() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                ImageView imageView = findViewById(R.id.profileImage);
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume(); // Always call the superclass method first
        Refresh(); // Call the Refresh method to update the UI
    }
}
