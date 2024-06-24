package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.sharebiteapp.Utility.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int splashtimeout = 3000;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = SessionManager.getInstance(this);

        if (sessionManager.userLoggedIn()) {

            Intent intent = new Intent(SplashActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, splashtimeout);
        }


    }
}