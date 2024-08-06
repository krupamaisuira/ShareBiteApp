package com.example.sharebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RequestFoodSuccessActivity extends BottomMenuActivity {
     Button  btnhome;
     TextView requestloc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_request_food_success);
        getLayoutInflater().inflate(R.layout.activity_request_food_success, findViewById(R.id.container));
        String location =  getIntent().getStringExtra("location");
        requestloc = findViewById(R.id.requestloc);
        btnhome = findViewById(R.id.btnhome);
        if(!location.isEmpty())
        {
            requestloc.setText("Pickup Location : " + location);
        }

        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestFoodSuccessActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}