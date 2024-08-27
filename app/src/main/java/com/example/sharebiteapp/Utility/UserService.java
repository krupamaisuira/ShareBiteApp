package com.example.sharebiteapp.Utility;


import androidx.annotation.NonNull;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.Interface.UserCallback;
import com.example.sharebiteapp.ModelData.Location;
import com.example.sharebiteapp.ModelData.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

interface IUser
{
    void createUser(User user, OperationCallback callback);
    void getUserByID(String uid, UserCallback callback);
    void updateUser(User user, OperationCallback callback);
}

public class UserService implements IUser {

    private DatabaseReference reference;
    private static String _collectionName = "users";

    public UserService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }
    @Override
    public void createUser(User user,OperationCallback callback) {

        reference.child(_collectionName).child(user.getUserID()).setValue(user)
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

    public void getUserByID(String uid,UserCallback callback) {
        reference.child(_collectionName).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {

                    callback.onSuccess(user);

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

    public void deleteProfile(String uid,OperationCallback callback)
    {
        reference.child(_collectionName).child(uid).child("profiledeleted").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
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

    public void setbackdeleteProfile(String uid)
    {
        reference.child(_collectionName).child(uid).child("profiledeleted").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public void setNotification(String uid,Boolean notify,OperationCallback callback)
    {
        reference.child(_collectionName).child(uid).child("notification").setValue(notify).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
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
    @Override
    public void updateUser(User model, OperationCallback callback){

        reference.child(_collectionName).child(model.getUserID()).updateChildren(model.toMapUpdate())
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
