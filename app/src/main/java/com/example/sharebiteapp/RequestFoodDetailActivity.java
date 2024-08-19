package com.example.sharebiteapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
    Button btnRequestFood,btnCancelFood;
    private SessionManager sessionManager;
    RequestFoodService requestFoodService;
    String location;
    private ImageView[] imageViews;
    private ImageView firstimg;
    String foodRequestId;
    int showcancelled = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_request_food_detail);
        getLayoutInflater().inflate(R.layout.activity_request_food_detail, findViewById(R.id.container));
        donateFoodService = new DonateFoodService();
        btnRequestFood = findViewById(R.id.btnRequestFood);
        btnCancelFood = findViewById(R.id.btnCancelFood);
        requestFoodService = new RequestFoodService();
        sessionManager = SessionManager.getInstance(this);
        imageViews = new ImageView[]{
                findViewById(R.id.reqdetailimgview),
                findViewById(R.id.reqdetailimgview2),
                findViewById(R.id.reqdetailimgview3),
                findViewById(R.id.reqdetailimgview4)
        };
        firstimg = findViewById(R.id.reqfirstimg);

        String donationId =  getIntent().getStringExtra("intentdonationId");
        String collectionsString = getIntent().getStringExtra("collections");

        if (collectionsString != null) {
            try {
                showcancelled = Integer.parseInt(collectionsString);
            } catch (NumberFormatException e) {
                showcancelled = 0;
            }
        }


        btnCancelFood.setVisibility(View.GONE);
        donateFoodService.getDonationDetail(donationId, new ListOperationCallback<DonateFood>() {
            @Override
            public void onSuccess(DonateFood model) {
                if(model.requestedBy != null) {
                    foodRequestId = model.requestedBy.requestId;
                }
                Log.d("donate", "Donation detail fetched successfully");
                if(showcancelled == 1)
                {
                    btnRequestFood.setVisibility(View.GONE);
                    if(model.getStatus() == FoodStatus.Requested.getIndex()) {
                        btnCancelFood.setVisibility(View.VISIBLE);
                    }
                    else if(model.getStatus() == FoodStatus.Available.getIndex())
                    {
                        btnRequestFood.setVisibility(View.VISIBLE);
                    }

                }
                else
                {
                    btnCancelFood.setVisibility(View.GONE);
                    btnRequestFood.setVisibility(View.VISIBLE);
                }
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
                RequestFood request = new RequestFood(donationId,sessionManager.getUserID(),null,null);
                requestFoodService.requestfood(request, new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        donateFoodService.updateFoodStatus(donationId, FoodStatus.Requested.getIndex(), new OperationCallback() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent(RequestFoodDetailActivity.this, RequestFoodSuccessActivity.class);
                                intent.putExtra("location", location);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(String errorMessage) {

                            }
                        });


                    }

                    @Override
                    public void onFailure(String errMessage) {
                        Toast.makeText(RequestFoodDetailActivity.this, "Request failed! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        btnCancelFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFoodService.requestFoodCancel(foodRequestId, sessionManager.getUserID(), new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(RequestFoodDetailActivity.this, ShowRequestHistoryActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        Toast.makeText(getApplicationContext(), "Error: " + errMessage, Toast.LENGTH_SHORT).show();
                    }
                });

                donateFoodService.updateFoodStatus(donationId, FoodStatus.Available.getIndex(), new OperationCallback() {
                    @Override
                    public void onSuccess() {


                    }
                    @Override
                    public void onFailure(String errMessage) {
                        Toast.makeText(getApplicationContext(), "Error: " + errMessage, Toast.LENGTH_SHORT).show();
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