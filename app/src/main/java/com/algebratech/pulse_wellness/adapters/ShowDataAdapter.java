package com.algebratech.pulse_wellness.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.ActivityListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowDataAdapter extends RecyclerView.Adapter<ShowDataAdapter.MyViewHolder> {


    private final Context mContext;
    private final ArrayList<ActivityListModel> products;

    public ShowDataAdapter(Context context, ArrayList<ActivityListModel> showact) {
        this.mContext = context;
        this.products = showact;

    }


    @NonNull
    @Override
    public ShowDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ShowDataAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_show_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowDataAdapter.MyViewHolder holder, int position) {


        holder.dateTime.setText(products.get(position).getDate());

        Double mDistanceDob = Double.parseDouble(String.valueOf(products.get(position).getDistance()));
        Double kcalsDob = Double.parseDouble(products.get(position).getKcals());

        String tempkcal = String.format("%.2f", kcalsDob);
        String tempdis = String.format("%.2f", mDistanceDob);

        holder.kal.setText(tempkcal);
        holder.step.setText(String.valueOf(products.get(position).getSteps()));
        holder.distance.setText(tempdis);
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                detailPreview(products.get(position).getAverage_pace(), products.get(position).getKcals(), products.get(position).getDistance(), products.get(position).getAverage_heartrate(), String.valueOf(products.get(position).getSteps()), products.get(position).getDate(), products.get(position).getDuration());
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateTime;
        private final TextView kal;
        private final TextView step;
        private final TextView distance;
        private final ImageView more;


        public MyViewHolder(View view) {
            super(view);

            dateTime = view.findViewById(R.id.dateTime);
            kal = view.findViewById(R.id.kal);
            step = view.findViewById(R.id.show_steps);
            more = view.findViewById(R.id.more);
            distance = view.findViewById(R.id.show_distance);


        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void detailPreview(String avaragePace, String calories, String distance, String heartRate, String steps, String timeStamp, String timeTaken) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_report_more, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView date = mView.findViewById(R.id.date);
        TextView tvdistance = mView.findViewById(R.id.tvdistance);
        TextView kcals = mView.findViewById(R.id.kcals);
        TextView tvsteps = mView.findViewById(R.id.tvsteps);
        TextView tvavaragePace = mView.findViewById(R.id.tvavaragePace);
        TextView tvavarageHeart = mView.findViewById(R.id.tvavarageHeart);
        TextView tvduration = mView.findViewById(R.id.tvduration);
        Button btn_close = mView.findViewById(R.id.btn_close);
        tvavaragePace.setText(avaragePace + " /Km");
        date.setText("" + timeStamp);


        Double mDistanceDob = Double.parseDouble(distance);
        Double kcalsDob = Double.parseDouble(calories);

        String tempkcal = String.format("%.2f", kcalsDob);
        String tempdis = String.format("%.2f", mDistanceDob);

        tvdistance.setText("" + tempdis + " Km");
        kcals.setText("" + tempkcal + " KCals");


        tvavarageHeart.setText(heartRate + " B/sec");
        tvsteps.setText("" + steps + " Steps");
        tvduration.setText(timeTaken);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}
