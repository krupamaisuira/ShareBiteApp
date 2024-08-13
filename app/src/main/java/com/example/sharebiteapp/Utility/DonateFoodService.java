    package com.example.sharebiteapp.Utility;

    import android.net.Uri;
    import android.util.Log;
    import android.widget.Toast;

    import androidx.annotation.NonNull;

    import com.example.sharebiteapp.DonateFoodActivity;
    import com.example.sharebiteapp.Interface.ListOperationCallback;
    import com.example.sharebiteapp.Interface.OperationCallback;
    import com.example.sharebiteapp.Interface.UserCallback;
    import com.example.sharebiteapp.ModelData.DonateFood;
    import com.example.sharebiteapp.ModelData.Location;
    import com.example.sharebiteapp.ModelData.Photos;
    import com.example.sharebiteapp.ModelData.User;
    import com.example.sharebiteapp.Utility.Interface.IDonateFood;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

    public class DonateFoodService implements IDonateFood {
        private DatabaseReference reference;
        private static String _collectionName = "donatefood";
        private LocationService locationService;
        private PhotoService photoService;
        private StorageReference storageReference;
        public DonateFoodService() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            locationService = new LocationService();
            photoService = new PhotoService();
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        @Override
        public void donatefood(DonateFood food, OperationCallback callback) {
            String newItemKey = reference.child(_collectionName).push().getKey();
                food.setDonationId(newItemKey);
                   reference.child(_collectionName).child(newItemKey).setValue(food.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (callback != null) {
                                addLocationForDonatedFood(newItemKey, food.location, callback);
                                photoService.uploadImages(newItemKey,food.imageUris,callback);

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
        public void addLocationForDonatedFood(String donationid, Location location, OperationCallback callback) {
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
        public void getAllDonatedFood(String userId,final ListOperationCallback<List<DonateFood>> callback) {
            reference.child(_collectionName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<DonateFood> donatedFoodList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DonateFood food = snapshot.getValue(DonateFood.class);
                        Log.d("donate food service","user id  " + userId);
                        Log.d("donate food service","donation by id  " + food.getDonatedBy());
                        if (food != null && food.fooddeleted == false  && userId.equals(food.getDonatedBy())) {
                            Log.d("donate food service","inside food list  " + food.getDonatedBy());
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
        public void deleteLocation(String donationid, OperationCallback callback) {

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

            reference.child(_collectionName).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    DonateFood food = snapshot.getValue(DonateFood.class);

                    if (food != null) {
                        food.setDonationId(uid);

                        Log.d("d", "DonateFood found with donationId: " + food.getDonationId());

                        // Fetch photos asynchronously
                        getAllPhotos(food.getDonationId(), new ListOperationCallback<List<Uri>>() {
                            @Override
                            public void onSuccess(List<Uri> imageUris) {
                                Log.d("d", "photo data: " + imageUris.size());
                                food.setUploadedImageUris(imageUris);

                                // Fetch location asynchronously
                                locationService.getLocationByDonationId(food.getDonationId(), new ListOperationCallback<Location>() {
                                    @Override
                                    public void onSuccess(Location data) {
                                        Log.d("d", "location data: " + data.address);
                                        food.location = data;

                                        // All data is populated, return the final DonateFood object
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
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e("d", "Error fetching photos: " + error);
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


        // region request food
public void getAllRequestFoodList(String userId,final ListOperationCallback<List<DonateFood>> callback) {
    reference.child(_collectionName).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<DonateFood> donatedFoodList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                DonateFood food = snapshot.getValue(DonateFood.class);
                if (food != null && food.fooddeleted == false && food.status == FoodStatus.Available.getIndex() && !userId.equals(food.getDonatedBy())) {
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


// endregion

private void getAllPhotos(String donationId,ListOperationCallback<List<Uri>> callback)
{
    photoService.getAllPhotosByDonationId(donationId, new ListOperationCallback<List<Uri>>() {
        @Override
        public void onSuccess(List<Uri> imageUri) {
            Log.d("d", "phot data: " + imageUri.size());

            if (callback != null) {
                callback.onSuccess(imageUri);
            }
        }

        @Override
        public void onFailure(String error) {
            Log.e("d", "Error fetching data: " + error);
            if (callback != null) {
                callback.onFailure(error);
            }
        }
    });


}
        @Override
        public void updatedonatedfood(DonateFood food, OperationCallback callback) {
            food.setUpdatedon(Utils.getCurrentDatetime());
            reference.child(_collectionName).child(food.donationId).updateChildren(food.toMap())
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

    }
