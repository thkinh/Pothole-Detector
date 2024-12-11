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

public class RecoverAccount extends androidx.appcompat.app.AppCompatActivity {
    private EditText editTextEmail;
    private Button cofirmButton;

    private TextView askSignup;
    private AuthManager authManager;
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

        authManager = AuthManager.getInstance();
    }

    // Hàm xử lý đăng ký
    private void handleSendEmail() {
        String email = editTextEmail.getText().toString().trim();
        // Kiểm tra thông tin hợp lệ
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            return;
        }

        authManager.getVerify(email, new AuthManager.GetVerifyCallback() {
            @Override
            public void onSuccess(Integer id) {
                runOnUiThread(() ->{
                    Toast.makeText(RecoverAccount.this, "Your id is " + id, Toast.LENGTH_SHORT).show();
                    navigateToOPTScreen();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(RecoverAccount.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Hàm điều hướng về màn hình OPT
    private void navigateToOPTScreen() {
        Intent intent = new Intent(RecoverAccount.this, OPTActive2.class);
        startActivity(intent);
    }
}
