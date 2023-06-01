package com.algebratech.pulse_wellness.adapters;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.SubscriptionPlanModel;
import com.algebratech.pulse_wellness.models.commentModel;
import com.algebratech.pulse_wellness.utils.FeedCommentListner;

import java.util.ArrayList;
import java.util.List;

public class subscriptionAdapter extends RecyclerView.Adapter<subscriptionAdapter.ViewHolder> {
    private List<SubscriptionPlanModel> subscriptionPlanModels = new ArrayList<>();
    Context context;
    FeedCommentListner feedCommentListner;

    public subscriptionAdapter(List<SubscriptionPlanModel> subscriptionPlanModels, Context context, FeedCommentListner feedCommentListner) {
        this.subscriptionPlanModels = subscriptionPlanModels;
        this.context = context;
        this.feedCommentListner = feedCommentListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_subscription, parent, false);
        subscriptionAdapter.ViewHolder viewHolder = new subscriptionAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SubscriptionPlanModel subscriptionPlanModel = subscriptionPlanModels.get(position);
        SubscriptionPlanModel subscriptionPlanModel1 = subscriptionPlanModels.get(1);
      //  subscriptionPlanModel1.setSelected(true);
        holder.plan_name.setText(subscriptionPlanModels.get(position).getPlan_name());
        holder.plan_price.setText(subscriptionPlanModels.get(position).getAmount());
        if (subscriptionPlanModels.get(position).getSelected()) {
            holder.subscription_card.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
            holder.plan_name.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            holder.plan_price.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            holder.pulse.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            holder.subscription_card.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            holder.plan_name.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            holder.plan_price.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            holder.pulse.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
        holder.subscription_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedCommentListner != null) {

                    feedCommentListner.onClickViewEvent(view, position,00);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionPlanModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView plan_name;
        public TextView plan_price;
        public TextView pulse;
        public CardView subscription_card;

        public ViewHolder(View itemView) {
            super(itemView);
            this.plan_name = itemView.findViewById(R.id.plan_name);
            this.pulse = itemView.findViewById(R.id.pulse);
            this.plan_price = itemView.findViewById(R.id.plan_price);
            this.subscription_card = itemView.findViewById(R.id.subscription_card);
        }
    }


}
