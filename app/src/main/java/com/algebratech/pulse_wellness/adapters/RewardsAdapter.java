package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.RewardsPreviewTest;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.models.RewardsModel;
import com.algebratech.pulse_wellness.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RewardsAdapter  extends RecyclerView.Adapter<RewardsAdapter.MyViewHolder> {

    private Context mContext;
    private List<RewardsModel> products = new ArrayList<>();
    private DBHelper db;
    private SharedPreferences sharedPreferences;


    public RewardsAdapter (Context context, List<RewardsModel> products){
        this.mContext = context;
        this.products = products;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHeadline, tvDesc,tvPrice,tvMerchant;
        private ImageView imageView;
        private CardView mContainer;
        private YouTubePlayerView youtube_vid;


        public MyViewHolder (View view){
            super(view);

            tvHeadline = view.findViewById(R.id.tvName);
            imageView = view.findViewById(R.id.image);
            tvDesc = view.findViewById(R.id.tvDesc);
            tvPrice = view.findViewById(R.id.tvPrice);
            mContainer = view.findViewById(R.id.outdoorRunning);
            youtube_vid = view.findViewById(R.id.youtube_vid);
            tvMerchant = view.findViewById(R.id.tvMerchant);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_rewards,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final RewardsModel product = products.get(position);

        holder.tvDesc.setText(product.getDescription());
        holder.tvHeadline.setText(product.getTitle());
        Glide.with(mContext).load(product.getImage()).into(holder.imageView);
        holder.tvPrice.setText(product.getPoints());
        holder.tvMerchant.setText(product.getMerchantName());

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("prod_id", product.getId());
                sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
                SQLiteDatabase.loadLibs(mContext);
                db = new DBHelper(mContext);

                String coins = db.getTotal(sharedPreferences.getString("userID",""));
                double coin = Double.parseDouble(coins);
                double prod_coins = Double.parseDouble(product.getPoints());


                if (coin >= prod_coins){

                    Intent intent = new Intent(mContext, RewardsPreviewTest.class);
                    intent.putExtra("prod_id",product.getId());
                    intent.putExtra("title",product.getTitle());
                    intent.putExtra("image",product.getImage());
                    intent.putExtra("description",product.getDescription());
                    intent.putExtra("points",product.getPoints());

                    mContext.startActivity(intent);
                }else {
                    //Toast.makeText(mContext,"You don't have enough points to redeem this product",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(mContext, RewardsPreviewTest.class);
                    intent.putExtra("prod_id",product.getId());
                    intent.putExtra("title",product.getTitle());
                    intent.putExtra("image",product.getImage());
                    intent.putExtra("description",product.getDescription());
                    intent.putExtra("points",product.getPoints());

                    mContext.startActivity(intent);
                }



            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}