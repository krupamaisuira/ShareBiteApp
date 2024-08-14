package com.example.sharebiteapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.RequestFood;
import com.example.sharebiteapp.Utility.Interface.IRequestFood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RequestFoodService implements IRequestFood {
    private DatabaseReference reference;
    private static String _collectionName = "foodrequest";

    public RequestFoodService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }
//    @Override
//    public void requestfood(RequestFood model, OperationCallback callback) {
//        String newItemKey = reference.child(_collectionName).push().getKey();
//
//        reference.child(_collectionName).child(newItemKey).setValue(model)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        if (callback != null) {
//                            callback.onSuccess();
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if (callback != null) {
//                            callback.onFailure(e.getMessage());
//                        }
//                    }
//                });
//    }
    public void requestfood(RequestFood model, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("requestforId")
                .equalTo(model.requestforId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean requestExists = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RequestFood existingRequest = snapshot.getValue(RequestFood.class);
                            Log.d("request service","requestedbyid : " + model.requestedBy);
                            Log.d("request service","exisiting requestedbyid : " + existingRequest.requestedBy);
                            if (existingRequest != null && existingRequest.requestedBy.equals(model.requestedBy)) {
                                requestExists = true;
                                break;
                            }
                        }
                        if (requestExists) {
                            if (callback != null) {
                                callback.onSuccess();
                            }
                        } else {
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
