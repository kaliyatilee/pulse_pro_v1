package com.algebratech.pulse_wellness.adapters;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.commentModel;
import com.algebratech.pulse_wellness.utils.FeedCommentListner;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.ViewHolder> {
    private List<commentModel> commentModel = new ArrayList<>();
    Context context;
    private FeedCommentListner feedCommentListner;
    private RecyclerView.Adapter mAdapter;
    private boolean isSubArray;
    int parentPosition;

    public commentAdapter(List<commentModel> commentModel,
                          Context context,
                          FeedCommentListner feedCommentListner,
                          Boolean isSubArray,
                          int parentId) {
        this.commentModel = commentModel;
        this.context = context;
        this.feedCommentListner = feedCommentListner;
        this.isSubArray = isSubArray;
        this.parentPosition = parentId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_commets, parent, false);
        commentAdapter.ViewHolder viewHolder = new commentAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final commentModel myListData = commentModel.get(position);
        //    final commentModel mySubListData = commentModel.get(position).getComments_arr().get();
        holder.userName.setText(commentModel.get(position).getUsername());
        holder.userComment.setText(commentModel.get(position).getComments());

        holder.userCommentTime.setText(covertTimeToText(commentModel.get(position).getCreated_at()));

        if (isSubArray) {

            if (commentModel.get(position).getTot_likes().equals("0")) {
                holder.likenum.setVisibility(View.GONE);
            } else {
                holder.likenum.setVisibility(View.VISIBLE);
                holder.likenum.setText(commentModel.get(position).getTot_likes() + " Pulse");
            }
        } else {
            if (commentModel.get(position).getTot_likes().equals("0")) {
                holder.likenum.setVisibility(View.GONE);
            } else {
                holder.likenum.setVisibility(View.VISIBLE);
                holder.likenum.setText(commentModel.get(position).getTot_likes() + " Pulse");
            }

        }


        if (isSubArray) {
            holder.userProfile.requestLayout();
            holder.userProfile.getLayoutParams().height = (int) pxFromDp(context, 30);
            holder.userProfile.getLayoutParams().width = (int) pxFromDp(context, 30);
            if (myListData.getUserImage().isEmpty()) {
                holder.userProfile.setImageResource(R.drawable.placeholder);
            } else
                Glide.with(context).load(myListData.getUserImage()).into(holder.userProfile);

            Log.e("SUBARRA_LENGTH",""+commentModel.get(position).getComments());


            if (commentModel.get(position).getLike_status().equalsIgnoreCase("Yes") ||
                    commentModel.get(position).getLike_status().equalsIgnoreCase("like")) {
                holder.like_button.setImageResource(R.drawable.ic_heart_green);
            } else {
                holder.like_button.setImageResource(R.drawable.ic_heart);

            }
        } else {
            if (myListData.getUserImage().isEmpty()) {
                holder.userProfile.setImageResource(R.drawable.placeholder);
            } else
                Glide.with(context).load(myListData.getUserImage()).into(holder.userProfile);

            if (commentModel.get(position).getLike_status().equals("Yes") ||
                commentModel.get(position).getLike_status().equalsIgnoreCase("like")) {
                holder.like_button.setImageResource(R.drawable.ic_heart_green);
            } else {
                holder.like_button.setImageResource(R.drawable.ic_heart);
            }


        }
        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSubArray) {
                    feedCommentListner.onClickViewEvent(view, parentPosition, position);
                } else {
                    if (feedCommentListner != null) {
                        feedCommentListner.onClickViewEvent(view, position, -1);
                    }
                }
            }
        });

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CLICK_POS",""+position);
                Log.e("CLICK_IS SUB_ARRAY",""+isSubArray);
                if (isSubArray) {
                    feedCommentListner.onClickViewEvent(view, parentPosition, position);
                } else {
                    if (feedCommentListner != null) {
                        feedCommentListner.onClickViewEvent(view, position, -1);
                    }
                }

            }
        });

        if (commentModel.get(position).getComments_arr() != null && !commentModel.get(position).getComments_arr().isEmpty()) {

            holder.nestedComment.setVisibility(View.VISIBLE);
            commentAdapter commentAdapter = new commentAdapter(commentModel.get(position).getComments_arr(), getContext(), feedCommentListner, true, position);
            holder.nestedComment.setHasFixedSize(true);
            holder.nestedComment.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter = commentAdapter;
            holder.nestedComment.setAdapter(mAdapter);

        } else
            holder.nestedComment.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return commentModel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userProfile;
        public ImageView like_button;
        public TextView userName;
        public TextView userComment;
        public TextView userCommentTime;
        public TextView reply;
        public TextView likenum;
        public RecyclerView nestedComment;

        public ViewHolder(View itemView) {
            super(itemView);
            this.userProfile = (ImageView) itemView.findViewById(R.id.userProfile);
            this.like_button = (ImageView) itemView.findViewById(R.id.like_button);
            this.userName = (TextView) itemView.findViewById(R.id.userName);
            this.reply = (TextView) itemView.findViewById(R.id.reply);
            this.userComment = (TextView) itemView.findViewById(R.id.userComment);
            this.userCommentTime = (TextView) itemView.findViewById(R.id.userCommentTime);
            this.likenum = (TextView) itemView.findViewById(R.id.likenum);
            this.nestedComment = itemView.findViewById(R.id.nestedComment);
        }
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


    public String covertTimeToText(String dataDate) {
        Log.e("time", dataDate);
        String convTime = null;

        String prefix = "";
        String suffix = "";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + "s" + suffix;
            } else if (minute < 60) {
                convTime = minute + "m" + suffix;
            } else if (hour < 24) {
                convTime = hour + "h" + suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + "Y" + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + "M" + suffix;
                } else {
                    convTime = (day / 7) + "w" + suffix;
                }
            } else if (day < 7) {
                convTime = day + "d" + suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }

}
