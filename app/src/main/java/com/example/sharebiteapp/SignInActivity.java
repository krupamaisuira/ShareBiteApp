package com.example.sharebiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Interface.UserCallback;
import com.example.sharebiteapp.Utility.UserService;
import com.example.sharebiteapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

public class SignInActivity extends AppCompatActivity {

    EditText txtloginusername,txtloginpwd;
    Button btnforgotpwd,btnsignin;
    TextView btnregister;
    ImageView eye_loginpwd;
    CheckBox chkrememberMe;
    private SessionManager sessionManager;
  FirebaseAuth mAuth ;
    UserService userService;

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
        chkrememberMe = findViewById(R.id.chkrememberMe);
        sessionManager = SessionManager.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        userService = new UserService();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedEmail = preferences.getString("email", "");
        boolean rememberMeChecked = preferences.getBoolean("rememberMe", false);

        txtloginusername.setText(savedEmail);
        chkrememberMe.setChecked(rememberMeChecked);
        //region signup and signin button click
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
                    txtloginusername.setError("Email address is required");
                    txtloginusername.requestFocus();
                    return ;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(txtloginusername.getText().toString()).matches()){
                    txtloginusername.setError("Invalid email address");
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

        // region forgot password
        btnforgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // end region

    }

    //region validate user in database
    private void validateUser()
    {
        String usernameEmailLogin = txtloginusername.getText().toString().trim();
        String password = txtloginpwd.getText().toString().trim();

       mAuth.signInWithEmailAndPassword(usernameEmailLogin,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {

               if(task.isSuccessful())
               {

                   if (chkrememberMe.isChecked()) {
                       Utils.saveCredentials(SignInActivity.this, usernameEmailLogin, true);
                   } else {
                       Utils.saveCredentials(SignInActivity.this,"", false);
                   }
                   FirebaseUser user = mAuth.getCurrentUser();
                   if (user != null) {

                       userService.getUserByID(user.getUid(), new UserCallback() {
                           @Override
                           public void onSuccess(User user) {
                               sessionManager.loginUser(user.getUserID(),user.getUsername(),user.getEmail(),user.getNotification());
                               Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
                               startActivity(intent);
                               finish();
                           }

                           @Override
                           public void onFailure(String errMessage) {
                               txtloginpwd.setText("");
                               Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
                   else
                   {
                       txtloginpwd.setText("");
                       Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
                   }
               }
               else
               {
                   txtloginpwd.setText("");
                   Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
               }
           }
       });




    }




    //endregion
}