package com.example.sharebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.RequestFood;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.RequestFoodService;
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Utility.Utils;

public class RequestFoodDetailActivity extends BottomMenuActivity {

    DonateFoodService donateFoodService;
    Button btnRequestFood;
    private SessionManager sessionManager;
    RequestFoodService requestFoodService;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_request_food_detail);
        getLayoutInflater().inflate(R.layout.activity_request_food_detail, findViewById(R.id.container));
        donateFoodService = new DonateFoodService();
        btnRequestFood = findViewById(R.id.btnRequestFood);
        requestFoodService = new RequestFoodService();
        sessionManager = SessionManager.getInstance(this);

        String donationId =  getIntent().getStringExtra("intentdonationId");
        donateFoodService.getDonationDetail(donationId, new ListOperationCallback<DonateFood>() {
            @Override
            public void onSuccess(DonateFood model) {
                Log.d("donate", "Donation detail fetched successfully");
                setDetail(model);
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(getApplicationContext(), "Error: " + errMessage, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RequestFoodDetailActivity.this, RequestFoodListActivity.class);
                startActivity(intent);
            }
        });
        btnRequestFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestFood request = new RequestFood(donationId,sessionManager.getUserID());
                requestFoodService.requestfood(request, new OperationCallback() {
                    @Override
                    public void onSuccess() {

                        Intent intent = new Intent(RequestFoodDetailActivity.this, RequestFoodSuccessActivity.class);
                        intent.putExtra("location", location);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        Toast.makeText(RequestFoodDetailActivity.this, "Request failed! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    private void setDetail(DonateFood model)
    {
        location = model.location.getAddress();
        TextView txtTitle = findViewById(R.id.txtreqdetailTitle);
        TextView txtShowPrice = findViewById(R.id.txtreqShowPrice);
        TextView txtBestBefore = findViewById(R.id.txtreqbestbefore);
        TextView txtDetailAddress = findViewById(R.id.txtreqdetailaddress);
        TextView txtdetaildesc = findViewById(R.id.txtreqdetaildesc);

        txtTitle.setText(model.getTitle());
        if(model.price > 0) {
            txtShowPrice.setText(String.format("Price: $%.2f", model.getPrice()));
        }
        else
        {
            txtShowPrice.setText("Price : free");
        }
        txtBestBefore.setText("Best before: " + model.getBestBefore());
        txtDetailAddress.setText(model.location.getAddress());
        txtdetaildesc.setText(model.getDescription());

    }
}