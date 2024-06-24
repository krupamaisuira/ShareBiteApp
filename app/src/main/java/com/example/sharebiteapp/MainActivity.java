package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.sharebiteapp.Utility.SessionManager;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    TextView txtview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtview = findViewById(R.id.txtmainview);
        sessionManager = SessionManager.getInstance(this);


        if (sessionManager.userLoggedIn()) {

            txtview.setText("Welcome : " + sessionManager.getUsername() + " notification : " + sessionManager.getNotificationStatus());
        }
    }
}