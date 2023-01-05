package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class FriendsCommunityAdapter extends RecyclerView.Adapter<FriendsCommunityAdapter.MyViewHolder> {

    private Context mContext;
    private Fragment fragment;
    private List<FriendsModel> products = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String userId;
    private NewsFeedListner newsFeedListner;
    FragmentManager FM;
    FragmentTransaction FT;

    public FriendsCommunityAdapter(Fragment fragment, Context context, List<FriendsModel> products, NewsFeedListner newsFeedListner) {
        this.mContext = context;
        this.products = products;
        this.fragment = fragment;
        this.newsFeedListner = newsFeedListner;
    }

    @NonNull
    @Override
    public FriendsCommunityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        return new FriendsCommunityAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_friends_community, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull FriendsCommunityAdapter.MyViewHolder holder, final int position) {

        final FriendsModel product = products.get(position);


        holder.userName.setText(product.getName());
        if (product.getProfilepic().isEmpty() || product.getProfilepic() == null || product.getProfilepic() == "") {
            Glide.with(mContext).load(R.drawable.placeholder).into(holder.userProfile);
        } else {
            Glide.with(mContext).load(product.getProfilepic()).error(R.drawable.placeholder).into(holder.userProfile);

        }

        holder.points.setText(product.getPoint() + " pts");

        holder.friendcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(v, position);
                }

            }
        });

        holder.unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
                }
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, points;
        private CircleImageView userProfile;
        private RelativeLayout friendcard;
        private ImageView unfriend;


        public MyViewHolder(View view) {
            super(view);

            userName = view.findViewById(R.id.tvFullname);
            userProfile = view.findViewById(R.id.profilePic);
            friendcard = view.findViewById(R.id.card);
            points = view.findViewById(R.id.points);
            unfriend = view.findViewById(R.id.unfriend);

        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
