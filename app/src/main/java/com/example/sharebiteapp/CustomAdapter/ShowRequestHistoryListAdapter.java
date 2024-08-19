package com.example.sharebiteapp.CustomAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.R;
import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class ShowRequestHistoryListAdapter extends RecyclerView.Adapter<ShowRequestHistoryListAdapter.ShowRequestHistoryViewHolder>{
    public List<DonateFood> donationList;
    private Context context;
    private OnCartClickListener onCartClickListener;
    private OnTextClickListener onTextClickListener;

    public interface OnCartClickListener {
        void onCartClick(int position);
    }
    public interface OnTextClickListener {
        void redirectToDetailPage(int position);
    }
    public ShowRequestHistoryListAdapter(Context context, List<DonateFood> donationList, OnCartClickListener onCartClickListener,OnTextClickListener onTextClickListener) {
        this.context = context;
        this.donationList = donationList != null ? donationList : new ArrayList<>();
        this.onCartClickListener = onCartClickListener;
        this.onTextClickListener = onTextClickListener;
    }

    @NonNull
    @Override
    public ShowRequestHistoryListAdapter.ShowRequestHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_collections_card, parent, false);
        return new ShowRequestHistoryListAdapter.ShowRequestHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowRequestHistoryListAdapter.ShowRequestHistoryViewHolder holder, int position) {
        DonateFood donateFood = donationList.get(position);
        holder.txtreqtitleView.setText(donateFood.getTitle());


        if(Utils.isFoodExpired(donateFood.bestBefore) == 0)
        {
            donateFood.setStatus(FoodStatus.Expired.getIndex());
        } else if (donateFood.getStatus() == FoodStatus.Available.getIndex()) {
            donateFood.setStatus(FoodStatus.Cancelled.getIndex());
        }
        holder.collstatusTextView.setText(FoodStatus.getByIndex(donateFood.getStatus()).toString());
        FoodStatus status = FoodStatus.fromString(holder.collstatusTextView.getText().toString());
        Utils.setStatusColor(context,status, holder.collstatusTextView);


        holder.reqcarticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onCartClickListener != null) {
                    onCartClickListener.onCartClick(adapterPosition);

                }
            }
        });
        holder.infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onTextClickListener != null) {
                    onTextClickListener.redirectToDetailPage(adapterPosition);
                }
            }
        });
        if(donateFood.uploadedImageUris != null) {

            Uri selectedImage =  Uri.parse(donateFood.uploadedImageUris.get(0).toString());
            Glide.with(holder.reqImageView.getContext())
                    .load(selectedImage.toString())
                    .error(android.R.drawable.ic_menu_gallery) // Error image
                    .into( holder.reqImageView);


        }

    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class ShowRequestHistoryViewHolder extends RecyclerView.ViewHolder {

        ImageView reqImageView, reqcarticon,infoIcon;
        TextView txtreqtitleView,collstatusTextView;

        public ShowRequestHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            reqImageView = itemView.findViewById(R.id.collimageView);
            reqcarticon = itemView.findViewById(R.id.trolleyIcon);
            txtreqtitleView = itemView.findViewById(R.id.colltextView);
            infoIcon = itemView.findViewById(R.id.infoIcon);
            collstatusTextView = itemView.findViewById(R.id.collstatusTextView);
        }
    }
}
