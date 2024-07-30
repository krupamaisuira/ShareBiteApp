package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends BottomMenuActivity  {

    Button buttondashdonate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_dashboard);
        getLayoutInflater().inflate(R.layout.activity_dashboard, findViewById(R.id.container));
        buttondashdonate =  findViewById(R.id.btndashdonate);

        buttondashdonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, DonateFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}