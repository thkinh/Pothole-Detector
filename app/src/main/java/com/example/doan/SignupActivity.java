package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;

import java.sql.Date;

public class SignupActivity extends AppCompatActivity {

    // Khai báo các view
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;
    private TextView askLoginText;
    private AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_signupscreen);
        // Ánh xạ các view từ layout
        usernameEditText = findViewById(R.id.editText_username_scrSignup);
        emailEditText = findViewById(R.id.editText_Email_scrSignup);
        passwordEditText = findViewById(R.id.editText_password_scrSignup);
        confirmPasswordEditText = findViewById(R.id.editText_rePassword_scrSignup);
        signupButton = findViewById(R.id.btn_signup100);
        askLoginText = findViewById(R.id.txt_askLogin_scrSignup);


        signupButton.setOnClickListener(v -> handleSignup());
        askLoginText.setOnClickListener(v -> navigateToLogin());

        authManager = AuthManager.getInstance();
    }

    // Hàm xử lý đăng ký
    private void handleSignup() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (ValidateInput(username, email, password, confirmPassword))
        {
            Date date_created = new Date(System.currentTimeMillis());
            AppUser appUser = new AppUser(username, email, password);
            //appUser.setDate_created(date_created);

            //TODO: ADD signup api method
        }
        else
        {
            Toast.makeText(SignupActivity.this, "Something was wrong with the inputs bro!", Toast.LENGTH_SHORT).show();
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