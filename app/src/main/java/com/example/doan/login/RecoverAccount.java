package com.example.doan.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.doan.R;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;

public class RecoverAccount extends androidx.appcompat.app.AppCompatActivity {
    private EditText editTextEmail;
    private Button cofirmButton;

    private TextView askSignup;
    private AuthManager authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_recoveraccount);
        EdgeToEdge.enable(this);
        // Ánh xạ các view từ layout
        editTextEmail = findViewById(R.id.editText_recoverAccount);
        cofirmButton = findViewById(R.id.btn_confirm);
        askSignup = findViewById(R.id.txt_askSignUp_scrRAccount);

        cofirmButton.setOnClickListener(v -> handleSendEmail());

        authManager = AuthManager.getInstance();
    }

    private void handleSendEmail() {
        String email = editTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            return;
        }
        runOnUiThread(()->{
            editTextEmail.setEnabled(false);
            cofirmButton.setEnabled(false);
            cofirmButton.setText("Please wait...");
        });
        authManager.getVerify(email, new AuthManager.GetVerifyCallback() {
            @Override
            public void onSuccess(Integer id) {
                runOnUiThread(() ->{
                    Toast.makeText(RecoverAccount.this, "Your id is " + id, Toast.LENGTH_SHORT).show();
                    navigateToOPTScreen();
                });
                AppUser user = new AppUser();
                user.setEmail(email);
                authManager.setGlobalAccount(user);
            }
            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {Toast.makeText(RecoverAccount.this, errorMessage, Toast.LENGTH_SHORT).show();
                    cofirmButton.setText("Finish");
                    editTextEmail.setEnabled(true);
                    cofirmButton.setEnabled(true);
                });

            }
        });
    }

    // Hàm điều hướng về màn hình OPT
    private void navigateToOPTScreen() {
        Intent intent = new Intent(RecoverAccount.this, OTPActive2.class);
        startActivity(intent);
        finish();
    }
}
