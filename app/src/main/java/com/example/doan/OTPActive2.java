package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;

import java.util.List;

public class OTPActive2 extends AppCompatActivity {


    private EditText editTextOTP;
    private AuthManager authManager;
    private Button cofirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.at_otp_code);
        editTextOTP = findViewById(R.id.editText_otp);
        cofirmButton = findViewById(R.id.btn_finish);

        cofirmButton.setOnClickListener(v -> handleSendOTP());
        authManager = AuthManager.getInstance();
    }


    private void handleSendOTP() {
        String otp = editTextOTP.getText().toString().trim();
        if (TextUtils.isEmpty(otp)) {
            editTextOTP.setError("Enter a valid otp");
            return;
        }
        cofirmButton.setEnabled(false);
        authManager.confirmOTP(authManager.getAccount().getEmail(), otp, new AuthManager.ConfirmOTPCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(OTPActive2.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OTPActive2.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(String errorMessage) {
                cofirmButton.setEnabled(true);
                Log.e("What?", errorMessage);
                Toast.makeText(OTPActive2.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show(); // Added Toast for feedback
            }
        });
    }
}