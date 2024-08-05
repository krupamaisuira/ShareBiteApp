package com.example.sharebiteapp.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.example.sharebiteapp.DonateFoodActivity;
import com.example.sharebiteapp.ModelData.CustomPrediction;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;


public class AutocompletePredictionAdapter extends RecyclerView.Adapter<AutocompletePredictionAdapter.ViewHolder> {
    private List<CustomPrediction> predictions;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CustomPrediction prediction);
    }

    public AutocompletePredictionAdapter(List<CustomPrediction> predictions, Context context, OnItemClickListener onItemClickListener) {
        this.predictions = predictions;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomPrediction prediction = predictions.get(position);
        holder.textView.setText(prediction.getFullAddress() != null ? prediction.getFullAddress() : prediction.getPrimaryText());
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(prediction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
