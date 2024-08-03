package com.example.sharebiteapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharebiteapp.CustomAdapter.DonatedListAdapter;
import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.Utility.DonateFoodService;

import java.util.ArrayList;
import java.util.List;

public class DonatedFoodListActivity extends BottomMenuActivity
{

    private RecyclerView recyclerView;
    private DonatedListAdapter adapter;
    DonateFoodService donatefoodservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
      //  setContentView(R.layout.activity_donated_food_list);
        getLayoutInflater().inflate(R.layout.activity_donated_food_list, findViewById(R.id.container));
        recyclerView = findViewById(R.id.recyclerView);
        donatefoodservice = new DonateFoodService();

        setAdapterDonatedFoodList();


    }
    public void setAdapterDonatedFoodList()
    {
        donatefoodservice.getAllDonatedFood(new ListOperationCallback<List<DonateFood>>() {
            @Override
            public void onSuccess(List<DonateFood> data) {

                adapter = new DonatedListAdapter(DonatedFoodListActivity.this, data);

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(DonatedFoodListActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {

                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}