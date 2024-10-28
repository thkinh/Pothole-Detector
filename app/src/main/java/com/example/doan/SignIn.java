package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.core.Tag;

public class SignIn extends AppCompatActivity {

    Button btn_signin;
    FirestoreClient fc ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.at_signin);
        fc = FirestoreClient.getInstance();
        MailClient mailClient = new MailClient();
        btn_signin = findViewById(R.id.btn_signin);
        EditText edit_text_email = findViewById(R.id.edit_email);
        EditText edit_text_pass = findViewById(R.id.edit_pass);


        btn_signin.setOnClickListener(view -> fc.UserFetchByEmail(edit_text_email.getText().toString()).thenAccept(user -> {
            if (user != null) {
                if (user.password.equals(edit_text_pass.getText().toString())) {
                    Toast.makeText(SignIn.this, "User found", Toast.LENGTH_SHORT).show();
                    mailClient.sendEmail(edit_text_email.getText().toString());
                    Intent intent = new Intent(SignIn.this, Maps.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignIn.this, "Wrong password", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(SignIn.this.getApplicationContext(), "User not found.", Toast.LENGTH_LONG).show();
            }
        }).exceptionally(e -> {
            Log.e("MailClient", "MAIL ERROR: ", e); // Pass 'e' directly to log the stack trace
            Toast.makeText(SignIn.this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }));

    }
}