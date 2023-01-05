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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.HumanDateUtils;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {

    private Context mContext;
    private NewsFeedListner newsFeedListner;
    private List<FriendsModel> products = new ArrayList<>();

    public FriendRequestAdapter(Context context, List<FriendsModel> products, NewsFeedListner newsFeedListner) {
        this.mContext = context;
        this.products = products;
        this.newsFeedListner = newsFeedListner;
    }


    @NonNull
    @Override
    public FriendRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new FriendRequestAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_friend_requests, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.MyViewHolder holder, int position) {

        final FriendsModel product = products.get(position);

        holder.userName.setText(product.getName());
        if (product.getProfilepic().isEmpty() || product.getProfilepic() == null || product.getProfilepic() == "") {
            Glide.with(mContext).load(R.drawable.placeholder).into(holder.userProfile);
        } else {
            Glide.with(mContext).load(product.getProfilepic()).error(R.drawable.placeholder).into(holder.userProfile);

        }
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(v, position);
                }

            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(v, position);
                }
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            Date d = sdf.parse(product.getTimeAgo());

            holder.timeAgo.setText(df.format("dd MMM h:mm a", d));

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, timeAgo;
        private CircleImageView userProfile;
        private ImageButton accept, reject;

        public MyViewHolder(View view) {
            super(view);

            userName = view.findViewById(R.id.tvFullname);
            userProfile = view.findViewById(R.id.profilePic);
            accept = view.findViewById(R.id.accept);
            reject = view.findViewById(R.id.reject);
            timeAgo = view.findViewById(R.id.timeAgo);

        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}



