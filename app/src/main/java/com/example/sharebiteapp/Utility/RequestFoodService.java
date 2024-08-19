package com.example.sharebiteapp.Utility;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.Interface.UserCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.Location;
import com.example.sharebiteapp.ModelData.RequestFood;
import com.example.sharebiteapp.ModelData.User;
import com.example.sharebiteapp.Utility.Interface.IRequestFood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestFoodService implements IRequestFood {
    private DatabaseReference reference;
    private static String _collectionName = "foodrequest";
    private UserService userService;
   // private DonateFoodService donateFoodService;

    public RequestFoodService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userService = new UserService();
//        donateFoodService = new DonateFoodService();
    }
    @Override
    public void requestfood(RequestFood model, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        model.requestId = newItemKey;
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
    public void isRequestFoodExist(RequestFood model, final ListOperationCallback<RequestFood> callback) {
        reference.child(_collectionName)
                .orderByChild("requestforId")
                .equalTo(model.requestforId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean dataFound = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RequestFood existingRequest = snapshot.getValue(RequestFood.class);

                            Log.d("request service", "existing requestedbyid : " + existingRequest.requestedBy);

                            if (existingRequest != null ) {
                                dataFound = true;

                                // Fetch user details of the existing request
                                userService.getUserByID(existingRequest.requestedBy, new UserCallback() {
                                    @Override
                                    public void onSuccess(User user) {
                                        Log.d("user service", "User details: " + user.toString());
                                        existingRequest.requestedUserDetail = user;

                                        if (callback != null) {
                                            callback.onSuccess(existingRequest);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Log.e("user service", "Error fetching user details: " + error);
                                        if (callback != null) {
                                            callback.onFailure(error);
                                        }
                                    }
                                });
                                return;
                            }
                        }

                        // If no matching RequestFood found
                        if (!dataFound && callback != null) {
                            callback.onSuccess(null);
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

    public  void requestFoodCancel(String uid,String cancelby,OperationCallback callback)
    {
        Map<String, Object> updates = new HashMap<>();
        updates.put("cancelon", Utils.getCurrentDatetime());
        updates.put("cancelby", cancelby);
        reference.child(_collectionName).child(uid).updateChildren(updates)
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
    public void fetchDonationRequests(String userId, ListOperationCallback<List<String>> callback) {

        reference.child(_collectionName).orderByChild("requestedBy").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    List<String> donationIds = new ArrayList<>();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String requestForId = snapshot.child("requestforId").getValue(String.class);
                            if (requestForId != null) {
                                donationIds.add(requestForId);
                            }
                        }
                        if (callback != null) {
                            callback.onSuccess(donationIds);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (callback != null) {
                            callback.onFailure(databaseError.getMessage());
                        }
                    }
                });
    }



}
