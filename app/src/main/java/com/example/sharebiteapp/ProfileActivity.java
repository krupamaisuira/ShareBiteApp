package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sharebiteapp.Utility.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    TextView txtLogout,txtprofileuser,txtchangepwd;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtLogout = findViewById(R.id.txtLogout);
        txtprofileuser = findViewById(R.id.txtprofileusername);
        txtchangepwd = findViewById(R.id.txtchangepwd);

        sessionManager = SessionManager.getInstance(this);


        if (sessionManager.userLoggedIn()) {
            txtprofileuser.setText(sessionManager.getUsername());
        }

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logoutUser();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        txtchangepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}