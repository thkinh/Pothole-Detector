package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.api.auth.AuthManager;
import com.example.doan.feature.UserPreferences;
import com.example.doan.model.AppUser;

public class SplashScreen extends AppCompatActivity {

    private Button getStartedButton;  // Đối tượng Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.at_splashscreen);
        getStartedButton = findViewById(R.id.btn_getStarted);
        getStartedButton.setOnClickListener(view -> {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Auto login
        UserPreferences userPreferences = new UserPreferences(this);
        AppUser appUser = userPreferences.getUser();

        if (appUser != null) {
            // Restore in-memory reference
            AuthManager authManager = AuthManager.getInstance();
            authManager.setGlobalAccount(appUser);
            Toast.makeText(this, "Welcome back "+authManager.getAccount().getUsername(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Welcome to our app ", Toast.LENGTH_SHORT).show();
        }
    }
}