package com.algebratech.pulse_wellness.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.Comment_activity;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.NewsFeedAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.NewsFeedModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class MyNewsFeedActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener, NewsFeedListner {
    private Toolbar toolbarPolicy;

    RecyclerView recyclerView;
    List<NewsFeedModel> news = new ArrayList<>();
    NewsFeedListner newsFeedListner;
    private RecyclerView.Adapter mAdapter;
    private int requestCountmain = 1;
    private SharedPreferences sharedPreferences;
    private String userId;
    private String my_name;
    private FragmentManager FM;
    private FragmentTransaction FT;
    private RequestQueue requestQueue;
    private NewsFeedAdapter newsFeedAdapter;
    ImageView likeImg, delete;
    TextView likeText, no_newsfeed;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_news_feed);

        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        setTitle("My News Feed");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        my_name = sharedPreferences.getString("fullname", null);
        recyclerView = findViewById(R.id.recyclerView);
        no_newsfeed = findViewById(R.id.no_newsfeed);
        recyclerView.setOnScrollChangeListener(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 1000;
            }
        };

        recyclerView.setLayoutManager(linearLayoutManager);

        try {
            getNewsFeeds(requestCountmain);
        } catch (Exception e) {

        }

    }


    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        if (isLastItemDisplaying(recyclerView)) {
            try {
                // getNewsFeeds(requestCountmain);
            } catch (Exception e) {

            }

        }
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void getNewsFeeds(int requestCount) {
        if (CM.isConnected(MyNewsFeedActivity.this)) {
            CM.showProgressLoader(MyNewsFeedActivity.this);
            JSONObject object = new JSONObject();
            try {

                object.put("page", requestCount);
                object.put("user_id", userId);
                Log.e("OBJECT", object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.myfeeds, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, String.valueOf(response));

                            news = new ArrayList<>();

                            try {

                                if (response.getString("status").equals("true")) {


                                    JSONArray array = new JSONArray(response.getString("data"));
                                    Log.e("datta", array.toString());

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String category = object.getString("category");
                                        String headline = object.getString("headline");
                                        String post = object.getString("post");
                                        String imageurl = object.getString("imageurl");
                                        String postedby = object.getString("postedby");
                                        String partnerid = object.getString("partnerid");
                                        String created_at = object.getString("created_at");
                                        String username = object.getString("firstname") + " " + object.getString("lastname");
                                        String profliepic = object.getString("profileurl");
                                        String type = object.getString("type");
                                        String total_likes = object.optString("tot_likes");
                                        String like_status = object.optString("like_status");
                                        String tot_comment_count = object.optString("tot_comment_count");
                                        String points = object.optString("points");
                                        NewsFeedModel newsfeed = new NewsFeedModel(id, category, headline, post, imageurl, postedby, partnerid, created_at, username, profliepic, type, total_likes, like_status, true, tot_comment_count,points);
                                        news.add(newsfeed);


                                    }


                                } else {
                                    CM.HideProgressLoader();
                                }

                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());
                                CM.HideProgressLoader();
                            }

                            if (news.size() == 0) {
                                no_newsfeed.setVisibility(View.VISIBLE);
                                CM.HideProgressLoader();
                            } else
                                setdataAdapter();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                }
            });

            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
            requestCountmain++;
        } else
            Toast.makeText(MyNewsFeedActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setdataAdapter() {
        newsFeedAdapter = new NewsFeedAdapter(this, news, this);
        mAdapter = newsFeedAdapter;
        recyclerView.setVisibility(View.VISIBLE);
        no_newsfeed.setVisibility(View.GONE);
        recyclerView.setAdapter(mAdapter);
        CM.HideProgressLoader();
    }


    @Override
    public void onClickViewEvent(View view, int position) {
        RelativeLayout relativeLayoutLikes = view.findViewById(R.id.relativeLayoutLikes);
        RelativeLayout relativeLayoutClick = view.findViewById(R.id.mmmmm);
        LinearLayout linearComments = view.findViewById(R.id.linearComments);
        LinearLayout share = view.findViewById(R.id.share);
        TextView likeCount = view.findViewById(R.id.LikeCount);
        likeImg = view.findViewById(R.id.likeImg);
        delete = view.findViewById(R.id.delete);
        likeText = view.findViewById(R.id.likeText);

        if (view == relativeLayoutLikes) {
            if (!news.get(position).getLike_status().isEmpty() && news.get(position).getLike_status().equals("like")) {
                Like(news.get(position).getId(), "unlike", likeImg, position);
            } else {
                Like(news.get(position).getId(), "like", likeImg, position);
            }
        }
        if (view == relativeLayoutClick) {
            OpenFeed(news.get(position));
        }
        if (view == linearComments) {
            Intent intent = new Intent(this, Comment_activity.class);
            intent.putExtra("FEEDID", news.get(position).getId());
            startActivityForResult(intent, 100);
        }
        if (view == share) {
            share(news.get(position));
        }
        if (view == likeCount) {
            Intent intent = new Intent(this, NewsLikeActivity.class);
            intent.putExtra("FEEDID", news.get(position).getId());
            startActivity(intent);
        }
        if (view == delete) {
            CM.ShowDialogueWithCustomAction(
                    this,
                    "Are you sure, you want to delete this Feed?",
                    "Yes", "No", true,
                    new DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            delete(userId, news.get(position).getId());
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    }
            );
        }
    }

    private void Like(String feedId, String status, ImageView imageView, int position) {

        if (CM.isConnected(MyNewsFeedActivity.this)) {
            JSONObject object = new JSONObject();
            try {
                //input your API parameters
                object.put("user_id", userId);
                object.put("type", status);
                object.put("feed_id", feedId);
                object.put("comments", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.add_feed_activities, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, String.valueOf(response));

                            try {
                                if (response.getString("status").equals("true")) {
                                    if (status.equals("unlike")) {
                                        news.get(position).setLike_status("unlike");
                                        int likeCount = 0;
                                        if (news.get(position).getTotal_likes() != null &&
                                                !news.get(position).getTotal_likes().isEmpty()) {
                                            likeCount = Integer.parseInt(news.get(position).getTotal_likes());
                                        }
                                        if (likeCount > 0) {
                                            likeCount = likeCount - 1;
                                        }
                                        news.get(position).setTotal_likes("" + likeCount);
                                    } else {
                                        news.get(position).setLike_status("like");
                                        int likeCount = 0;
                                        if (news.get(position).getTotal_likes() != null &&
                                                !news.get(position).getTotal_likes().isEmpty()) {
                                            likeCount = Integer.parseInt(news.get(position).getTotal_likes());
                                        }
                                        likeCount = likeCount + 1;
                                        news.get(position).setTotal_likes("" + likeCount);
                                    }
                                    mAdapter.notifyItemChanged(position, news.get(position));

                                } else {

                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.toString());
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(MyNewsFeedActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    private void OpenFeed(NewsFeedModel position) {
        startActivity(new Intent(this, NewsFeedDetailActivity.class)
                .putExtra("headline", position.getHeadline())
                .putExtra("post", position.getPost())
                .putExtra("type", position.getType())
                .putExtra("data", position.getImageurl())
                .putExtra("profile", position.getProfile_pic())
                .putExtra("username", position.getUserName())
        );
    }

    private void share(NewsFeedModel position) {
        Intent shareIntent = new Intent();
        String headline = position.getHeadline();
        String post = position.getPost();
        if (position.getType().equals("video")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "Posted a feed on Pulse News Feed."
                    + "\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            shareIntent.setType("*/*");
            startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

        }
        if (position.getType().equals("image")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "\n"
                    + truncate(post, 50)
                    + "\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            shareIntent.setType("*/*");


            try {
                URL url = new URL(position.getImageurl());
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(image));
                startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        if (position.getType().equals("text")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "\n"
                    + truncate(post, 50)
                    + "'\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

        }
        if (position.getType().equals("youtube")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "Posted a feed on Pulse News Feed."
                    + "\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            shareIntent.setType("*/*");
            startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

        }
    }

    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            return str;
        }
    }

    private void delete(String user_id, String feed_id) {

        if (CM.isConnected(MyNewsFeedActivity.this)) {

            JSONObject object = new JSONObject();
            try {
                object.put("user_id", user_id);
                object.put("feed_id", feed_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CM.showProgressLoader(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.deleteFeed, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, String.valueOf(response));
                            CM.HideProgressLoader();
                            try {

                                if (response.getString("status").equals("true")) {

                                    try {
                                        requestCountmain = 1;
                                        getNewsFeeds(requestCountmain);
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {

                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.toString());
                    CM.HideProgressLoader();
                }
            });
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(MyNewsFeedActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            getNewsFeeds(1);
        }

    }
}