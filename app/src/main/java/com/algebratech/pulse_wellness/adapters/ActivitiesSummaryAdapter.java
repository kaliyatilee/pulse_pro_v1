package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.ActivitiesSummaryModel;
import com.algebratech.pulse_wellness.models.NewsFeedModel;
import com.algebratech.pulse_wellness.models.TodaysActivityModel;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesSummaryAdapter extends RecyclerView.Adapter<ActivitiesSummaryAdapter.MyViewHolder> {
    private Context mContext;
    private List<TodaysActivityModel> activitiesSummaryModels = new ArrayList<>();

    public ActivitiesSummaryAdapter(Context mContext, List<TodaysActivityModel> activitiesSummaryModels) {
        this.mContext = mContext;
        this.activitiesSummaryModels = activitiesSummaryModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActivitiesSummaryAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_summary_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final TodaysActivityModel summaryModel = activitiesSummaryModels.get(position);

        // TODO we have to change icons here according to activity name
        holder.activityImage.setImageResource(R.drawable.runningg);
        holder.activityTitle.setText(summaryModel.getActivity());
        holder.activityTime.setText(summaryModel.getTime_taken());
        holder.activityData.setText(summaryModel.getSteps() + " Steps");
    }

    @Override
    public int getItemCount() {
        return activitiesSummaryModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView activityTitle, activityTime, activityData;
        private ImageView activityImage;

        public MyViewHolder(View view) {
            super(view);
            activityImage = view.findViewById(R.id.activityImage);
            activityTitle = view.findViewById(R.id.activityTitle);
            activityTime = view.findViewById(R.id.activityTime);
            activityData = view.findViewById(R.id.activityData);
        }
    }

}
