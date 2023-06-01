package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.algebratech.pulse_wellness.activities.FriendProfileActivity;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.models.addFriendModel;

public class addFriendAdapter extends RecyclerView.Adapter<addFriendAdapter.ViewHolder> {
    private final addFriendModel[] addFriendModels;
    Context context;
    public addFriendAdapter(addFriendModel[] addFriendModels, Context context) {
        this.addFriendModels = addFriendModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_friends, parent, false);
        addFriendAdapter.ViewHolder viewHolder = new addFriendAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final addFriendModel myListData = addFriendModels[position];
        holder.imageView.setImageResource(addFriendModels[position].getProfileImage());
        holder.textView.setText(addFriendModels[position].getUserName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Friend request sent", Toast.LENGTH_SHORT).show();
            }
        });
        holder.friendcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), FriendProfileActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addFriendModels.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public Button button;
        public CardView friendcard;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imgView);
            this.textView = itemView.findViewById(R.id.tvFullname);
            this.button = itemView.findViewById(R.id.addMember);
            this.friendcard = itemView.findViewById(R.id.friendcard);
        }
    }
}
