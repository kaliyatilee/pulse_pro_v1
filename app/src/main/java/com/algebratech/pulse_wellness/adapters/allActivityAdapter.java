package com.algebratech.pulse_wellness.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.allActivityModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class allActivityAdapter extends RecyclerView.Adapter<allActivityAdapter.ViewHolder>{

    private final allActivityModel[] allActivityModels;

    public allActivityAdapter(allActivityModel[] allActivityModels) {
        this.allActivityModels = allActivityModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.activity_data, parent, false);
        allActivityAdapter.ViewHolder viewHolder = new allActivityAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final allActivityModel myListData = allActivityModels[position];
        holder.time.setText(allActivityModels[position].getTime());
        holder.calories.setText(allActivityModels[position].getCalories());
        holder.steps.setText(allActivityModels[position].getSteps());
        holder.points.setText(allActivityModels[position].getPoints());
        holder.activtyName.setText(allActivityModels[position].getActivityName());
    }

    @Override
    public int getItemCount() {
        return allActivityModels.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView calories;
        public TextView steps;
        public TextView points;
        public LinearLayout cardView;
        public TextView activtyName;
        public ViewHolder(View itemView) {
            super(itemView);
            this.time = itemView.findViewById(R.id.time);
            this.calories = itemView.findViewById(R.id.calories);
            this.steps = itemView.findViewById(R.id.steps);
            this.points = itemView.findViewById(R.id.points);
            this.cardView = itemView.findViewById(R.id.card_layout);
            this.activtyName = itemView.findViewById(R.id.activity_name);
        }
    }


}
