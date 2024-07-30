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
import android.widget.Toast;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.User;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.SessionManager;
import com.example.sharebiteapp.Utility.UserService;
import com.example.sharebiteapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class DonateFoodActivity extends BottomMenuActivity {
     Button btndonate;
     EditText txttitle,txtdesc,txtbtbefore,txtprice;
    private SessionManager sessionManager;
     DonateFoodService donatefoodservice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_donate_food);
        getLayoutInflater().inflate(R.layout.activity_donate_food, findViewById(R.id.container));
        txttitle = findViewById(R.id.txttitle);
        txtdesc = findViewById(R.id.txtdesc);
        txtbtbefore = findViewById(R.id.txtbtbefore);
        txtprice = findViewById(R.id.txtprice);
        btndonate = findViewById(R.id.btndonate);

        donatefoodservice = new DonateFoodService();
        sessionManager = SessionManager.getInstance(this);

        btndonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDonateFood();
            }
        });
    }

    public void addDonateFood()
    {
        String title = txttitle.getText().toString().trim();
        String desc = txtdesc.getText().toString().trim();
        String bestbefore = txtbtbefore.getText().toString().trim();
        String price = txtprice.getText().toString();

        if (TextUtils.isEmpty(title)) {
            txttitle.setError("Title is required");
            txttitle.requestFocus();
            return ;
        }

        if (TextUtils.isEmpty(desc)) {
            txtdesc.setError("Description is required");
            txtdesc.requestFocus();
            return ;
        }




        DonateFood food = new DonateFood(sessionManager.getUserID(),title,desc,bestbefore,Double.parseDouble(price));
        donatefoodservice.donatefood(food, new OperationCallback() {
            @Override
            public void onSuccess() {

                Intent intent = new Intent(DonateFoodActivity.this, DonationSuccessActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(DonateFoodActivity.this, "Food Donate failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}