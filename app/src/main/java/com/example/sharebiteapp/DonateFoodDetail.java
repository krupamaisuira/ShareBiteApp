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
import com.example.sharebiteapp.Interface.UserCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.User;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.RequestFoodService;
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Utility.UserService;
import com.example.sharebiteapp.Utility.Utils;

public class DonateFoodDetail extends BottomMenuActivity {

    DonateFoodService donateFoodService;
    RequestFoodService requestFoodService;
    private ImageView[] imageViews;
    private ImageView firstimg;
    private Button buttonDonated,buttonCancel;
    LinearLayout requestedBySection;
    TextView txtDetailStatus;
    private SessionManager sessionManager;
    String foodRequestId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_donate_food_detail);
        getLayoutInflater().inflate(R.layout.activity_donate_food_detail, findViewById(R.id.container));
         requestedBySection = findViewById(R.id.requestedBySection);
         txtDetailStatus = findViewById(R.id.txtdetailStatus);
        donateFoodService = new DonateFoodService();
        requestFoodService = new RequestFoodService();
        imageViews = new ImageView[]{
                findViewById(R.id.detailimgview),
                findViewById(R.id.detailimgview2),
                findViewById(R.id.detailimgview3),
                findViewById(R.id.detailimgview4)
        };
        firstimg = findViewById(R.id.firstimg);
        buttonDonated = findViewById(R.id.buttonDonated);
        buttonCancel = findViewById(R.id.buttonCancel);
        sessionManager = SessionManager.getInstance(this);
        String donationId =  getIntent().getStringExtra("intentdonationId");
        donateFoodService.getDonationDetail(donationId, new ListOperationCallback<DonateFood>() {
            @Override
            public void onSuccess(DonateFood model) {
                if(model.requestedBy != null) {
                    foodRequestId = model.requestedBy.requestId;
                }
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

        buttonDonated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 setFoodStatus(donationId,FoodStatus.Donated.getIndex());
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFoodStatus(donationId,FoodStatus.Available.getIndex());
            }
        });
    }
    private void setFoodStatus(String donationid, int status)
    {
        if(status == FoodStatus.Available.getIndex())
        {
            requestFoodService.requestFoodCancel(foodRequestId, sessionManager.getUserID(), new OperationCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(String errMessage) {
                    Toast.makeText(getApplicationContext(), "Error: " + errMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
        donateFoodService.updateFoodStatus(donationid, status, new OperationCallback() {
            @Override
            public void onSuccess() {

                if(status == FoodStatus.Available.getIndex())
                {
                    requestedBySection.setVisibility(View.GONE);
                    txtDetailStatus.setText("Available");
                    Utils.setStatusColor(DonateFoodDetail.this,FoodStatus.Available, txtDetailStatus);
                    Toast.makeText(getApplicationContext(), "Request is cancel now this donation will available for other user", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Utils.setStatusColor(DonateFoodDetail.this,FoodStatus.Donated, txtDetailStatus);
                    txtDetailStatus.setText("Donated");
                    Toast.makeText(getApplicationContext(), "Food donated", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(getApplicationContext(), "Error: " + errMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDetail(DonateFood model)
    {
        TextView txtTitle = findViewById(R.id.txtdetailTitle);
        TextView txtShowPrice = findViewById(R.id.txtShowPrice);
        TextView txtBestBefore = findViewById(R.id.txtbestbefore);

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
        if(Utils.isFoodExpired(model.bestBefore) == 0)
        {
            model.setStatus(FoodStatus.Expired.getIndex());
        }

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

        if (model.requestedBy != null && (model.status == FoodStatus.Requested.getIndex() || model.status == FoodStatus.Available.getIndex() )) {
            requestedBySection.setVisibility(View.VISIBLE);

            TextView userName = findViewById(R.id.userName);
            TextView userEmail = findViewById(R.id.userEmail);
            TextView userPhone = findViewById(R.id.userPhone);

            // Assuming requestedBy has the required fields
            userName.setText("User Name: " + model.requestedBy.requestedUserDetail.getUsername());
            userEmail.setText("Email: " + model.requestedBy.requestedUserDetail.getEmail());
            userPhone.setText("Phone: " + model.requestedBy.requestedUserDetail.getMobilenumber());
        } else {
            requestedBySection.setVisibility(View.GONE);
        }
    }
}