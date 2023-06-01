package com.algebratech.pulse_wellness.activities;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.LikeAdapter;
import com.algebratech.pulse_wellness.adapters.commentAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.models.commentModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsLikeActivity extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    private TextView like_count;
    private String feedId;
    private final List<LikeModel> likeModelList = new ArrayList<>();
    private LikeAdapter likeAdapter;
    private RecyclerView.Adapter mAdapter;
    private RequestQueue requestQueue;
    private int requestCountmain = 1;
    int likeCount;
    private String userId;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_like);
        feedId = getIntent().getStringExtra("FEEDID");
        like_count = findViewById(R.id.like_count);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        setTitle("Pulse");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        GetLikes();


    }

    private void GetLikes() {
        if (CM.isConnected(NewsLikeActivity.this)) {
            CM.showProgressLoader(NewsLikeActivity.this);
            JSONObject object = new JSONObject();
            try {
                object.put("feed_id", feedId);
                object.put("user_id", userId);
                object.put("type", "like");
                Log.e("FEEDID",feedId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getfeeddetails, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            CM.HideProgressLoader();
                            try {
                                if (response.getString("status").equals("success")) {
                                    Log.e("comment", response.toString());
                                    Log.e("PARAMS", object.toString());
                                    likeCount = response.getInt("tot_likes");
                                    JSONArray array = new JSONArray(response.getString("data"));
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String user_id = object.getString("user_id");
                                        String userName = object.getString("firstname") + " " + object.getString("lastname");
                                        String userComment = object.getString("comments");
                                        String comment_id = object.getString("comment_id");
                                        String tot_comments = object.getString("tot_comments");
                                        String profileurl = object.getString("profileurl");
                                        String created_at = object.getString("created_at");
                                        LikeModel likeModel = new LikeModel(userName, profileurl, created_at);
                                        likeModelList.add(likeModel);
                                    }
                                    setDataAdapter();


                                } else {

                                    //no comments

                                }

                            } catch (Exception e) {
                                CM.HideProgressLoader();
                                Log.e(Constants.TAG, e.getMessage());

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    Log.e("Error", error.toString());
                }
            });

            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
            requestCountmain++;
        } else
            Toast.makeText(NewsLikeActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setDataAdapter() {
        like_count.setText(likeCount + " Pulse");
        RecyclerView recyclerView = findViewById(R.id.recycle_likes);
        likeAdapter = new LikeAdapter(likeModelList, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = likeAdapter;
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.layer));

        recyclerView.addItemDecoration(itemDecoration);

    }
}