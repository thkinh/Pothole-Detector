package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    // Khai báo các view
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;
    private TextView askLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_signupscreen);

        // Ánh xạ các view từ layout
        usernameEditText = findViewById(R.id.editText_username_scrSignup);
        emailEditText = findViewById(R.id.editText_Email_scrSignup);
        passwordEditText = findViewById(R.id.editText_password_scrSignup);
        confirmPasswordEditText = findViewById(R.id.editText_rePassword_scrSignup);
        signupButton = findViewById(R.id.btn_signup);
        askLoginText = findViewById(R.id.txt_askLogin_scrSignup);

        // Thiết lập sự kiện nhấn cho nút Signup
        signupButton.setOnClickListener(v -> handleSignup());

        // Điều hướng trở lại màn hình đăng nhập khi nhấn vào "Already have an account?"
        askLoginText.setOnClickListener(v -> navigateToLogin());
    }

    // Hàm xử lý đăng ký
    private void handleSignup() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Kiểm tra thông tin hợp lệ
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Thực hiện đăng ký (Lưu thông tin hoặc gửi thông tin lên server)
        // Tại đây bạn có thể thực hiện yêu cầu đăng ký với backend của mình.

        // Hiển thị thông báo đăng ký thành công
        Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();

        // Điều hướng về màn hình đăng nhập
        navigateToLogin();
    }

    // Hàm điều hướng về màn hình Login
    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Đóng SignupActivity để không thể quay lại màn hình này
    }
}