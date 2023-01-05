package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.DailyReads;

import java.util.List;

public class ShowDataDailyAdapter extends RecyclerView.Adapter<ShowDataDailyAdapter.MyViewHolder> {


    private Context mContext;
    private List<DailyReads> products;
    private int user_id;

    public ShowDataDailyAdapter(Context context, List<DailyReads> showact, int user_id) {
        this.mContext = context;
        this.products = showact;
        this.user_id = user_id;

    }


    @NonNull
    @Override
    public ShowDataDailyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ShowDataDailyAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_show_data_daily, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowDataDailyAdapter.MyViewHolder holder, int position) {

        Log.d("TryProd", products.get(position).getSteps());
//        if (user_id == products.get(position).getId()){
//            Toast.makeText(mContext,"Ndini Munhu wacho",Toast.LENGTH_LONG).show();
//        }


        Double mDistanceDob = Double.parseDouble(String.valueOf(products.get(position).getDistance()));
        Double kcalsDob = Double.parseDouble(products.get(position).getKcals());

        String tempkcal = String.format("%.2f", kcalsDob);
        String tempdis = String.format("%.2f", mDistanceDob);

        holder.dateTime.setText(products.get(position).getDate());
        holder.kal.setText(tempkcal);
        holder.step.setText(String.valueOf(products.get(position).getSteps()));
        holder.distance.setText(tempdis);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTime, kal, step, distance;


        public MyViewHolder(View view) {
            super(view);

            dateTime = view.findViewById(R.id.dateTime);
            kal = view.findViewById(R.id.kal);
            step = view.findViewById(R.id.show_steps);
            distance = view.findViewById(R.id.show_distance);


        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


}
