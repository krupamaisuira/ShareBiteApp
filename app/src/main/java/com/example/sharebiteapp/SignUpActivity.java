package com.example.sharebiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharebiteapp.ModelData.User;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.Utility.UserService;
import com.example.sharebiteapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
     EditText txtpwd,txtconfpwd,txtusername,txtmobile,txtemail;
     ImageView eye_password,eye_confpassword;
     Button btnsignup;
     TextView btnlogin;
     CheckBox chkterms;
     FirebaseAuth mAuth;

     UserService userService;
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

        userService = new UserService();
        mAuth = FirebaseAuth.getInstance();


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
                Utils.togglePasswordVisibility(txtpwd, eye_password);
            }
        });

        eye_confpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtconfpwd, eye_confpassword);
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
        else if (!Utils.isValidPassword(password)) {
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

     mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
             if(task.isSuccessful())
             {
                 FirebaseUser currentUser = mAuth.getCurrentUser();
                 if (currentUser != null) {
                     String uid = currentUser.getUid();
                     User user = new User(uid, username,mobile, email);
                     userService.createUser(user, new OperationCallback() {
                         @Override
                         public void onSuccess() {
                             Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                             startActivity(intent);
                             finish();
                         }

                         @Override
                         public void onFailure(String errMessage) {
                             Toast.makeText(SignUpActivity.this, "Sign up failed! Please try again later.", Toast.LENGTH_SHORT).show();
                         }
                     });
                 }else {
                     Toast.makeText(SignUpActivity.this, "Sign up failed! Please try again later.", Toast.LENGTH_SHORT).show();
                 }

             }
             else
             {
                 Toast.makeText(SignUpActivity.this, "Sign up failed! Please try again later.", Toast.LENGTH_SHORT).show();
             }
         }
     });

    }



}