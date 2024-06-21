package com.example.sharebiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class SignUpActivity extends AppCompatActivity {
    private EditText txtpwd,txtconfpwd;
    private ImageView eye_password,eye_confpassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtpwd = findViewById(R.id.txtpwd);
        txtconfpwd = findViewById(R.id.txtconfpwd);

        eye_password = findViewById(R.id.eye_password);
        eye_confpassword = findViewById(R.id.eye_confpassword);

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
    }

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
}