package com.example.sharebiteapp.Utility;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sharebiteapp.DonateFoodActivity;
import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
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

    public DonateFoodService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }
    @Override
    public void donatefood(DonateFood food, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();

               reference.child(_collectionName).child(newItemKey).setValue(food.toMap())
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
    public void getAllDonatedFood(final ListOperationCallback<List<DonateFood>> callback) {
        reference.child(_collectionName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DonateFood> donatedFoodList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DonateFood food = snapshot.getValue(DonateFood.class);
                    if (food != null) {
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

}
