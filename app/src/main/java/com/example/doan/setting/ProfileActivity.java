package com.example.doan.setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.R;

public class ProfileActivity extends AppCompatActivity {

    private Button btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_profile);
        EdgeToEdge.enable(this);

        btn_edit = findViewById(R.id.editProfileButton);
        btn_edit.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

    }




}
