package com.algebratech.pulse_wellness.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.AddFriendActivity;
import com.algebratech.pulse_wellness.activities.MainActivity;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private final Context mContext;
    private List<FriendsModel> products = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String userId;
    private final NewsFeedListner newsFeedListner;

    public FriendsAdapter(Context context, List<FriendsModel> products, NewsFeedListner newsFeedListner) {
        this.mContext = context;
        this.products = products;
        this.newsFeedListner = newsFeedListner;


    }

    @NonNull
    @Override
    public FriendsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        return new FriendsAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_friends, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.MyViewHolder holder, final int position) {

        final FriendsModel product = products.get(position);

        holder.userName.setText(product.getName());

        Glide.with(mContext).load(product.getProfilepic()).error(R.drawable.placeholder).into(holder.userProfile);


//        holder.friendcard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d("IDs :", product.getDataUser() + " -- " + userId);
//                StaticMethods.AlertMessage("Are you sure you want to add  " + product.getName() + " as a friend", mContext,
//                        new StaticMethods.DialogSingleButtonListener() {
//                            @Override
//                            public void onButtonClicked(DialogInterface mdialog) {
//                                dialog.show();
//                                send_request(product.getDataUser(), position);
//                            }
//                        });
//
//
//            }
//        });
        holder.addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(v, position);
                }

            }
        });

        holder.friendcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(v, position);
                }
            }
        });

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView userName;
        private final CircleImageView userProfile;
        private final RelativeLayout friendcard;
        private final ImageButton addMember;


        public MyViewHolder(View view) {
            super(view);

            userName = view.findViewById(R.id.tvFullname);
            userProfile = view.findViewById(R.id.imgView);
            friendcard = view.findViewById(R.id.friendcard);
            addMember = view.findViewById(R.id.addMember);


        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}