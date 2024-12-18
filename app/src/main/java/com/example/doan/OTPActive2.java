package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class OTPActive2 extends AppCompatActivity {

    private Button cofirmButton;
    private EditText editTextOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.at_otp_code);

        // Ánh xạ các view từ layout
        editTextOTP = findViewById(R.id.editText_otp);
        cofirmButton = findViewById(R.id.btn_finish);

        // Thiết lập sự kiện nhấn cho nút Signup
        cofirmButton.setOnClickListener(v -> handleSendOTP());

    }

    // Hàm xử lý đăng ký
    private void handleSendOTP() {
        String otp = editTextOTP.getText().toString().trim();

        // Kiểm tra thông tin hợp lệ
        if (TextUtils.isEmpty(otp) || !android.util.Patterns.EMAIL_ADDRESS.matcher(otp).matches()) {
            editTextOTP.setError("Enter a valid otp");
            return;
        }
        // Hiển thị thông báo đăng ký thành công
        Toast.makeText(OTPActive2.this, "Confirm OTP successful", Toast.LENGTH_SHORT).show();

        // Điều hướng về màn hình đăng nhập
        navigateToLoginScreen();
    }

    // Hàm điều hướng về màn hình OPT
    private void navigateToLoginScreen() {
        Intent intent = new Intent(OTPActive2.this, LoginActivity.class);
        startActivity(intent);
    }
}