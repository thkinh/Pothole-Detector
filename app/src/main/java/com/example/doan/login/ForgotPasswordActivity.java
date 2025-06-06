package com.example.doan.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editText_newPassword, editText_confirmPassword;
    private AuthManager authManager;
    private Button btn_finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_forgotpasswordscreen);

        btn_finish = findViewById(R.id.btn_finish);
        editText_newPassword = findViewById(R.id.editText_newPassword);
        editText_confirmPassword = findViewById(R.id.editText_cfPassword);

        btn_finish.setOnClickListener(view -> finish_changingPassword());

        authManager = AuthManager.getInstance();
    }

    private void finish_changingPassword(){
        String newPass = editText_newPassword.getText().toString().trim();
        if (validateInput(newPass)){
            String email = authManager.getAccount().getEmail();
            if (email == null || email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this,
                        "Email not available. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            btn_finish.setEnabled(false);
            authManager.confirmPassword(email, newPass, new AuthManager.ConfirmPasswordCallBack() {
                @Override
                public void onSuccess(AppUser user) {
                    runOnUiThread(()->{
                        Toast.makeText(ForgotPasswordActivity.this, "Changed password!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                    authManager.setGlobalAccount(user);
                }
                @Override
                public void onFailure(String errorMessage) {
                    btn_finish.setEnabled(true);
                    runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this,
                            errorMessage,
                            Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    private boolean validateInput(String newPass){
        String cfPass = editText_confirmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(newPass) || newPass.length() < 6){
            editText_newPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (!cfPass.equals(newPass)){
            editText_confirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }
}