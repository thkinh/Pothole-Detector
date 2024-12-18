package com.example.doan;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView askSignupText;
    private TextView forgotPassword;

    private AuthManager authManager;

    //For Google Sign In
    private Button googleButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    GoogleSignInOptions gso1;
    GoogleSignInClient gsc1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_loginscreen);

        // Ánh xạ các view từ layout
        emailEditText = findViewById(R.id.editText_Email_srcLogin);
        passwordEditText = findViewById(R.id.editText_Password_scrLogin);
        loginButton = findViewById(R.id.btn_login);
        askSignupText = findViewById(R.id.txt_askSignUp_scrLogin);
        forgotPassword = findViewById(R.id.txt_forgotPassword);

        //Ánh xạ các view từ layout for Google Sign In
        googleButton = findViewById(R.id.btn_continueWGoogle_scrLogin);
        // Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        mUser = mAuth.getCurrentUser();

        // When user already sign in redirect to profile activity
        if (mUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        // Initialize sign in options the client-id is copied form google-services.json file
        gso1 = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getResources().getString(R.string.default_web_client_id)).requestEmail().build();
        // Initialize sign in client
        gsc1 = GoogleSignIn.getClient(this, gso1);

        googleButton.setOnClickListener(view -> {
            googlesignIn();
        });

        loginButton.setOnClickListener(view -> handleLogin());

        askSignupText.setOnClickListener(v-> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecoverAccount.class);
            startActivity(intent);
        });

        authManager = AuthManager.getInstance();
    }

    // Hàm xử lý đăng nhập
    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter your password");
            return;
        }

        authManager.signIn(email, password, new AuthManager.SignInCallback() {
            @Override
            public void onSuccess(AppUser user) {
                // Login successful
                runOnUiThread(() ->{
                    //TODO: ADD this user just found as the global account
                    //  authManager.getAccount();
                   Toast.makeText(LoginActivity.this, "Welcome "+ user.getUsername(), Toast.LENGTH_SHORT).show();
                   navigateToDashboard();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Login failed
                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show());
                //Log.e("FAILED: ", errorMessage);
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Đóng SignupActivity để không thể quay lại màn hình này
    }

    // Initialize sign in intent and then Start activity for result
    private void googlesignIn() {

        Intent signInIntent = gsc1.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When request code is equal to 1000 initialize task
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // When google sign in successful initialize string
            if (task.isSuccessful()) {
                String s = "Google sign in successful";
                displayToast(s);
                // Initialize sign in account
                try {
                    GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // When task is successful redirect to profile activity display Toast
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    displayToast("Firebase authentication successful");
                                } else {
                                    displayToast("Authentication failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
//                    navigateToSecondActivity2();
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    /*private void navigateToSecondActivity2() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }*/
    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}

