package com.example.sharebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.Interface.UserCallback;
import com.example.sharebiteapp.ModelData.User;
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Utility.UserService;
import com.example.sharebiteapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends BottomMenuActivity {
    EditText txteditusername,txteditemail,txteditmobile;
    Button btnUpdateChanges;
    TextView btnChangePassword;
    private SessionManager sessionManager;
    UserService userService;
    Boolean notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.activity_edit_profile);
        getLayoutInflater().inflate(R.layout.activity_edit_profile, findViewById(R.id.container));
        txteditusername = findViewById(R.id.txteditusername);
        txteditemail = findViewById(R.id.txteditemail);
        txteditmobile = findViewById(R.id.txteditmobile);
        btnUpdateChanges = findViewById(R.id.btnUpdateChanges);
        btnChangePassword = findViewById(R.id.btneditPassword);
        sessionManager = SessionManager.getInstance(this);
        userService = new UserService();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnUpdateChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  updateUser();
            }
        });
        if (!sessionManager.userLoggedIn()) {
            Toast.makeText(EditProfileActivity.this, "Something wrong try after sometime", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        userService.getUserByID(sessionManager.getUserID(), new UserCallback() {
            @Override
            public void onSuccess(User user) {
                txteditusername.setText(user.getUsername());
                txteditemail.setText(user.getEmail());
                txteditmobile.setText(user.getMobilenumber());
                notification = user.getNotification();
            }

            @Override
            public void onFailure(String errMessage) {

                Toast.makeText(EditProfileActivity.this, "Something wrong try after sometime", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void updateUser()
    {
        String username = txteditusername.getText().toString().trim();
        String mobile = txteditmobile.getText().toString().trim();
        String email = txteditemail.getText().toString().trim();


        if (TextUtils.isEmpty(username)) {
            txteditusername.setError("User name is required");
            txteditusername.requestFocus();
            return ;
        }
        if (TextUtils.isEmpty(email)) {
            txteditemail.setError("Email is required");
            txteditemail.requestFocus();
            return;
        }
        else if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txteditemail.setError("Invalid email address");
            txteditemail.requestFocus();
            return ;
        }
        if (TextUtils.isEmpty(mobile)) {
            txteditmobile.setError("Mobile number is required");
            txteditmobile.requestFocus();
            return ;
        }
        else if(mobile.length() < 10)
        {
            txteditmobile.setError("Mobile number must have 10 digits");
            txteditmobile.requestFocus();
            return;
        }
        User user = new User(sessionManager.getUserID(), username,mobile, email);
        userService.createUser(user, new OperationCallback() {
            @Override
            public void onSuccess() {
                sessionManager.loginUser(user.getUserID(),user.getUsername(),user.getEmail(),notification);
                Toast.makeText(EditProfileActivity.this, "User profile edit successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(EditProfileActivity.this, "edit profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });





    }

}