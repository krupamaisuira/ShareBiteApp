    package com.example.sharebiteapp;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextUtils;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.util.Patterns;
    import android.view.Gravity;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.PopupWindow;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.example.sharebiteapp.CustomAdapter.AutocompletePredictionAdapter;
    import com.example.sharebiteapp.Interface.OperationCallback;
    import com.example.sharebiteapp.ModelData.CustomPrediction;
    import com.example.sharebiteapp.ModelData.DonateFood;
    import com.example.sharebiteapp.ModelData.Location;
    import com.example.sharebiteapp.ModelData.User;
    import com.example.sharebiteapp.Utility.DonateFoodService;
    import com.example.sharebiteapp.Utility.SessionManager;
    import com.example.sharebiteapp.Utility.UserService;
    import com.example.sharebiteapp.Utility.Utils;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.android.libraries.places.api.Places;
    import com.google.android.libraries.places.api.model.AutocompletePrediction;
    import com.google.android.libraries.places.api.model.Place;
    import com.google.android.libraries.places.api.model.TypeFilter;
    import com.google.android.libraries.places.api.net.FetchPlaceRequest;
    import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
    import com.google.android.libraries.places.api.net.PlacesClient;
    import com.google.firebase.FirebaseApp;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseUser;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;

    public class DonateFoodActivity extends BottomMenuActivity {
        TextView txtselectedAddress;
         Button btndonate;
         EditText txttitle,txtdesc,txtbtbefore,txtprice,locationInput;
        private SessionManager sessionManager;
         DonateFoodService donatefoodservice;
        ImageView iconAddLocation;
        private PlacesClient placesClient;
        private RecyclerView placesList;


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

            donatefoodservice = new DonateFoodService();
            location = new Location();
            sessionManager = SessionManager.getInstance(this);

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


            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), "AIzaSyDHWW-ftVRQBiatCrTFs3dLb4VxWAQGiJk");
            }
            placesClient = Places.createClient(this);
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

            DonateFood food = new DonateFood(sessionManager.getUserID(),title,desc,bestbefore,Double.parseDouble(price),location);
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

                // Adapter notification moved to after fetching place details
            }).addOnFailureListener(exception -> {
                Toast.makeText(DonateFoodActivity.this, "Error fetching predictions", Toast.LENGTH_SHORT).show();
                // Handle the error
            });
        }


    }