package com.example.sharebiteapp.CustomAdapter;


import android.content.Context;
import android.util.Log;
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

    public List<DonateFood> donationList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;
    private OnEditClickListener onEditClickListener;
    private OnTextClickListener onTextClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    public interface OnEditClickListener {
        void editDonateFood(int position);
    }
    public interface OnTextClickListener {
        void redirectToDetailPage(int position);
    }

    public DonatedListAdapter(Context context, List<DonateFood> donationList) {
        this.context = context;
        this.donationList = donationList != null ? donationList : new ArrayList<>();
    }
    public DonatedListAdapter(Context context, List<DonateFood> donationList, OnDeleteClickListener onDeleteClickListener, OnTextClickListener onTextClickListener,OnEditClickListener onEditClickListener) {
        this.context = context;
        this.donationList = donationList != null ? donationList : new ArrayList<>();
        this.onDeleteClickListener = onDeleteClickListener;
        this.onTextClickListener = onTextClickListener;
        this.onEditClickListener = onEditClickListener;
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

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(adapterPosition);

                }
            }
        });
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onTextClickListener != null) {
                    onEditClickListener.editDonateFood(adapterPosition);
                }
            }
        });
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onTextClickListener != null) {
                    onTextClickListener.redirectToDetailPage(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, deleteIcon,editIcon;
        TextView textView,statusTextView;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            textView = itemView.findViewById(R.id.textView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            editIcon = itemView.findViewById(R.id.editIcon);
        }
    }
//    public void removeItem(int position) {
//        if (position >= 0 && position < donationList.size()) {
//             donationList.remove(position);
//              notifyDataSetChanged();
//
//        }
//    }
}
