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
import com.example.sharebiteapp.Utility.SessionManager;
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
    EditText txttitle,txtdesc,txtbtbefore,txtprice,locationInput;
    private SessionManager sessionManager;
    DonateFoodService donatefoodservice;
    ImageView iconAddLocation,imgview;
    private PlacesClient placesClient;
    private RecyclerView placesList;
    private static final int PICK_IMAGE = 1;
    private ImageView[] imageViews;
    private ImageButton imgCapture;
    private ImageButton[] closeButtons;
    private int imageCount = 0;

    private AutocompletePredictionAdapter adapter;
    private List<CustomPrediction> predictions = new ArrayList<>();
    private CustomPrediction selectedLocation;
    Location location;

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
                showAddLocationPopup(v);
            }
        });


        imgCapture = findViewById(R.id.imgcapture);
        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDHWW-ftVRQBiatCrTFs3dLb4VxWAQGiJk");
        }
        placesClient = Places.createClient(this);
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
private Uri[] getImageUrisFromImageViews() {
    List<Uri> uriList = new ArrayList<>();

    for (ImageView imageView : imageViews) {
        Object tag = imageView.getTag();
        if (tag instanceof Uri) {
            Uri uri = (Uri) tag;
            uriList.add(uri);
            Log.d("DonateFoodActivity", "Retrieved URI: " + uri.toString());
        } else {
            Log.w("DonateFoodActivity", "Tag is not a URI: " + tag);
        }
    }

    // Convert List to Array
    Uri[] uris = uriList.toArray(new Uri[0]);

    return uris;
}

    public void addDonateFood()
    {
        String title = txttitle.getText().toString().trim();
        String desc = txtdesc.getText().toString().trim();
        String bestbefore = txtbtbefore.getText().toString().trim();
        String price = txtprice.getText().toString();

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
        if (selectedLocation == null) {
            Toast.makeText(this, "Please add address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            price = "0";
        }

        // set imageuri here
        Uri[] imageUris = getImageUrisFromImageViews();


        if(imageUris.length == 0)
        {
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            return;
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

    private void fetchPlaceDetails(CustomPrediction customPrediction) {
        FetchPlaceRequest request = FetchPlaceRequest.builder(customPrediction.getPlaceId(), Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG)).build();

        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            LatLng latLng = place.getLatLng();
            if (latLng != null) {
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                customPrediction.setFullAddress(place.getAddress());
                customPrediction.setLatitude(latitude);
                customPrediction.setLongitude(longitude);
                adapter.notifyDataSetChanged();

            }


        }).addOnFailureListener(exception -> {
            // Handle the error
        });
    }
    private void showAddLocationPopup(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_location, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        ImageView btnClose = popupView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> popupWindow.dismiss());

        locationInput = popupView.findViewById(R.id.location_input);
        placesList = popupView.findViewById(R.id.places_list);

        // Set up RecyclerView
        placesList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AutocompletePredictionAdapter(predictions, this, prediction -> {
            locationInput.setText(prediction.getFullAddress());
            selectedLocation = prediction;
        });
        placesList.setAdapter(adapter);

        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    fetchAutocompletePredictions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button btnSave = popupView.findViewById(R.id.btn_add_location);
        btnSave.setOnClickListener(v -> {

            if (selectedLocation == null) {
                Toast.makeText(this, "No place selected", Toast.LENGTH_SHORT).show();
                txtselectedAddress.setText("Add Address");
                return;
            }
            else
            {
                location.setAddress(selectedLocation.getFullAddress());
                location.setLatitude(selectedLocation.getLatitude());
                location.setLongitude(selectedLocation.getLongitude());
                txtselectedAddress.setText(location.getAddress());
                popupWindow.dismiss();
            }
        });
    }

    private void fetchAutocompletePredictions(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            predictions.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                CustomPrediction customPrediction = new CustomPrediction(prediction.getPlaceId(), prediction.getPrimaryText(null).toString());
                predictions.add(customPrediction); // Add to predictions list
                fetchPlaceDetails(customPrediction); // Fetch place details
            }


        }).addOnFailureListener(exception -> {
            Toast.makeText(DonateFoodActivity.this, "Error fetching predictions", Toast.LENGTH_SHORT).show();

        });
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