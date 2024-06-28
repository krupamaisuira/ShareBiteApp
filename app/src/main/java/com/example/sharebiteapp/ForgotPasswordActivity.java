package com.example.sharebiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText  txtfgtpwdemail;
    Button btnresetpwd;
    TextView txtbacklogin;
    FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txtfgtpwdemail = findViewById(R.id.txtfgtpwdemail);
        btnresetpwd = findViewById(R.id.btnresetpwd);
        txtbacklogin = findViewById(R.id.txtbacklogin);
        mAuth = FirebaseAuth.getInstance();

        txtbacklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnresetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtfgtpwdemail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    txtfgtpwdemail.setError("Email address is required");
                    txtfgtpwdemail.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    txtfgtpwdemail.setError("Invalid email address");
                    txtfgtpwdemail.requestFocus();
                    return ;
                }
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Fail to send reset password email! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


    }
}