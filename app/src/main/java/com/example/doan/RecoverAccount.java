package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecoverAccount extends androidx.appcompat.app.AppCompatActivity {
    private EditText editTextEmail;
    private Button cofirmButton;

    private TextView askSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_recoveraccount);

        // Ánh xạ các view từ layout
        editTextEmail = findViewById(R.id.editText_recoverAccount);
        cofirmButton = findViewById(R.id.btn_confirm);
        askSignup = findViewById(R.id.txt_askSignUp_scrRAccount);

        // Thiết lập sự kiện nhấn cho nút Signup
        cofirmButton.setOnClickListener(v -> handleSendEmail());

        // Điều hướng trở lại màn hình đăng nhập khi nhấn vào "Already have an account?"
        //askSignup.setOnClickListener(v -> navigateToSignupScreen());
    }

    // Hàm xử lý đăng ký
    private void handleSendEmail() {
        String email = editTextEmail.getText().toString().trim();

        // Kiểm tra thông tin hợp lệ
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            return;
        }
        // Hiển thị thông báo đăng ký thành công
        Toast.makeText(RecoverAccount.this, "Send email successful", Toast.LENGTH_SHORT).show();

        // Điều hướng về màn hình đăng nhập
        navigateToOPTScreen();
    }

    // Hàm điều hướng về màn hình OPT
    private void navigateToOPTScreen() {
        Intent intent = new Intent(RecoverAccount.this, OPTActive2.class);
        startActivity(intent);
    }
}
