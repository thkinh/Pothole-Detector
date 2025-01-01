package com.example.doan.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;

import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    // Khai báo các view
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;
    private TextView askLoginText;
    private AuthManager authManager;
    private Switch langSw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        setContentView(R.layout.at_signupscreen);
        EdgeToEdge.enable(this);
        // Ánh xạ các view từ layout
        usernameEditText = findViewById(R.id.editText_username_scrSignup);
        emailEditText = findViewById(R.id.editText_Email_scrSignup);
        passwordEditText = findViewById(R.id.editText_password_scrSignup);
        confirmPasswordEditText = findViewById(R.id.editText_rePassword_scrSignup);
        signupButton = findViewById(R.id.btn_signup100);
        askLoginText = findViewById(R.id.txt_askLogin_scrSignup);
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

        signupButton.setOnClickListener(v -> handleSignup());
        askLoginText.setOnClickListener(v -> navigateToLogin());

        authManager = AuthManager.getInstance();
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

    // Hàm xử lý đăng ký
    private void handleSignup() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        Long distancetraveled = 0L;

        if (ValidateInput(username, email, password, confirmPassword))
        {
            runOnUiThread(()->{
                signupButton.setEnabled(false);
                signupButton.setText("Please wait...");
            });
            Date date_created = new Date(System.currentTimeMillis());
            AppUser appUser = new AppUser(username, email, password, distancetraveled);
            //appUser.setDate_created(date_created);
            authManager.signUp(appUser, new AuthManager.SignUpCallback() {
                @Override
                public void onSuccess(AppUser user) {
                    runOnUiThread(() -> Toast.makeText(SignupActivity.this,
                            "Welcome new user: "+ user.getUsername(),
                            Toast.LENGTH_SHORT).show());
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        signupButton.setEnabled(true);
                        signupButton.setText("Sign Up");
                        Toast.makeText(SignupActivity.this,
                                errorMessage,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

    private boolean ValidateInput(String username, String email, String password, String confirm_pass)
    {
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return false;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirm_pass)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }


    // Hàm điều hướng về màn hình Login
    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Đóng SignupActivity để không thể quay lại màn hình này
    }
}