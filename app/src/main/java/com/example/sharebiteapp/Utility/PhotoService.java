package com.example.sharebiteapp.Utility;

import androidx.annotation.NonNull;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.Photos;
import com.example.sharebiteapp.Utility.Interface.IPhoto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhotoService implements IPhoto {
    private DatabaseReference reference;
    private static String _collectionName = "photos";

    public PhotoService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
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
}
