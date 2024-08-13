package com.example.sharebiteapp;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.CustomPrediction;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.Location;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.LocationService;
import com.example.sharebiteapp.Utility.LocationUtils;
import com.example.sharebiteapp.Utility.PhotoService;
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Utility.Utils;
import com.bumptech.glide.Glide;

public class DonateFoodUpdateActivity extends BottomMenuActivity {
    DonateFoodService donateFoodService;
    LocationService locationService;
    PhotoService photoService;
    private ImageView[] imageViews;
    private ImageButton imgCapture;
    private ImageButton[] closeButtons;
    private int imageCount = 0;
    EditText txtTitle,txtShowPrice,txtBestBefore,txtdetaildesc;
    TextView txtDetailAddress;
    ImageView iconEditLocation;
    Button editbtndonate;
    Location location;
    LocationUtils locationUtils;
    private static final int PICK_IMAGE = 1;
    private boolean isEdited = false;
    private boolean isEditedLocation = false;
    private boolean isEditedPhotos = false;
    private boolean isUpdated = true;
    String donationId;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.activity_donate_food_update);
        getLayoutInflater().inflate(R.layout.activity_donate_food_update, findViewById(R.id.container));
         txtTitle = findViewById(R.id.edittxttitle);
         txtShowPrice = findViewById(R.id.edittxtprice);
         txtBestBefore = findViewById(R.id.edittxtbtbefore);
         txtDetailAddress = findViewById(R.id.edittxtselectedAddress);
         txtdetaildesc = findViewById(R.id.edittxtdesc);
        imgCapture = findViewById(R.id.editimgcapture);
        iconEditLocation = findViewById(R.id.icon_edit_location);
        editbtndonate = findViewById(R.id.editbtndonate);
        sessionManager = SessionManager.getInstance(this);
         donateFoodService = new DonateFoodService();
        locationService = new LocationService();
        photoService = new PhotoService();
        imageViews = new ImageView[]{
                findViewById(R.id.editimgview),
                findViewById(R.id.editimgview2),
                findViewById(R.id.editimgview3),
                findViewById(R.id.editimgview4)
        };
        closeButtons = new ImageButton[]{
                findViewById(R.id.editbtnClose1),
                findViewById(R.id.editbtnClose2),
                findViewById(R.id.editbtnClose3),
                findViewById(R.id.editbtnClose4)
        };

        location = new Location();
        locationUtils = new LocationUtils(this);

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

         donationId =  getIntent().getStringExtra("editdonationId");;
        donateFoodService.getDonationDetail(donationId, new ListOperationCallback<DonateFood>() {
            @Override
            public void onSuccess(DonateFood model) {

                setDetail(model);
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(getApplicationContext(), "Error: " + errMessage, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DonateFoodUpdateActivity.this, DonatedFoodListActivity.class);
                startActivity(intent);
            }
        });
        setupTextWatchers();
        iconEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationUtils.showAddLocationPopup(v,txtDetailAddress,"Update Address");
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        editbtndonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDonateFood();
            }
        });

    }
    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        txtTitle.addTextChangedListener(textWatcher);
        txtShowPrice.addTextChangedListener(textWatcher);
        txtBestBefore.addTextChangedListener(textWatcher);
        txtdetaildesc.addTextChangedListener(textWatcher);
    }
    private void updateLocationFromSelected() {
        CustomPrediction selectedLocation = locationUtils.getSelectedLocation();
        if (selectedLocation != null) {
            location.setAddress(selectedLocation.getFullAddress());
            location.setLatitude(selectedLocation.getLatitude());
            location.setLongitude(selectedLocation.getLongitude());
            isEditedLocation = true;
        }
    }
    public void updateDonateFood()
    {

        String title = txtTitle.getText().toString().trim();
        String desc = txtdetaildesc.getText().toString().trim();
        String bestbefore = txtBestBefore.getText().toString().trim();
        String price = txtShowPrice.getText().toString();

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
            txtTitle.setError("Title is required");
            txtTitle.requestFocus();
            return ;
        }

        if (TextUtils.isEmpty(desc)) {
            txtdetaildesc.setError("Description is required");
            txtdetaildesc.requestFocus();
            return ;
        }

        if (TextUtils.isEmpty(price)) {
            price = "0";
        }
        if(isEdited || isEditedPhotos || isEditedLocation)
        {
            if(isEdited)
            {

                DonateFood food = new DonateFood(sessionManager.getUserID(),title,desc,bestbefore,Double.parseDouble(price),null,null);
                food.setDonationId(donationId);

                donateFoodService.updatedonatedfood(food, new OperationCallback() {
                    @Override
                    public void onSuccess() {
                            isUpdated = true;
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        isUpdated = false;

                    }
                });
            }
            if(isEditedPhotos)
            {

//                photoService.updateImages(donationId,imageUris, new OperationCallback() {
//                    @Override
//                    public void onSuccess() {
//                        isUpdated = true;
//                    }
//
//                    @Override
//                    public void onFailure(String errMessage) {
//                        isUpdated = false;
//
//                    }
//                });
            }
            if(isEditedLocation)
            {

                locationService.updatelocation(location, new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        isUpdated = true;
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        isUpdated = false;

                    }
                });
            }
            if(isUpdated)
            {
                Toast.makeText(this, "successfully update", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DonateFoodUpdateActivity.this, DonatedFoodListActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(DonateFoodUpdateActivity.this, "Update failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setDetail(DonateFood model)
    {

        imgCapture.setEnabled(true);
        imgCapture.setVisibility(View.VISIBLE);

        txtTitle.setText(model.getTitle());
        if(model.price > 0) {
            txtShowPrice.setText(String.format("Price: $%.2f", model.getPrice()));
        }
        else
        {
            txtShowPrice.setText("");
        }
        txtBestBefore.setText(model.getBestBefore());

        location.setLocationId(model.location.getLocationId());
        if(model.location.getAddress() == null)
        {
            txtDetailAddress.setText("Edit Address");
        }
        else
        {
            location.setAddress(model.location.getAddress());
            location.setLatitude(model.location.getLatitude());
            location.setLongitude(model.location.getLongitude());
            txtDetailAddress.setText(model.location.getAddress());
        }

        txtdetaildesc.setText(model.getDescription());

        if(model.uploadedImageUris != null && model.uploadedImageUris.size() > 0)
        {

            for(int i = 0; i < model.uploadedImageUris.size();i++) {

                Uri selectedImage =  Uri.parse(model.uploadedImageUris.get(i).toString());
                Log.d("food update page","image url" + selectedImage);
                imageViews[imageCount].setVisibility(View.VISIBLE);
                closeButtons[imageCount].setVisibility(View.VISIBLE);

                Glide.with(imageViews[imageCount].getContext())
                        .load(selectedImage.toString())
                        .error(android.R.drawable.ic_menu_gallery) // Error image
                        .into(imageViews[imageCount]);



//                imageViews[imageCount].setImageURI(model.uploadedImageUris.get(i));
                imageViews[imageCount].setTag(selectedImage);
                imageCount++;
            }
            if (imageCount == imageViews.length) {
                imgCapture.setEnabled(false);
                imgCapture.setVisibility(View.GONE);
            }
        }


    }
    // region for image capture
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
                isEditedPhotos =  true;
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
            isEditedPhotos =  true;
            if (imageCount < imageViews.length) {
                imgCapture.setEnabled(true);
                imgCapture.setVisibility(View.VISIBLE);
            }
        }
    }

    // end region

}