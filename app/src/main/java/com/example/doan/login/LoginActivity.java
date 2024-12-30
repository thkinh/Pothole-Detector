package com.example.doan.login;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.doan.dashboard.MainActivity;
import com.example.doan.R;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.feature.Setting;
import com.example.doan.feature.UserPreferences;
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

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView askSignupText;
    private TextView forgotPassword;
    private Switch langSw;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        setContentView(R.layout.at_loginscreen);
        EdgeToEdge.enable(this);
        // Ánh xạ các view từ layout
        emailEditText = findViewById(R.id.editText_Email_srcLogin);
        passwordEditText = findViewById(R.id.editText_Password_scrLogin);
        loginButton = findViewById(R.id.btn_login);
        askSignupText = findViewById(R.id.txt_askSignUp_scrLogin);
        forgotPassword = findViewById(R.id.txt_forgotPassword);
        langSw = findViewById(R.id.sw_language);

        // Set switch state based on current language
        SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        langSw.setChecked(language.equals("vi"));

        langSw.setOnClickListener(v -> {
            if (langSw.isChecked()) {
                setLanguage("vi", 1);
            } else {
                setLanguage("en", 0);
            }
            recreate();
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

    /*private void showLanguageDialog() {
        final String[] languageList ={"English", "Tiếng Việt"};

        SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE);
        int item = sharedPreferences.getInt("item", 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Choose Language");
        builder.setSingleChoiceItems(languageList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    setLanguage("en", 0);
                    recreate();
                }
                else if (which == 1){
                    setLanguage("vi", 1);
                    recreate();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

    private void setLanguage(String language, int item) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        //save language
        SharedPreferences.Editor editor = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.putInt("item", item);
        editor.apply();
    }// set language end here

    private void loadLanguage(){
        SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        int item = sharedPreferences.getInt("item", 0);
        setLanguage(language, 0);
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
        runOnUiThread(()->{
            loginButton.setEnabled(false);
            loginButton.setText("Please wait...");
        });
        authManager.signIn(email, password, new AuthManager.SignInCallback() {
            @Override
            public void onSuccess(AppUser user) {
                // Login successful
                runOnUiThread(() ->{
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
                //navigateToDashboard();
                UserPreferences userPreferences = new UserPreferences(LoginActivity.this);
                authManager.setGlobalAccount(user);
                userPreferences.saveUser(user);
                Toast.makeText(LoginActivity.this, "Welcome "+ authManager.getAccount().getUsername(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String errorMessage) {
                // Login failed
                runOnUiThread(() ->{
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    loginButton.setText("Login");
                    loginButton.setEnabled(true);
                });
            }
        });
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
                        //AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        /*mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                        });*/
                        String email = googleSignInAccount.getEmail();
                        String username = googleSignInAccount.getDisplayName();
                        sendUserInfoToServer(username, email);
                    }
//                    navigateToSecondActivity2();
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void sendUserInfoToServer(String username, String email) {
        AuthManager.getInstance().checkUserExists(email, new AuthManager.CheckUserCallback() {
            @Override
            public void onUserExists() {
                // User exists, log them in
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onUserNotFound() {
                // User does not exist, create a new account
                AppUser appUser = new AppUser(username, email, "123456", 0L);
                AuthManager.getInstance().signUp(appUser, new AuthManager.SignUpCallback() {
                    @Override
                    public void onSuccess(AppUser user) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}

