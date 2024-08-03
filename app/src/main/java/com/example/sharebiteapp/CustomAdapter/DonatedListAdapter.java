package com.example.sharebiteapp.CustomAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.R;
import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class DonatedListAdapter extends RecyclerView.Adapter<DonatedListAdapter.DonationViewHolder> {

    private List<DonateFood> donationList;
    private Context context;

    public DonatedListAdapter(Context context, List<DonateFood> donationList) {
        this.context = context;
        this.donationList = donationList != null ? donationList : new ArrayList<>();
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donated_food_card, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        DonateFood donateFood = donationList.get(position);

        holder.textView.setText(donateFood.getTitle());
        holder.statusTextView.setText(FoodStatus.getByIndex(donateFood.getStatus()).toString());
        FoodStatus status = FoodStatus.fromString(holder.statusTextView.getText().toString());
        Utils.setStatusColor(context,status, holder.statusTextView);
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, deleteIcon;
        TextView textView,statusTextView;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            textView = itemView.findViewById(R.id.textView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}
