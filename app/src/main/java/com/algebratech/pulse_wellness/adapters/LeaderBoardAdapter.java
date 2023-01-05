package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.models.LeaderBoardModel;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder> {
    private Context mContext;
    private List<LeaderBoardModel> leaderBoardModels = new ArrayList<>();
    private NewsFeedListner newsFeedListner;

    public LeaderBoardAdapter(Context mContext, List<LeaderBoardModel> leaderBoardModels, NewsFeedListner newsFeedListner) {
        this.mContext = mContext;
        this.leaderBoardModels = leaderBoardModels;
        this.newsFeedListner = newsFeedListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LeaderBoardAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_leader_board, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final LeaderBoardModel leaderBoardModel = leaderBoardModels.get(position);

        Glide.with(mContext).load(leaderBoardModel.getProfileurl()).error(R.drawable.placeholder).into(holder.profilePic);
        holder.userName.setText(leaderBoardModel.getFirstname() + " " + leaderBoardModel.getLastname());
        holder.points.setText(leaderBoardModel.getSteps());
        holder.count.setText(String.valueOf(leaderBoardModel.getCount()));

        holder.card.setOnClickListener(new View.OnClickListener() {
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
        return leaderBoardModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, points, count;
        private CircleImageView profilePic;
        private LinearLayout card;

        public MyViewHolder(View view) {
            super(view);
            count = view.findViewById(R.id.count);
            userName = view.findViewById(R.id.name);
            profilePic = view.findViewById(R.id.profilePic);
            points = view.findViewById(R.id.points);
            card = view.findViewById(R.id.cardd);


        }
    }
}
