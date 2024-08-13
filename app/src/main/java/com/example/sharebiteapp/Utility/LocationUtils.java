package com.example.sharebiteapp.Utility;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharebiteapp.CustomAdapter.AutocompletePredictionAdapter;
import com.example.sharebiteapp.DonateFoodActivity;
import com.example.sharebiteapp.ModelData.CustomPrediction;
import com.example.sharebiteapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationUtils {

    private AutocompletePredictionAdapter adapter;
    private List<CustomPrediction> predictions = new ArrayList<>();
    private CustomPrediction selectedLocation;
    private PlacesClient placesClient;
    private Context context;
    private RecyclerView placesList;

    public LocationUtils(Context context) {
        this.context = context;
        if (!Places.isInitialized()) {
            Places.initialize(context.getApplicationContext(), "AIzaSyDHWW-ftVRQBiatCrTFs3dLb4VxWAQGiJk");
        }
        placesClient = Places.createClient(context);
    }
    public CustomPrediction getSelectedLocation() {
        return selectedLocation;
    }
    public   void fetchPlaceDetails(CustomPrediction customPrediction) {
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
    public  void showAddLocationPopup(View view, TextView txtselectedAddress, String defaultText) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_location, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        ImageView btnClose = popupView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> popupWindow.dismiss());

        TextView  locationInput = popupView.findViewById(R.id.location_input);
        placesList = popupView.findViewById(R.id.places_list);

        // Set up RecyclerView
        placesList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AutocompletePredictionAdapter(predictions, context, prediction -> {
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
                Toast.makeText(context, "No place selected", Toast.LENGTH_SHORT).show();
                txtselectedAddress.setText(defaultText);
                return;
            }
            else
            {

                txtselectedAddress.setText(selectedLocation.getFullAddress());
                popupWindow.dismiss();
            }
        });
    }

    public  void fetchAutocompletePredictions(String query) {
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
            Toast.makeText(context, "Error fetching predictions", Toast.LENGTH_SHORT).show();

        });
    }
}
