package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.Report;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.SessionManager;

public class DashboardActivity extends BottomMenuActivity  {
     DonateFoodService donateFoodService;
     Button buttondashdonate;
    private SessionManager sessionManager;
    TextView txtcollections,txtdonations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_dashboard);
        getLayoutInflater().inflate(R.layout.activity_dashboard, findViewById(R.id.container));
        buttondashdonate =  findViewById(R.id.btndashdonate);
        txtcollections =  findViewById(R.id.txtcollections);
        txtdonations =  findViewById(R.id.txtdonations);
        donateFoodService = new DonateFoodService();
        sessionManager = SessionManager.getInstance(this);
        buttondashdonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, DonateFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
        donateFoodService.fetchReport(sessionManager.getUserID(), new ListOperationCallback<Report>() {
            @Override
            public void onSuccess(Report data) {
                txtdonations.setText(data.getDonations() + " Donations");
                txtcollections.setText(data.getCollections() + " Collections");
            }

            @Override
            public void onFailure(String error) {
                txtdonations.setText("0 Donations");
                txtcollections.setText("0 Collections");
            }

        });
        txtdonations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, UserRequestedActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtcollections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ShowRequestHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}