package com.example.doan.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.core.splashscreen.SplashScreen;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.dashboard.MainActivity;
import com.example.doan.R;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.feature.UserPreferences;
import com.example.doan.model.AppUser;

public class SplashScreenn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        SplashScreen.installSplashScreen(SplashScreenn.this);
        setContentView(R.layout.at_splashscreen);

        // Adjust insets for immersive UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Auto login logic
        UserPreferences userPreferences = new UserPreferences(this);
        AppUser appUser = userPreferences.getUser();

        if (appUser != null) {
            // Restore in-memory reference
            AuthManager authManager = AuthManager.getInstance();
            authManager.setGlobalAccount(appUser);
            Toast.makeText(this, "Welcome back " + authManager.getAccount().getUsername(), Toast.LENGTH_SHORT).show();
            // Navigate to MainActivity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "Welcome to our app", Toast.LENGTH_SHORT).show();
            // Navigate to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish(); // Ensure SplashScreen doesn't remain in the back stack

    }
}
