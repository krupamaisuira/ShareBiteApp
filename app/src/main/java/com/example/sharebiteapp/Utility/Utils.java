package com.example.sharebiteapp.Utility;

import android.content.Context;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sharebiteapp.R;
import com.example.sharebiteapp.SignInActivity;
import com.example.sharebiteapp.SignUpActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {


    public static boolean isValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLetter(password.charAt(i))) {
                hasLetter = true;
            } else if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
            }
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        return false;
    }
    public static String getCurrentDatetime() {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return datetimeFormat.format(new Date());
    }
    public static void togglePasswordVisibility(EditText editText, ImageView imageView) {
        if (editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            // Show Password
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.hidden);
        } else {
            // Hide Password
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.eye);
        }
        // Move the cursor to the end of the text
        editText.setSelection(editText.getText().length());
    }

    // region check user  in database
    public static void checkUserExists(Context context,String username,String email, final UserExistenceCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // Query for username
        Query usernameQuery = reference.orderByChild("username").equalTo(username);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usernameSnapshot) {
                if (usernameSnapshot.exists()) {
                    DataSnapshot userSnapshot = usernameSnapshot.getChildren().iterator().next(); // Get the single user snapshot


                    Boolean isDeleted = userSnapshot.child("profiledeleted").getValue(Boolean.class);
                    if (isDeleted != null && !isDeleted) {
                        callback.onResult(true, usernameSnapshot);
                    }
                    else {

                        callback.onResult(false,null);
                    }

                } else {
                    // Query for email
                    Query emailQuery = reference.orderByChild("email").equalTo(email);
                    emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot emailSnapshot) {
                            if (emailSnapshot.exists()) {
                                DataSnapshot userSnapshot = emailSnapshot.getChildren().iterator().next(); // Get the single user snapshot

                                Boolean isDeleted = userSnapshot.child("profiledeleted").getValue(Boolean.class);
                                if (isDeleted != null && !isDeleted) {
                                    callback.onResult(true, emailSnapshot);

                                }

                            } else {
                                // Neither username nor email exists
                                callback.onResult(false,null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors.
                            Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // endregion

}
