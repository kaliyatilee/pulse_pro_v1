package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.FriendProfileActivity;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.models.commentModel;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {
    private List<LikeModel> listdata = new ArrayList<>();

    Context context;

    public LikeAdapter(List<LikeModel> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.like_count_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final LikeModel myListData = listdata.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            android.text.format.DateFormat df = new android.text.format.DateFormat();
        try {
            Date d = sdf.parse(listdata.get(position).getLikeTime());
            holder.likeTime.setText(DateFormat.format("dd MMM h:mm a", d));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.username.setText(listdata.get(position).getUserName());
        if (myListData.getUserImage().isEmpty()) {
            holder.userProfile.setImageResource(R.drawable.placeholder);
        } else
            Glide.with(context).load(listdata.get(position).getUserImage()).into(holder.userProfile);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView imageView;
//        public TextView textView;
//        public Button button;
//        public CardView friendcard;
//        public ViewHolder(View itemView) {
//            super(itemView);
//            this.imageView = (ImageView) itemView.findViewById(R.id.imgView);
//            this.textView = (TextView) itemView.findViewById(R.id.tvFullname);
//            this.button = (Button) itemView.findViewById(R.id.addMember);
//            this.friendcard = (CardView) itemView.findViewById(R.id.friendcard);
//        }

        public ImageView userProfile;
        public TextView username;
        public TextView likeTime;

        public ViewHolder(View itemView) {
            super(itemView);
            this.userProfile = itemView.findViewById(R.id.userProfile);
            this.username = itemView.findViewById(R.id.userName);
            this.likeTime = itemView.findViewById(R.id.likeTime);

        }
    }


}
