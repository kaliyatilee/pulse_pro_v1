package com.algebratech.pulse_wellness.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.GreetingsNotificationModel;
import com.algebratech.pulse_wellness.models.LikeModel;

public class GreetingNotificationAdapter extends RecyclerView.Adapter<GreetingNotificationAdapter.ViewHolder> {
    private final GreetingsNotificationModel[] greetingsNotificationModels;

    public GreetingNotificationAdapter(GreetingsNotificationModel[] greetingsNotificationModels) {
        this.greetingsNotificationModels = greetingsNotificationModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.greetings_notification_layout, parent, false);
        GreetingNotificationAdapter.ViewHolder viewHolder = new GreetingNotificationAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GreetingsNotificationModel myListData = greetingsNotificationModels[position];
        holder.greeting_text.setText(greetingsNotificationModels[position].getGreetingText());
        holder.greeting_icon.setImageResource(greetingsNotificationModels[position].getGreetingImage());

    }

    @Override
    public int getItemCount() {
       return greetingsNotificationModels.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView greeting_icon;
        public TextView greeting_text;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            this.greeting_icon = itemView.findViewById(R.id.greeting_icon);
            this.greeting_text = itemView.findViewById(R.id.greeting_text);
            this.card = itemView.findViewById(R.id.card);
        }
    }


}
