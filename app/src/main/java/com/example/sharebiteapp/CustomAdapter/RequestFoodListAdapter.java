package com.example.sharebiteapp.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.R;
import java.util.ArrayList;
import java.util.List;

public class RequestFoodListAdapter extends RecyclerView.Adapter<RequestFoodListAdapter.RequestFoodListViewHolder>{
    public List<DonateFood> donationList;
    private Context context;
    private OnCartClickListener onCartClickListener;
    public interface OnCartClickListener {
        void onCartClick(int position);
    }

    public RequestFoodListAdapter(Context context, List<DonateFood> donationList,OnCartClickListener onCartClickListener) {
        this.context = context;
        this.donationList = donationList != null ? donationList : new ArrayList<>();
        this.onCartClickListener = onCartClickListener;
    }

    @NonNull
    @Override
    public RequestFoodListAdapter.RequestFoodListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_food_card, parent, false);
        return new RequestFoodListAdapter.RequestFoodListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestFoodListAdapter.RequestFoodListViewHolder holder, int position) {
        DonateFood donateFood = donationList.get(position);
         holder.txtreqtitleView.setText(donateFood.getTitle());
        if(donateFood.price > 0) {
           holder.txtreqprice.setText(String.format("$%.2f", donateFood.getPrice()));
        }
        else
        {
            holder.txtreqprice.setText("free");
        }
        holder.reqcarticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onCartClickListener != null) {
                    onCartClickListener.onCartClick(adapterPosition);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class RequestFoodListViewHolder extends RecyclerView.ViewHolder {

        ImageView reqImageView, reqcarticon;
        TextView txtreqtitleView,txtreqprice;

        public RequestFoodListViewHolder(@NonNull View itemView) {
            super(itemView);
            reqImageView = itemView.findViewById(R.id.reqImageView);
            reqcarticon = itemView.findViewById(R.id.reqcarticon);
            txtreqtitleView = itemView.findViewById(R.id.txtreqtitleView);
            txtreqprice = itemView.findViewById(R.id.txtreqprice);
        }
    }
}
