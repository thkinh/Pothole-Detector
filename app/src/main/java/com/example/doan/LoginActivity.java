package com.example.doan;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView askSignupText;
    private TextView forgotPassword;
    private AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_loginscreen);

        // Ánh xạ các view từ layout
        emailEditText = findViewById(R.id.editText_Email_srcLogin);
        passwordEditText = findViewById(R.id.editText_Password_scrLogin);
        loginButton = findViewById(R.id.btn_login);
        askSignupText = findViewById(R.id.txt_askSignUp_scrLogin);
        forgotPassword = findViewById(R.id.txt_forgotPassword);

        // Thiết lập sự kiện nhấn cho nút Login
        loginButton.setOnClickListener(view -> handleLogin());

        askSignupText.setOnClickListener(v-> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecoverAccount.class);
            startActivity(intent);
        });

        authManager = AuthManager.getInstance();
    }

    // Hàm xử lý đăng nhập
    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter your password");
            return;
        }

        authManager.signIn(email, password, new AuthManager.SignInCallback() {
            @Override
            public void onSuccess(AppUser user) {
                // Login successful
                runOnUiThread(() ->{
                    //TODO: ADD this user just found as the global account
                    //  authManager.getAccount();
                   Toast.makeText(LoginActivity.this, "Welcome "+ user.getUsername(), Toast.LENGTH_SHORT).show();
                   navigateToDashboard();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Login failed
                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show());
                //Log.e("FAILED: ", errorMessage);
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Đóng SignupActivity để không thể quay lại màn hình này
    }
}

