package com.example.sharebiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Utility.UserService;
import com.example.sharebiteapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BottomMenuActivity {

    TextView txtLogout,txtprofileuser,txtchangepwd,txtdelprofile;
    private SessionManager sessionManager;
    UserService userService;
    FirebaseAuth mAuth;
    Switch switchNotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_profile);
        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.container));
        txtLogout = findViewById(R.id.txtLogout);
        txtprofileuser = findViewById(R.id.txtprofileusername);
        txtchangepwd = findViewById(R.id.txtchangepwd);
        txtdelprofile = findViewById(R.id.txtdelprofile);
        switchNotify = findViewById(R.id.switchNotify);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);
        userService = new UserService();

        if (sessionManager.userLoggedIn()) {
            txtprofileuser.setText(sessionManager.getUsername());
            switchNotify.setChecked(sessionManager.getNotificationStatus());
        }

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logoutUser();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        txtchangepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtdelprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user !=  null) {
                    userService.deleteProfile(sessionManager.getUserID(), new OperationCallback() {
                        @Override
                        public void onSuccess() {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Utils.saveCredentials(ProfileActivity.this,"", false);
                                        sessionManager.logoutUser();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        userService.setbackdeleteProfile(sessionManager.getUserID());
                                        Toast.makeText(ProfileActivity.this, "Delete Profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errMessage) {
                            userService.setbackdeleteProfile(sessionManager.getUserID());
                            Toast.makeText(ProfileActivity.this, "Delete Profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Delete Profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

      switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          boolean IsNotification = false;
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
            if(ischecked)
            {
                IsNotification = true;
            }
            else
            {
                IsNotification = false;
            }
            userService.setNotification(sessionManager.getUserID(), IsNotification, new OperationCallback() {
                @Override
                public void onSuccess() {
                    sessionManager.setNotificationStatus(IsNotification);
                    if (IsNotification) {

                        Toast.makeText(ProfileActivity.this, "Notifications are now ON", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Notifications have been turned OFF. You can enable them again in the settings.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(String errMessage) {
                    switchNotify.setChecked(sessionManager.getNotificationStatus());
                    Toast.makeText(ProfileActivity.this, "Notification setting failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
          }
      });
    }
}