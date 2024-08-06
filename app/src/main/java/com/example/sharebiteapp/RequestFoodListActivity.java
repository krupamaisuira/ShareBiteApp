package com.example.sharebiteapp;

import android.content.Intent;
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
import com.example.sharebiteapp.CustomAdapter.RequestFoodListAdapter;
import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.Utility.DonateFoodService;
import com.example.sharebiteapp.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class RequestFoodListActivity extends BottomMenuActivity implements RequestFoodListAdapter.OnCartClickListener {

    private RecyclerView recyclerView;
    private RequestFoodListAdapter adapter;
    DonateFoodService donatefoodservice;
    List<DonateFood> list ;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.activity_request_food_list);
        getLayoutInflater().inflate(R.layout.activity_request_food_list, findViewById(R.id.container));
        recyclerView = findViewById(R.id.reqrecyclerView);
        donatefoodservice = new DonateFoodService();
        list = new ArrayList<>();
        sessionManager = SessionManager.getInstance(this);
        setAdapterFoodList();
    }
    public void setAdapterFoodList()
    {
        donatefoodservice.getAllRequestFoodList(sessionManager.getUserID(),new ListOperationCallback<List<DonateFood>>() {
            @Override
            public void onSuccess(List<DonateFood> data) {
                list = new ArrayList<>(data);
                adapter = new RequestFoodListAdapter(RequestFoodListActivity.this, list,RequestFoodListActivity.this);

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(RequestFoodListActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {

                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCartClick(int position) {
        DonateFood donateFood = list.get(position);

        Intent intent = new Intent(RequestFoodListActivity.this, RequestFoodDetailActivity.class);
        intent.putExtra("intentdonationId", donateFood.donationId);
        startActivity(intent);
    }
}