package com.example.doan.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

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

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {

    private Button getStartedButton;  // Đối tượng Button
    private Switch langSw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        EdgeToEdge.enable(this);
        setContentView(R.layout.at_splashscreen);
        getStartedButton = findViewById(R.id.btn_getStarted);
        getStartedButton.setOnClickListener(view -> {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        langSw = findViewById(R.id.sw_language);

        // Set switch state based on current language
        SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        langSw.setChecked(language.equals("vi"));

        langSw.setOnClickListener(v -> {
            if (langSw.isChecked()) {
                setLanguage("vi", 1);
            } else {
                setLanguage("en", 0);
            }
            recreate();
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

    private void setLanguage(String language, int item) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        //save language
        SharedPreferences.Editor editor = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.putInt("item", item);
        editor.apply();
    }// set language end here

    private void loadLanguage(){
        SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        int item = sharedPreferences.getInt("item", 0);
        setLanguage(language, 0);
    }
}