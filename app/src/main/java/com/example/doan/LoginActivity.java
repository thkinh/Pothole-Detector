package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView askSignupText;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_loginscreen);

        // Ánh xạ các view từ layout
        emailEditText = findViewById(R.id.editText_Email_srcLogin);
        passwordEditText = findViewById(R.id.editText_Password_scrLogin);
        loginButton = findViewById(R.id.btn_login);
        askSignupText = findViewById(R.id.txt_askSignUp_scrLogin);
        forgotPassword = findViewById(R.id.txt_forgotPassword);

        // Thiết lập sự kiện nhấn cho nút Login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Thiết lập sự kiện nhấn cho dòng chữ Signup để chuyển sang màn hình đăng ký
        askSignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang SignupActivity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //thiết lập sự kiện nhấn cho dòng chữ Forgot Password để chuyển sang màn hình forgot password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    // Hàm xử lý đăng nhập
    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Kiểm tra nếu các trường email và mật khẩu trống
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter your password");
            return;
        }

        // Xác thực đăng nhập (giả lập)
        if (email.equals("example@example.com") && password.equals("password123")) {
            // Đăng nhập thành công
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            // Chuyển sang màn hình chính hoặc màn hình khác theo yêu cầu
            // startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            // Đăng nhập thất bại
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}

