package com.example.sharebiteapp.Utility;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.Location;
import com.example.sharebiteapp.ModelData.Photos;
import com.example.sharebiteapp.Utility.Interface.IPhoto;
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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PhotoService implements IPhoto {
    private DatabaseReference reference;
    private static String _collectionName = "photos";
    private StorageReference storageReference;
    public PhotoService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }



    @Override
    public void addFoodPhotos(Photos model, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        model.setPhotoId(newItemKey);
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

    public void getAllPhotosByDonationId(String uid, ListOperationCallback<List<Uri>> callback) {
        reference.child(_collectionName).orderByChild("donationId")
                .equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<Photos> photosList = new ArrayList<>();
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                Photos photo = childSnapshot.getValue(Photos.class);
                                if (photo != null) {
                                    photosList.add(photo);
                                } else {
                                    Log.d("photo service", "photo is null");
                                }
                            }

                            photosList.sort(Comparator.comparingInt(Photos::getOrder));

                            List<Uri> imageUrls = new ArrayList<>();
                            for (Photos photo : photosList) {
                                Uri uri = Uri.parse(photo.getImagePath());
                                imageUrls.add(uri);
                            }

                            if (callback != null) {
                                callback.onSuccess(imageUrls);

                            }
                        } else {
                            Log.d("photo service", "snapshot does not exist");
                            if (callback != null) {
                                callback.onFailure("Photos not found");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
    }


    public void uploadImages(String donationId, Uri[] imageUris,OperationCallback callback) {
        for (int i = 0; i < imageUris.length; i++) {
            int order = i + 1;
            Uri imageUri = imageUris[i];
            StorageReference fileReference = storageReference.child("foodimages/" + donationId + "/" + UUID.randomUUID().toString());

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUrl -> {

                Photos photos = new Photos(donationId,downloadUrl.toString(), order);
                addFoodPhotos(photos, new OperationCallback() {
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
            })).addOnFailureListener(e -> {

            });
        }
    }
  
}
