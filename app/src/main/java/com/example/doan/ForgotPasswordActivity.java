package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView askSignupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_forgotpasswordscreen);

        // Ánh xạ TextView dòng chữ "Sign Up"
        askSignupText = findViewById(R.id.txt_askSignUp_scrForgot);

        // Thiết lập sự kiện nhấn cho dòng chữ "Sign Up"
        askSignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang SignupActivity
                Intent intent = new Intent(ForgotPasswordActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}