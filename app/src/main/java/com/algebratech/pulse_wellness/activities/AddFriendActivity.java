package com.algebratech.pulse_wellness.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.FriendsAdapter;
import com.algebratech.pulse_wellness.adapters.NewsFeedAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.models.GraphModel;
import com.algebratech.pulse_wellness.models.NewsFeedModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendActivity extends AppCompatActivity implements NewsFeedListner {
    private Toolbar toolbarPolicy;
    RecyclerView recyclerView;
    List<FriendsModel> friendsModelList = new ArrayList<>();
    List<FriendsModel> tempModelList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private EditText etSearchKey;
    private SharedPreferences sharedPreferences;
    private String userId;
    private TextView no_data;
    private TextView search_user;
    BottomSheetDialog bottomSheetDialog;
    private int requestCountmain = 1;
    private RequestQueue requestQueue;
    int decorationCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);

        recyclerView = findViewById(R.id.recyclerView);
        etSearchKey = findViewById(R.id.etSearchKey);
        no_data = findViewById(R.id.no_data);
        search_user = findViewById(R.id.search_user);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        setSupportActionBar(toolbarPolicy);
        setTitle("Add Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        bottomSheetDialog = new BottomSheetDialog(AddFriendActivity.this, R.style.BottomSheetDialog);

        if (CM.isConnected(AddFriendActivity.this)) {
            getFriends();
        } else
            Toast.makeText(AddFriendActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


        etSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                if (s.length() <= 0) {
                    recyclerView.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                    search_user.setVisibility(View.VISIBLE);
                } else if (friendsModelList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    search_user.setVisibility(View.GONE);
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    no_data.setVisibility(View.GONE);
                    search_user.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

    }

    private void getFriends() {
        CM.showProgressLoader(AddFriendActivity.this);
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.allusers, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(Constants.TAG, String.valueOf(response));
                        try {

                            JSONArray array = new JSONArray(response.getString("data"));
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);

                                String userid = object.getString("id");
                                String imageurl = object.getString("profileurl");
                                String username = object.getString("firstname") + " " + object.getString("lastname");


                                FriendsModel friendsModel = new FriendsModel(username, imageurl, userid, "", "");
                                friendsModelList.add(friendsModel);
                                tempModelList.add(friendsModel);
                            }
                            //setAdapter();
                            CM.HideProgressLoader();
                        } catch (Exception e) {
                            CM.HideProgressLoader();
                            Log.e(Constants.TAG, e.getMessage());

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                CM.HideProgressLoader();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }

    void setAdapter() {
        mAdapter = new FriendsAdapter(AddFriendActivity.this, friendsModelList, this);
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.layer));

        if (decorationCount == 0) {
            decorationCount = 1;
            recyclerView.addItemDecoration(itemDecoration);

        }
        CM.HideProgressLoader();
    }


    private void filter(String text) {
        friendsModelList.clear();
        friendsModelList.addAll(tempModelList);
        if (text.length() > 0) {
            ArrayList<FriendsModel> newList = new ArrayList<>();
            for (FriendsModel item : friendsModelList) {
                if (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                    newList.add(item);
                }
            }
            friendsModelList.clear();
            friendsModelList.addAll(newList);

        }

//        recyclerView.removeAllViews();
//        mAdapter = new FriendsAdapter(AddFriendActivity.this, friendsModelList, this);
//        recyclerView.setAdapter(mAdapter);
        setAdapter();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClickViewEvent(View view, int position) {
        ImageButton addMember = view.findViewById(R.id.addMember);
        RelativeLayout friendcard = view.findViewById(R.id.friendcard);

        if (view == addMember) {
            CM.ShowDialogueWithCustomAction(
                    AddFriendActivity.this,
                    "Are you sure you want to add " + friendsModelList.get(position).getName() + " as a friend",
                    "Yes", "No", true,
                    new DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            CM.showProgressLoader(AddFriendActivity.this);
                            send_request(friendsModelList.get(position).getDataUser(), position);
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    }
            );

        }

        if (view == friendcard) {
            getFriendDetail(friendsModelList.get(position).getDataUser(), position);
        }

    }


    private void send_request(final String id, final int position) {
        if (CM.isConnected(AddFriendActivity.this)) {
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("friend_id", id);
                Log.e("USERID", userId);
                Log.e("FRIENDID", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.addfriend, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            CM.HideProgressLoader();
                            friendsModelList.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            mAdapter.notifyItemRangeChanged(position, friendsModelList.size());


                            AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
                            LayoutInflater factory = LayoutInflater.from(AddFriendActivity.this);
                            final View v = factory.inflate(R.layout.friendreq_success, null);
                            builder.setView(v);
                            Button button = v.findViewById(R.id.ok);
                            AlertDialog alert = builder.create();
                            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alert.dismiss();
                                    onBackPressed();
                                    finish();
                                }
                            });
                            alert.show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AddFriendActivity.this, "Friend request failed , please try again.", Toast.LENGTH_LONG).show();
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(AddFriendActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    void getFriendDetail(String id, int position) {

        if (CM.isConnected(AddFriendActivity.this)) {
            CM.showProgressLoader(AddFriendActivity.this);
            JSONObject object = new JSONObject();
            try {

                object.put("user_id", id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getUserDetails, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (response.getString("status").equals("true")) {
                                    JSONObject data = new JSONObject(response.getString("data"));
                                    Log.e("DATA", data.toString());
                                    String name = data.getString("firstname") + " " + data.getString("lastname");
                                    String profile = data.getString("profileurl");
                                    String dob = data.getString("dob");
                                    String gender = data.getString("gender");
                                    String country = data.getString("country");
                                    String created_at = data.getString("created_at");

                                    if (dob.isEmpty() || dob.equals("null") || dob.equals(""))
                                        dob = "N/A";
                                    if (gender.isEmpty() || gender.equals("null") || gender.equals(""))
                                        gender = "N/A";
                                    if (country.isEmpty() || country.equals("null") || country.equals(""))
                                        country = "N/A";
                                    if (created_at.isEmpty() || created_at.equals("null") || created_at.equals(""))
                                        created_at = "N/A";

                                    CM.HideProgressLoader();

                                    showBottomSheetDialog(position, name, profile, gender, country, dob, created_at);
                                } else {
                                    CM.HideProgressLoader();
                                }

                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());
                                CM.HideProgressLoader();
                            }

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
            Toast.makeText(AddFriendActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    private void showBottomSheetDialog(int position, String namee, String profile, String genderr, String countryy, String dobb, String created_at) {
        View view = LayoutInflater.from(AddFriendActivity.this).inflate(R.layout.user_detail_bottomsheet, null);
        bottomSheetDialog.setContentView(view);
        Button addFriend = view.findViewById(R.id.addFriend);
        ImageView close = view.findViewById(R.id.close);
        TextView gender = view.findViewById(R.id.gender);
        TextView country = view.findViewById(R.id.country);
        TextView dob = view.findViewById(R.id.dob);
        TextView name = view.findViewById(R.id.name);
        TextView memberSince = view.findViewById(R.id.memberSince);
        CircleImageView profilePic = view.findViewById(R.id.profilePic);


        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();

        name.setText(namee);
        gender.setText("Gender: " + genderr);
        country.setText("Country: " + countryy);
        dob.setText("DOB: " + StaticMethods.correctDate(dobb, "yyyy/MM/dd", "dd MMM yyyy"));
        memberSince.setText("Member since: " + StaticMethods.correctDate(created_at, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy"));

        Glide.with(this).load(profile).error(R.drawable.placeholder).into(profilePic);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.hide();
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CM.ShowDialogueWithCustomAction(
                        AddFriendActivity.this,
                        "Are you sure you want to add " + friendsModelList.get(position).getName() + " as a friend",
                        "Yes", "No", true,
                        new DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                bottomSheetDialog.hide();
                                CM.showProgressLoader(AddFriendActivity.this);
                                send_request(friendsModelList.get(position).getDataUser(), position);
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        }
                );

            }
        });

    }

}
