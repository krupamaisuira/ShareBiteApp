package com.example.sharebiteapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
    private ImageView[] imageViews;
    private ImageView firstimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_donate_food_detail);
        getLayoutInflater().inflate(R.layout.activity_donate_food_detail, findViewById(R.id.container));

        donateFoodService = new DonateFoodService();
        imageViews = new ImageView[]{
                findViewById(R.id.detailimgview),
                findViewById(R.id.detailimgview2),
                findViewById(R.id.detailimgview3),
                findViewById(R.id.detailimgview4)
        };
        firstimg = findViewById(R.id.firstimg);
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

        if(model.uploadedImageUris != null && model.uploadedImageUris.size() > 0) {

            for (int i = 0; i < model.uploadedImageUris.size(); i++) {

                Uri selectedImage = Uri.parse(model.uploadedImageUris.get(i).toString());

                if(i == 0)
                {
                    Glide.with(firstimg.getContext())
                            .load(selectedImage.toString())
                            .error(android.R.drawable.ic_menu_gallery) // Error image
                            .into(firstimg);
                }

                imageViews[i].setVisibility(View.VISIBLE);
                Glide.with(imageViews[i].getContext())
                        .load(selectedImage.toString())
                        .error(android.R.drawable.ic_menu_gallery) // Error image
                        .into(imageViews[i]);


            }
        }
    }
}