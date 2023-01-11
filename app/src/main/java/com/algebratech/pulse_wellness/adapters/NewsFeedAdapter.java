package com.algebratech.pulse_wellness.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.NewsFeedModel;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.bumptech.glide.Glide;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.potyvideo.library.AndExoPlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.MyViewHolder> {

    private Context mContext;
    private NewsFeedListner newsFeedListner;
    private List<NewsFeedModel> products = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private FragmentManager FM;
    private FragmentTransaction FT;
    MyViewHolder myholder;


    public NewsFeedAdapter(Context context, List<NewsFeedModel> products, NewsFeedListner newsFeedListner) {
        this.mContext = context;
        this.products = products;
        this.newsFeedListner = newsFeedListner;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_news_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {

        int position = holder.getAdapterPosition();

        Log.e("POSITION", "" + position);

        myholder = holder;
        final NewsFeedModel product = products.get(position);
        sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        final String my_name = sharedPreferences.getString("fullname", null);

        holder.userName.setText(product.getUserName());
        holder.CommentCount.setText(product.getTot_comment_count() + " Comments");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            Date d = sdf.parse(product.getCreatedAt());

            holder.tv_time.setText(df.format("dd MMM yyyy h:mm a", d));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (product.isMyNewsFeed() == true)
            holder.delete.setVisibility(View.VISIBLE);
        if (product.getTotal_likes().isEmpty()) {
            holder.LikeCount.setText("0 Pulse");
        } else
            holder.LikeCount.setText(product.getTotal_likes() + " Pulses");

        if (!product.getLike_status().isEmpty() &&
                product.getLike_status().equals("like")) {
            holder.likeImg.setImageResource(R.drawable.ic_heart_green);
            holder.likeText.setTextColor(Color.parseColor("#7CBE31"));
        } else {
            holder.likeImg.setImageResource(R.drawable.ic_heart_grey);
            holder.likeText.setTextColor(Color.parseColor("#000000"));
        }

        if (product.getImageurl().isEmpty() ||
                product.getImageurl().equals(null) ||
                product.getImageurl() == "null" ||
                product.getImageurl().isEmpty()) {
            Log.e("IMAGE_URL", "IS _BLANK");
            try {
                holder.imageView.setVisibility(View.GONE);
                holder.youTubePlayerView.setVisibility(View.GONE);
                holder.vdimg.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.GONE);
            } catch (Exception e) {

            }
        } else {
            Log.e("IMAGE_URL", product.getType());
            Log.e("IMAGE_URL", product.getImageurl());
            if (product.getType().equals("youtube")) {
                Log.e("IMAGE_URL", "YOUTUBE");
                try {
                    holder.videoView.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.GONE);
                    holder.vdimg.setVisibility(View.VISIBLE);
                    holder.youTubePlayerView.setVisibility(View.VISIBLE);
                    holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            String videoId = product.getImageurl();
                            youTubePlayer.cueVideo(videoId, 2);

                        }
                    });
                } catch (Exception e) {

                }
            }
            if (product.getType().equals("video")) {
                Log.e("IMAGE_URL", "VIDEO");
                try {
                    holder.vdimg.setVisibility(View.VISIBLE);
                    holder.youTubePlayerView.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.GONE);
                    holder.videoView.setVisibility(View.VISIBLE);
                    holder.videoView.setSource(product.getImageurl());
                    holder.videoView.mutePlayer();
                } catch (Exception e) {

                }

            }
            if (product.getType().equals("image")) {
                try {
                    holder.videoView.setVisibility(View.GONE);
                    holder.vdimg.setVisibility(View.VISIBLE);
                    holder.youTubePlayerView.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(product.getImageurl()).placeholder(R.drawable.logo_new).into(holder.imageView);
                } catch (Exception e) {

                }
            }


        }

        if (product.getHeadline().isEmpty() || product.getHeadline().equals(null) || product.getHeadline() == "null") {
            holder.headline.setVisibility(View.GONE);

        } else {
            holder.headline.setVisibility(View.VISIBLE);
            String headline = product.getHeadline();
            holder.headline.setText(truncate(headline, 50));
        }

        if (product.getPost().isEmpty() || product.getPost().equals(null) || product.getPost() == "null") {
            holder.tv_status.setVisibility(View.GONE);

        } else {
            holder.tv_status.setVisibility(View.VISIBLE);
            String post = product.getPost();
            holder.tv_status.setText(truncate(post, 50));
        }

        Glide.with(mContext).load(product.getProfile_pic()).error(R.drawable.placeholder).into(holder.userProfile);

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(v, position);
                }
            }
        });

        holder.vdimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
                }
            }
        });

        holder.linearComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(v, position);
                }
            }
        });

        holder.relativeLayoutLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
                }

            }
        });

        holder.LikeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
                }
            }
        });

        holder.data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsFeedListner != null) {
                    newsFeedListner.onClickViewEvent(view, position);
                }
            }
        });

        holder.points.setText("Activity Points: "+product.getPoints());

//        holder.tvPrice.setText(product.getPoints());
//
//        holder.mContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(mContext, RewardsPreview.class);
        //          intent.putExtra("title",product.getTitle());
//                intent.putExtra("image",product.getImage());
//                intent.putExtra("description",product.getDescription());
//                intent.putExtra("points",product.getPoints());
//
//                mContext.startActivity(intent);
//
//            }
//        });
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, tv_time, tv_status, headline, LikeCount, likeText, CommentCount, points;
        private ImageView imageView, likeImg, delete;
        private CircleImageView userProfile;
        private LinearLayout vdimg, share, linearComments, data;
        private YouTubePlayerView youTubePlayerView;
        private AndExoPlayerView videoView;
        private RelativeLayout relativeLayoutLikes, relativeLayoutClick;
        //      private CardView mContainer;


        public MyViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.delete);
            points = view.findViewById(R.id.points);
            userName = view.findViewById(R.id.tv_name);
            CommentCount = view.findViewById(R.id.CommentCount);
            LikeCount = view.findViewById(R.id.LikeCount);
            likeText = view.findViewById(R.id.likeText);
            userProfile = view.findViewById(R.id.imgView_proPic);
            imageView = view.findViewById(R.id.imgView_postPic);
            likeImg = view.findViewById(R.id.likeImg);
            headline = view.findViewById(R.id.headline);
            tv_status = view.findViewById(R.id.tv_status);
            tv_time = view.findViewById(R.id.tv_time);
            youTubePlayerView = view.findViewById(R.id.youtube_vid);
            vdimg = view.findViewById(R.id.vidimg);
            share = view.findViewById(R.id.share);
            videoView = view.findViewById(R.id.video);
            linearComments = view.findViewById(R.id.linearComments);
            data = view.findViewById(R.id.data);
            relativeLayoutLikes = view.findViewById(R.id.relativeLayoutLikes);
            relativeLayoutClick = view.findViewById(R.id.mmmmm);

//            tvPrice = view.findViewById(R.id.tvPrice);
//            mContainer = view.findViewById(R.id.card1);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        try {
            releasePlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onViewDetachedFromWindow(holder);


    }

    public void releasePlayer() throws Exception {
        if (myholder.videoView != null) {
            myholder.videoView.stopPlayer();
            myholder.videoView.releasePlayer();
            myholder.videoView = null;
        }

        if (myholder.youTubePlayerView != null) {
            myholder.youTubePlayerView.release();
            myholder.youTubePlayerView = null;
        }


    }

    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            return str;
        }
    }
}