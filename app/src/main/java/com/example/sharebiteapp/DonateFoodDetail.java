package com.example.sharebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.UserCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.User;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.UserService;
import com.example.sharebiteapp.Utility.Utils;

public class DonateFoodDetail extends BottomMenuActivity {

    DonateFoodService donateFoodService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_donate_food_detail);
        getLayoutInflater().inflate(R.layout.activity_donate_food_detail, findViewById(R.id.container));

        donateFoodService = new DonateFoodService();

        String donationId =  getIntent().getStringExtra("intentdonationId");;
        donateFoodService.getDonationDetail(donationId, new ListOperationCallback<DonateFood>() {
            @Override
            public void onSuccess(DonateFood model) {
                Log.d("donate", "Donation detail fetched successfully");
                 setDetail(model);
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(getApplicationContext(), "Error: " + errMessage, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DonateFoodDetail.this, DonatedFoodListActivity.class);
                startActivity(intent);
            }
        });

    }
    private void setDetail(DonateFood model)
    {
        TextView txtTitle = findViewById(R.id.txtdetailTitle);
        TextView txtShowPrice = findViewById(R.id.txtShowPrice);
        TextView txtBestBefore = findViewById(R.id.txtbestbefore);
        TextView txtDetailStatus = findViewById(R.id.txtdetailStatus);
        TextView txtDetailAddress = findViewById(R.id.txtdetailaddress);
        TextView txtdetaildesc = findViewById(R.id.txtdetaildesc);

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
        txtDetailStatus.setText(model.getFoodStatus());
        FoodStatus status = FoodStatus.fromString(txtDetailStatus.getText().toString());
        Utils.setStatusColor(DonateFoodDetail.this,status, txtDetailStatus);

    }
}