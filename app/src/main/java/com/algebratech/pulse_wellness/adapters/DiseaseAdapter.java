package com.algebratech.pulse_wellness.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.DiseaseModel;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;

import java.util.ArrayList;
import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ViewHolder> {
    private List<DiseaseModel> diseaseModels = new ArrayList<>();
    private NewsFeedListner newsFeedListner;

    public DiseaseAdapter(List<DiseaseModel> diseaseModels, NewsFeedListner newsFeedListner) {
        this.diseaseModels = diseaseModels;
        this.newsFeedListner = newsFeedListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_disease, parent, false);
        DiseaseAdapter.ViewHolder viewHolder = new DiseaseAdapter.ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DiseaseModel myListData = diseaseModels.get(position);
        holder.disease_name.setText(myListData.getName());

        if (diseaseModels.get(position).getSelected()) {
            holder.disease_name.setBackgroundResource(R.drawable.round_border_green);
            holder.disease_name.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.disease_name.setBackgroundResource(R.drawable.round_border_green_border);
            holder.disease_name.setTextColor(Color.parseColor("#000000"));
        }

        holder.disease_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
//                    diseaseModels.get(position).setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView disease_name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.disease_name = itemView.findViewById(R.id.disease);
        }
    }
}
