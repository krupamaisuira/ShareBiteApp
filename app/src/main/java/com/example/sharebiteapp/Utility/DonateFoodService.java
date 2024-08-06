    package com.example.sharebiteapp.Utility;

    import android.util.Log;
    import android.widget.Toast;

    import androidx.annotation.NonNull;

    import com.example.sharebiteapp.DonateFoodActivity;
    import com.example.sharebiteapp.Interface.ListOperationCallback;
    import com.example.sharebiteapp.Interface.OperationCallback;
    import com.example.sharebiteapp.Interface.UserCallback;
    import com.example.sharebiteapp.ModelData.DonateFood;
    import com.example.sharebiteapp.ModelData.Location;
    import com.example.sharebiteapp.ModelData.User;
    import com.example.sharebiteapp.Utility.Interface.IDonateFood;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;

    public class DonateFoodService implements IDonateFood {
        private DatabaseReference reference;
        private static String _collectionName = "donatefood";
        private LocationService locationService;

        public DonateFoodService() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            locationService = new LocationService();
        }
        @Override
        public void donatefood(DonateFood food, OperationCallback callback) {
            String newItemKey = reference.child(_collectionName).push().getKey();

                   reference.child(_collectionName).child(newItemKey).setValue(food.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (callback != null) {
                                addLocationForDonatedFood(newItemKey, food.location, callback);
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
        private void addLocationForDonatedFood(String donationid, Location location, OperationCallback callback) {
            Location selectedadd = new Location(donationid,location.getAddress(),location.getLatitude(),location.getLongitude());
            locationService.addlocation(selectedadd, new OperationCallback() {
                @Override
                public void onSuccess() {
                    // Handle success scenario, possibly update food record with locationId
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(String error) {
                    // Handle failure scenario
                    if (callback != null) {
                        callback.onFailure(error);
                    }
                }
            });
        }
        public void getAllDonatedFood(final ListOperationCallback<List<DonateFood>> callback) {
            reference.child(_collectionName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<DonateFood> donatedFoodList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DonateFood food = snapshot.getValue(DonateFood.class);
                        if (food != null && food.fooddeleted == false) {
                            food.setDonationId(snapshot.getKey());
                            donatedFoodList.add(food);
                        }
                    }
                    if (callback != null) {
                        callback.onSuccess(donatedFoodList);
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

        public void deleteDonatedFood(String uid,OperationCallback callback)
        {
            reference.child(_collectionName).child(uid).child("fooddeleted").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    if (callback != null) {
                        deleteLocation(uid, callback);
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
        private void deleteLocation(String donationid, OperationCallback callback) {

            locationService.deleteLocationByDonationID(donationid, new OperationCallback() {
                @Override
                public void onSuccess() {
                    // Handle success scenario, possibly update food record with locationId
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(String error) {
                    // Handle failure scenario
                    if (callback != null) {
                        callback.onFailure(error);
                    }
                }
            });
        }

        public void getDonationDetail(String uid, ListOperationCallback<DonateFood> callback) {
            Log.d("d", "getDonationDetail called with uid: " + uid);
            reference.child(_collectionName).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("d", "onDataChange called for uid: " + uid);
                    DonateFood food = snapshot.getValue(DonateFood.class);
                    if (food != null) {
                        food.setDonationId(uid);
                        Log.d("d", "DonateFood found with donationId: " + food.getDonationId());
                        locationService.getLocationByDonationId(food.donationId, new ListOperationCallback<Location>() {
                            @Override
                            public void onSuccess(Location data) {
                                Log.d("d", "location data: " + data.address);
                                food.location = data;
                                if (callback != null) {
                                    callback.onSuccess(food);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e("d", "Error fetching location: " + error);
                                if (callback != null) {
                                    callback.onFailure(error);
                                }
                            }
                        });
                    } else {
                        Log.e("d", "DonateFood not found for donationId: " + uid);
                        if (callback != null) {
                            callback.onFailure("DonateFood not found.");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError e) {
                    Log.e("d", "DatabaseError: " + e.getMessage());
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                }
            });
        }


    }
