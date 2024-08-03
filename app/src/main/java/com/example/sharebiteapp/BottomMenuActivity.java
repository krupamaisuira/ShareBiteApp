package com.example.sharebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_menu);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(R.id.navigation_dashboard == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, DashboardActivity.class));
                    return true;
                }
                else if(R.id.navigation_donate == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, DonateFoodActivity.class));
                    return true;
                }
                else if(R.id.navigation_donatedlst == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, DonatedFoodListActivity.class));
                    return true;
                }
                else if(R.id.navigation_profile == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}