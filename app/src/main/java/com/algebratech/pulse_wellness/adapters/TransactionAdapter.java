package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.models.TransactionModel;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<TransactionModel> transactionModels;
    Context context;
    private NewsFeedListner newsFeedListner;
    public TransactionAdapter(List<TransactionModel> transactionModels, Context context,NewsFeedListner newsFeedListner) {
        this.transactionModels = transactionModels;
        this.context = context;
        this.newsFeedListner = newsFeedListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.transactioncard, parent, false);
        TransactionAdapter.ViewHolder viewHolder = new TransactionAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TransactionModel myListData = transactionModels.get(position);
        Glide.with(context).load(myListData.getImageurl()).into(holder.imageView);
        holder.name.setText(transactionModels.get(position).getReward_name());
        holder.coins.setText(transactionModels.get(position).getPrice() + " Coins");
        if (myListData.getStatus().equals("expired")) {
            holder.isExpired.setVisibility(View.VISIBLE);
            holder.isCollected.setVisibility(View.GONE);
            holder.showCode.setVisibility(View.GONE);
        }
        if(myListData.getStatus().equals("paid")){
            holder.isCollected.setVisibility(View.VISIBLE);
            holder.showCode.setVisibility(View.GONE);
            holder.isExpired.setVisibility(View.GONE);
        }
        if(myListData.getStatus().equals("pending")){
            holder.isCollected.setVisibility(View.GONE);
            holder.isExpired.setVisibility(View.GONE);
            holder.showCode.setVisibility(View.VISIBLE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            Date d = sdf.parse(transactionModels.get(position).getCreated_at());

            holder.date.setText(df.format("dd MMM yyyy", d));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.showCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name;
        public TextView isExpired;
        public TextView isCollected;
        public TextView coins;
        public TextView date;
        public CardView cardView;
        public Button showCode;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.image);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.coins = (TextView) itemView.findViewById(R.id.coins);
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.isExpired = (TextView) itemView.findViewById(R.id.isExpired);
            this.isCollected = (TextView) itemView.findViewById(R.id.isCollected);
            this.cardView = (CardView) itemView.findViewById(R.id.card_layout);
            this.showCode = (Button) itemView.findViewById(R.id.showCode);
        }
    }

}
