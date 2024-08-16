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
import com.google.firebase.database.Query;
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
    public void deleteImage(String donationId, String photoPath) {
        DatabaseReference photoRef = reference.child(_collectionName);

        // Query the photos by donationId
        Query photoQuery = photoRef.orderByChild("donationId").equalTo(donationId);

        photoQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && task.getResult().exists()) {
                    Log.d("DeleteImage", "Query successful. Processing results...");

                    boolean photoDeleted = false;

                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        String storedPhotoPath = snapshot.child("imagePath").getValue(String.class);
                        Log.d("DeleteImage", "Stored path: " + storedPhotoPath);

                        if (storedPhotoPath != null && storedPhotoPath.equals(photoPath)) {
                            Log.d("DeleteImage", "Matching photo found. Deleting...");

                            // Delete the image from Firebase Storage
                            deleteImageFromStorage(donationId, storedPhotoPath);

                            // Delete the document from 'photos' collection
                            snapshot.getRef().removeValue().addOnCompleteListener(removeTask -> {
                                if (removeTask.isSuccessful()) {
                                    Log.d("DeleteImage", "Image and database entry deleted successfully.");
                                } else {
                                    Log.e("DeleteImage", "Failed to delete database entry. Error: " + removeTask.getException());
                                }
                            });

                            photoDeleted = true;
                            break; // Stop looping after deleting the matching photo
                        }
                    }

                    if (!photoDeleted) {
                        Log.w("DeleteImage", "Photo path not found in the database.");
                    }
                } else {
                    Log.e("DeleteImage", "No results found or results are empty.");
                }
            } else {
                Log.e("DeleteImage", "Task failed. Error: " + task.getException());
            }
        });
    }




    private void deleteImageFromStorage(String donationId, String photoPath) {
        // Reference to the image in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoPath);

        // Delete the image from Firebase Storage
        storageRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DeleteImage", "Image deleted from Firebase Storage.");
            } else {
                Log.e("DeleteImage", "Failed to delete image from Firebase Storage.");
            }
        });
    }

    public void updatePhotoOrder(String donationId, OperationCallback callback) {
        DatabaseReference photosRef = FirebaseDatabase.getInstance().getReference("photos");

        // Query the photos by donationId
        photosRef.orderByChild("donationId").equalTo(donationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (callback != null) {
                        callback.onFailure("No photos found for this donationId.");
                    }
                    return;
                }

                int order = 1;
                boolean[] failureOccurred = {false};  // Track if a failure occurs

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Update the order field for each photo
                    snapshot.getRef().child("order").setValue(order)
                            .addOnFailureListener(e -> {
                                // On failure, trigger the callback and set failureOccurred to true
                                if (!failureOccurred[0]) { // Ensure failure is only handled once
                                    failureOccurred[0] = true;
                                    if (callback != null) {
                                        callback.onFailure("Error updating photo order: " + e.getMessage());
                                    }
                                }
                            });

                    order++;
                }

                // After all updates are triggered, if no failure occurred, trigger success callback
                if (!failureOccurred[0]) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (callback != null) {
                    callback.onFailure("Database operation cancelled: " + databaseError.getMessage());
                }
            }
        });
    }




//    public void reorderImage(String donationId, Uri[] imageUris) {
//        List<Uri> firebaseStorageUris = new ArrayList<>();
//        List<Uri> galleryImageUris = new ArrayList<>();
//        for (int i = 0; i < imageUris.length; i++) {
//            int order = i + 1;
//            Uri imageUri = imageUris[i];
//
//            // Check if the URI is from Firebase Storage or the gallery
//            if (imageUri != null && imageUri.getScheme() != null) {
//                String scheme = imageUri.getScheme();
//
//                // Check if it's a Firebase Storage URI
//                if (scheme.equals("gs") || (scheme.equals("https") && imageUri.toString().contains("firebasestorage"))) {
//                    firebaseStorageUris.add(imageUri);
//                }
//               else
//                {
//                    galleryImageUris.add(imageUri);
//                }
//            }
//        }
//        int count = 1;
//        if(firebaseStorageUris != null && firebaseStorageUris.size() > 0)
//        {
//            DatabaseReference photosRef = FirebaseDatabase.getInstance().getReference("photos");
//            Query photoQuery = photosRef.orderByChild("donationId").equalTo(donationId);
//        }
//
//
//    }

}
