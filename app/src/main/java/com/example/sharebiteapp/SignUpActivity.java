package com.example.sharebiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharebiteapp.ModelData.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
     EditText txtpwd,txtconfpwd,txtusername,txtmobile,txtemail;
     ImageView eye_password,eye_confpassword;
     Button btnsignup;
     TextView btnlogin;
     CheckBox chkterms;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtusername = findViewById(R.id.txtusername);
        txtmobile = findViewById(R.id.txtmobile);
        txtemail = findViewById(R.id.txtemail);
        btnsignup = findViewById(R.id.btnsignup);
        btnlogin = findViewById(R.id.btnlogin);
        txtpwd = findViewById(R.id.txtpwd);
        txtconfpwd = findViewById(R.id.txtconfpwd);
        eye_password = findViewById(R.id.eye_password);
        eye_confpassword = findViewById(R.id.eye_confpassword);
        chkterms = findViewById(R.id.chkterms);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

       //region button on click
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        
        // endregion


        //region icon password click
        eye_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(txtpwd, eye_password);
            }
        });

        eye_confpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(txtconfpwd, eye_confpassword);
            }
        });
        //endregion
    }

    //region user function

    //validate user
    public void registerUser()
    {
        String username = txtusername.getText().toString().trim();
        String mobile = txtmobile.getText().toString().trim();
        String email = txtemail.getText().toString().trim();
        String password = txtpwd.getText().toString();
        String confirmPassword = txtconfpwd.getText().toString();

        if (TextUtils.isEmpty(username)) {
            txtusername.setError("User name is required");
            txtusername.requestFocus();
            return ;
        }

        if (TextUtils.isEmpty(mobile)) {
            txtmobile.setError("Mobile number is required");
            txtmobile.requestFocus();
            return ;
        }
        else if(mobile.length() < 10)
        {
            txtmobile.setError("Mobile number must have 10 digits");
            txtmobile.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            txtemail.setError("Email is required");
            txtemail.requestFocus();
            return;
        }
        else if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
             txtemail.setError("Invalid email address");
             txtemail.requestFocus();
             return ;
        }

        if (TextUtils.isEmpty(password)) {
            txtpwd.setError("Password is required");
            txtpwd.requestFocus();
            return ;
        }
        else if(password.length() < 8)
        {
            txtpwd.setError("Password should be more than 8 char");
            txtpwd.requestFocus();
            return;
        }
        else if (!isValidPassword(password)) {
            txtpwd.setError("Password must contain at least one letter and one digit");
            txtpwd.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            txtconfpwd.setError("Passwords do not match");
            txtconfpwd.requestFocus();
            return ;
        }
        if (!chkterms.isChecked()) {
            Toast.makeText(SignUpActivity.this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }
        User newUser = new User(reference.push().getKey(),username,mobile,email,password);
        reference.child("users").child(newUser.getUserID()).setValue(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "registered failed ! please try after sometimes.", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private boolean isValidPassword(String password) {
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

    //toggle to text of password hide and show
    private void togglePasswordVisibility(EditText editText, ImageView imageView) {
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
    //endregion
}