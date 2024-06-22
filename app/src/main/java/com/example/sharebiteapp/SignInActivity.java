package com.example.sharebiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharebiteapp.ModelData.User;
import com.example.sharebiteapp.Utility.UserExistenceCallback;
import com.example.sharebiteapp.Utility.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    EditText txtloginusername,txtloginpwd;
    Button btnforgotpwd,btnsignin;
    TextView btnregister;
    ImageView eye_loginpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        txtloginusername = findViewById(R.id.txtloginusername);
        txtloginpwd = findViewById(R.id.txtloginpwd);
        btnforgotpwd = findViewById(R.id.btnforgotpwd);
        btnsignin = findViewById(R.id.btnsignin);
        btnregister = findViewById(R.id.btnregister);
        eye_loginpwd = findViewById(R.id.eye_loginpwd);

        //region button click
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = txtloginpwd.getText().toString();
                if (TextUtils.isEmpty(txtloginusername.getText().toString().trim())) {
                    txtloginusername.setError("User name /Email address is required");
                    txtloginusername.requestFocus();
                    return ;
                }
                if (TextUtils.isEmpty(password)) {
                    txtloginpwd.setError("Password is required");
                    txtloginpwd.requestFocus();
                    return ;
                }
                else if(password.length() < 8)
                {
                    txtloginpwd.setError("Password should be more than 8 char");
                    txtloginpwd.requestFocus();
                    return;
                }
                else if (!Utils.isValidPassword(password)) {
                    txtloginpwd.setError("Password must contain at least one letter and one digit");
                    txtloginpwd.requestFocus();
                    return;
                }
                validateUser();
            }
        });
        eye_loginpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtloginpwd, eye_loginpwd);
            }
        });

        //endregion

    }

    //region validate user in database
    private void validateUser()
    {
        String usernameEmailLogin = txtloginusername.getText().toString().trim();
        Utils.checkUserExists(SignInActivity.this,usernameEmailLogin,usernameEmailLogin,new UserExistenceCallback() {

            @Override
            public void onResult(boolean exists,DataSnapshot snapshot) {
                if(exists)
                {
                   validatePassword(snapshot);
                }
                else
                {

                    Toast.makeText(SignInActivity.this, "Invalid username/email address",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


//    private void validateUser() {
//
//        String usernameEmailLogin = txtloginusername.getText().toString().trim();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
//
//        Query usernameQuery = reference.orderByChild("username").equalTo(usernameEmailLogin);
//        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot usernameSnapshot) {
//                if (usernameSnapshot.exists()) {
//                    validatePassword(usernameSnapshot);
//                } else {
//
//                    Query emailQuery = reference.orderByChild("email").equalTo(usernameEmailLogin);
//                    emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot emailSnapshot) {
//                            if (emailSnapshot.exists()) {
//                                validatePassword(emailSnapshot);
//                            } else {
//
//                                Toast.makeText(SignInActivity.this, "Invalid username/email or password", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            Toast.makeText(SignInActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle possible errors.
//                Toast.makeText(SignInActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void validatePassword(DataSnapshot snapshot) {
        boolean userValid = false;
        String password = txtloginpwd.getText().toString().trim();

        DataSnapshot userSnapshot = snapshot.getChildren().iterator().next(); // Get the single user snapshot

        String dbPassword = userSnapshot.child("password").getValue(String.class);
            if (dbPassword != null && dbPassword.equals(password)) {
                userValid = true;
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

        if (!userValid) {
            Toast.makeText(SignInActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion
}