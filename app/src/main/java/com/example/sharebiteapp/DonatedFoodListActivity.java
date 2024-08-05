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
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.Utility.DonateFoodService;

import java.util.ArrayList;
import java.util.List;

public class DonatedFoodListActivity extends BottomMenuActivity  implements DonatedListAdapter.OnDeleteClickListener
{

    private RecyclerView recyclerView;
    private DonatedListAdapter adapter;
    DonateFoodService donatefoodservice;
    List<DonateFood> list ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
      //  setContentView(R.layout.activity_donated_food_list);
        getLayoutInflater().inflate(R.layout.activity_donated_food_list, findViewById(R.id.container));
        recyclerView = findViewById(R.id.recyclerView);
        donatefoodservice = new DonateFoodService();
        list = new ArrayList<>();
        setAdapterDonatedFoodList();


    }
    public void setAdapterDonatedFoodList()
    {
        donatefoodservice.getAllDonatedFood(new ListOperationCallback<List<DonateFood>>() {
            @Override
            public void onSuccess(List<DonateFood> data) {
                list = new ArrayList<>(data);
                adapter = new DonatedListAdapter(DonatedFoodListActivity.this, list,DonatedFoodListActivity.this);

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

    public void onDeleteClick(int position) {
        DonateFood donateFood = list.get(position);

        donatefoodservice.deleteDonatedFood(donateFood.getDonationId(), new OperationCallback() {
            @Override
            public void onSuccess() {
               // list.remove(position);
                adapter.notifyItemRemoved(position);
               // adapter.removeItem(position);
                Toast.makeText(DonatedFoodListActivity.this, "Food donation deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DonatedFoodListActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}