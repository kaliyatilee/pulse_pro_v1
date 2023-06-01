package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.FriendRequestAdapter;
import com.algebratech.pulse_wellness.adapters.FriendsCommunityAdapter;
import com.algebratech.pulse_wellness.adapters.GreetingNotificationAdapter;
import com.algebratech.pulse_wellness.adapters.LikeAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.fragments.CommunityFragment;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.models.GreetingsNotificationModel;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements NewsFeedListner {
    private Toolbar toolbarPolicy;
    private TextView friendreqcount;
    private TextView no_req;
    private Context mContext;
    private RecyclerView.Adapter friendReqAdapter;
    private final List<FriendsModel> friendsReqModelList = new ArrayList<>();
    private RecyclerView friendrequestsRecycle;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private String userId;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        intent = getIntent();
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        no_req = findViewById(R.id.no_req);
        friendrequestsRecycle = findViewById(R.id.recyclerviewreqs);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        setSupportActionBar(toolbarPolicy);
        setTitle("Notifications");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getFriendsRequest();


        GreetingsNotificationModel[] greetingsNotificationModels = new GreetingsNotificationModel[]
                {
                        new GreetingsNotificationModel("Congrats! Your complete 4000 steps Today", R.drawable.celebration),
                        new GreetingsNotificationModel("Congrats! Your complete 3000 steps Today", R.drawable.celebration),
                        new GreetingsNotificationModel("Congrats! Your complete 2000 steps Today", R.drawable.celebration),
                        new GreetingsNotificationModel("Congrats! Your complete 1000 steps Today", R.drawable.celebration),
                };

        RecyclerView recyclerView = findViewById(R.id.recyclerviewGreetings);
        GreetingNotificationAdapter adapter = new GreetingNotificationAdapter(greetingsNotificationModels);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.layer));

        recyclerView.addItemDecoration(itemDecoration);


    }

    private void getFriendsRequest() {

        if (CM.isConnected(NotificationActivity.this)) {
            CM.showProgressLoader(NotificationActivity.this);
            friendsReqModelList.clear();
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.friendreq, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                Log.e("FRIENDREQ", response.toString());
                                JSONArray array = new JSONArray(response.getString("data"));
                                if (!array.isNull(0)) {


                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String userid = object.getString("id");
                                        String imageurl = object.getString("profileurl");
                                        String timeAgo = object.getString("created_at");
                                        String username = object.getString("firstname") + " " + object.getString("lastname");


                                        FriendsModel friendsModel = new FriendsModel(username, imageurl, userid, "", timeAgo);
                                        friendsReqModelList.add(friendsModel);
                                    }


                                }


                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());
                                CM.HideProgressLoader();
                            }

                            if (friendsReqModelList.size() == 0) {
                                no_req.setVisibility(View.VISIBLE);
                                CM.HideProgressLoader();
                            } else
                                setAdapter();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    CM.HideProgressLoader();
                }
            });

            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(NotificationActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setAdapter() {
        no_req.setVisibility(View.GONE);
        friendReqAdapter = new FriendRequestAdapter(getApplicationContext(), friendsReqModelList, this);
        friendrequestsRecycle.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        friendrequestsRecycle.setAdapter(friendReqAdapter);
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.layer));

        friendrequestsRecycle.addItemDecoration(itemDecoration);
        CM.HideProgressLoader();
    }

    @Override
    public void onClickViewEvent(View view, int position) {
        ImageButton accept = view.findViewById(R.id.accept);
        ImageButton reject = view.findViewById(R.id.reject);

        if (view == accept) {
            if (CM.isConnected(NotificationActivity.this)) {
                acceptRequest(friendsReqModelList.get(position).getDataUser(), position);
            } else
                Toast.makeText(NotificationActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

        } else if (view == reject) {
            if (CM.isConnected(NotificationActivity.this)) {
                rejectRequest(friendsReqModelList.get(position).getDataUser(), position);
            } else
                Toast.makeText(NotificationActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

        }
    }

    private void acceptRequest(String dataUser, int position) {
        CM.showProgressLoader(NotificationActivity.this);
        JSONObject object = new JSONObject();
        try {
            object.put("id", dataUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.friendaccept, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(NotificationActivity.this, "Friend request accepted", Toast.LENGTH_LONG).show();
                        friendsReqModelList.remove(position);
                        friendReqAdapter.notifyDataSetChanged();
                        CM.HideProgressLoader();
                        getFriendsRequest();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NotificationActivity.this, "Failed , please try again.", Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void rejectRequest(String dataUser, int position) {
        CM.ShowDialogueWithCustomAction(
                NotificationActivity.this,
                "Are you sure you want to add remove this friend request?",
                "Yes", "No", true,
                new DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        CM.showProgressLoader(NotificationActivity.this);
                        JSONObject object = new JSONObject();
                        try {
                            object.put("id", dataUser);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.friendreject, object,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        CM.HideProgressLoader();
                                        Toast.makeText(NotificationActivity.this, "Friend request removed", Toast.LENGTH_LONG).show();
                                        friendsReqModelList.remove(position);
                                        friendReqAdapter.notifyDataSetChanged();
                                        getFriendsRequest();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(NotificationActivity.this, "Failed , please try again.", Toast.LENGTH_LONG).show();
                                    }
                                });
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(jsonObjectRequest);
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }
        );


    }

    @Override
    public void onBackPressed() {
        String fcm = intent.getStringExtra("fcm");
        if (fcm.equals("fcm")) {
            Intent i = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else
            super.onBackPressed();
    }
}

