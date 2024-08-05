package com.example.sharebiteapp.Utility;

import androidx.annotation.NonNull;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.Location;
import com.example.sharebiteapp.Utility.Interface.ILocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationService implements ILocation {

    private DatabaseReference reference;
    private static String _collectionName = "location";

    public LocationService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @Override
    public void addlocation(Location model, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();

        reference.child(_collectionName).child(newItemKey).setValue(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (callback != null) {
                            callback.onSuccess();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
    }
    public void deleteLocationByDonationID(String donationId, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("donationId")
                .equalTo(donationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            DataSnapshot firstMatch = dataSnapshot.getChildren().iterator().next();
                            String locationId = firstMatch.getKey();

                            reference.child(_collectionName).child(locationId).child("locationdeleted").setValue(true)
                                    .addOnSuccessListener(aVoid -> {
                                        if (callback != null) {
                                            callback.onSuccess();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (callback != null) {
                                            callback.onFailure(e.getMessage());
                                        }
                                    });
                        } else {
                            if (callback != null) {
                                callback.onFailure("No location found with the provided donation ID.");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (callback != null) {
                            callback.onFailure(databaseError.getMessage());
                        }
                    }
                });
    }

}
