package com.example.sharebiteapp;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sharebiteapp.CustomAdapter.AutocompletePredictionAdapter;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.CustomPrediction;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.Location;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.LocationUtils;
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Utility.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.Manifest;

public class DonateFoodActivity extends BottomMenuActivity {
    TextView txtselectedAddress;
    Button btndonate;
    EditText txttitle,txtdesc,txtbtbefore,txtprice;
    private SessionManager sessionManager;
    DonateFoodService donatefoodservice;
    ImageView iconAddLocation;

    private static final int PICK_IMAGE = 1;
    private ImageView[] imageViews;
    private ImageButton imgCapture;
    private ImageButton[] closeButtons;
    private int imageCount = 0;


    Location location;
    LocationUtils locationUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_donate_food);
        getLayoutInflater().inflate(R.layout.activity_donate_food, findViewById(R.id.container));
        txttitle = findViewById(R.id.txttitle);
        txtdesc = findViewById(R.id.txtdesc);
        txtbtbefore = findViewById(R.id.txtbtbefore);
        txtprice = findViewById(R.id.txtprice);
        txtselectedAddress = findViewById(R.id.txtselectedAddress);
        btndonate = findViewById(R.id.btndonate);
        iconAddLocation = findViewById(R.id.icon_add_location);
        imgCapture = findViewById(R.id.imgcapture);
        //  imgview = findViewById(R.id.imgview);
        imageViews = new ImageView[]{
                findViewById(R.id.imgview),
                findViewById(R.id.imgview2),
                findViewById(R.id.imgview3),
                findViewById(R.id.imgview4)
        };
        closeButtons = new ImageButton[]{
                findViewById(R.id.btnClose1),
                findViewById(R.id.btnClose2),
                findViewById(R.id.btnClose3),
                findViewById(R.id.btnClose4)
        };
        donatefoodservice = new DonateFoodService();
        location = new Location();
        locationUtils = new LocationUtils(this);
        sessionManager = SessionManager.getInstance(this);

        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setVisibility(View.GONE);
            closeButtons[i].setVisibility(View.GONE);

            final int index = i;
            closeButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeImage(index);
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }


        btndonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDonateFood();
            }
        });

        iconAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationUtils.showAddLocationPopup(v,txtselectedAddress,"Add Address");
            }
        });


        imgCapture = findViewById(R.id.imgcapture);
        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


    }
//    private Uri[] getImageUrisFromImageViews() {
//        Uri[] uris = new Uri[imageViews.length];
//
//        for (int i = 0; i < imageViews.length; i++) {
//
//            uris[i] = (Uri) imageViews[i].getTag();
//            Log.d("DonateFoodActivity", "Retrieved URI: " + (uris[i] != null ? uris[i].toString() : "null"));
//        }
//
//        return uris;
//    }


    public void addDonateFood()
    {
        String title = txttitle.getText().toString().trim();
        String desc = txtdesc.getText().toString().trim();
        String bestbefore = txtbtbefore.getText().toString().trim();
        String price = txtprice.getText().toString();


        Uri[] imageUris = Utils.getImageUrisFromImageViews(imageViews);


        if(imageUris.length == 0)
        {
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            return;
        }

        updateLocationFromSelected();

        if (location.getAddress() == null) {
            Toast.makeText(this, "Please add address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(title)) {
            txttitle.setError("Title is required");
            txttitle.requestFocus();
            return ;
        }

        if (TextUtils.isEmpty(desc)) {
            txtdesc.setError("Description is required");
            txtdesc.requestFocus();
            return ;
        }

        if (TextUtils.isEmpty(price)) {
            price = "0";
        }



        DonateFood food = new DonateFood(sessionManager.getUserID(),title,desc,bestbefore,Double.parseDouble(price),location,imageUris);
        donatefoodservice.donatefood(food, new OperationCallback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(DonateFoodActivity.this, DonationSuccessActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(DonateFoodActivity.this, "Food Donate failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateLocationFromSelected() {
        CustomPrediction selectedLocation = locationUtils.getSelectedLocation();
        if (selectedLocation != null) {
            location.setAddress(selectedLocation.getFullAddress());
            location.setLatitude(selectedLocation.getLatitude());
            location.setLongitude(selectedLocation.getLongitude());
        }
    }

    // region open gallery
//        private void openGallery() {
//            Intent intent = new Intent(Intent.ACTION_PICK);
//            intent.setType("image/*");
//            startActivityForResult(intent, 1);
//        }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null && imageCount < imageViews.length) {

                imageViews[imageCount].setVisibility(View.VISIBLE);
                closeButtons[imageCount].setVisibility(View.VISIBLE);
                imageViews[imageCount].setImageURI(selectedImage);
                imageViews[imageCount].setTag(selectedImage);
                imageCount++;


                if (imageCount == imageViews.length) {
                    imgCapture.setEnabled(false);
                    imgCapture.setVisibility(View.GONE);
                }
            }
        }
    }
    private void removeImage(int index) {
        if (index < imageCount && imageCount > 0) {
            for (int i = index; i < imageCount - 1; i++) {

                Drawable nextDrawable = imageViews[i + 1].getDrawable();
                Uri nextTag = (Uri) imageViews[i + 1].getTag();
                imageViews[i].setImageDrawable(nextDrawable);
                imageViews[i].setTag(nextTag);
            }

            imageViews[imageCount - 1].setVisibility(View.GONE);
            imageViews[imageCount - 1].setImageDrawable(null);
            imageViews[imageCount - 1].setTag(null);
            closeButtons[imageCount - 1].setVisibility(View.GONE);
            imageCount--;
            if (imageCount < imageViews.length) {
                imgCapture.setEnabled(true);
                imgCapture.setVisibility(View.VISIBLE);
            }
        }
    }
    // end region
}