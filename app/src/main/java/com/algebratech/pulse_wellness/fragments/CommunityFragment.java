package com.algebratech.pulse_wellness.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.AddFriendActivity;
import com.algebratech.pulse_wellness.activities.FriendDetailScreen;
import com.algebratech.pulse_wellness.activities.MainActivity;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.adapters.FriendsAdapter;
import com.algebratech.pulse_wellness.adapters.FriendsCommunityAdapter;
import com.algebratech.pulse_wellness.adapters.RewardsAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.models.RewardsModel;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CommunityFragment extends Fragment implements NewsFeedListner {

    View root;
    private FloatingActionButton addfriend;
    private RecyclerView friendRecycle;
    private TextView no_friends;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;
    private String userId;
    private Context mContext;
    private RecyclerView.Adapter friendsAdapter;
    private final List<FriendsModel> friendsModelList = new ArrayList<>();
    List<FriendsModel> tempModelList = new ArrayList<>();
    private RequestQueue requestQueue;
    EditText search_friend;
    List<FriendsModel> temp = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_community, container, false);

        mContext = getContext();
        sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        userId = sharedPreferences.getString("userID", null);

        search_friend = root.findViewById(R.id.search_friend);
        friendRecycle = root.findViewById(R.id.recyclerView);
        no_friends = root.findViewById(R.id.no_friends);
        addfriend = root.findViewById(R.id.fab);
        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddFriendActivity.class));
            }
        });

        friendRecycle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));


        try {
            if (getActivity()!=null)
                getFriends();


        } catch (Exception e) {

        }


        search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchFriend(friendsModelList, charSequence.toString());
                if (temp.size() == 0) {
                    friendRecycle.setVisibility(View.GONE);
                    no_friends.setVisibility(View.VISIBLE);
                } else {
                    friendRecycle.setVisibility(View.VISIBLE);
                    no_friends.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return root;
    }

    private void getFriends() {

        if (CM.isConnected(getActivity())) {

            CM.showProgressLoader(getActivity());
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.myfriends, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            friendsModelList.clear();
                            try {

                                JSONArray array = new JSONArray(response.getString("data"));

                                if (!array.isNull(0)) {

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String userid = object.getString("id");
                                        String imageurl = object.getString("profileurl");
                                        String username = object.getString("firstname") + " " + object.getString("lastname");
                                        String points = object.optString("loyaltpoints");

                                        FriendsModel friendsModel = new FriendsModel(username, imageurl, userid, points, "");

                                        if (!userid.equals(userId)) {

                                            friendsModelList.add(friendsModel);
                                            tempModelList.add(friendsModel);
                                        }

                                    }


                                } else {
                                    CM.HideProgressLoader();
                                }


                            } catch (Exception e) {
                                CM.HideProgressLoader();
                                Log.e(Constants.TAG, e.getMessage());

                            }
                            if (friendsModelList.size() == 0) {
                                no_friends.setVisibility(View.VISIBLE);
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

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(jsonObjectRequest);

        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            requestQueue.stop();
            requestQueue.getCache().clear();
        } catch (Exception ignored) {

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            requestQueue.stop();
            requestQueue.getCache().clear();
        } catch (Exception ignored) {

        }
    }

    void setAdapter() {
        search_friend.setVisibility(View.VISIBLE);
        friendRecycle.setVisibility(View.VISIBLE);
        no_friends.setVisibility(View.GONE);
        friendsAdapter = new FriendsCommunityAdapter(CommunityFragment.this, mContext, friendsModelList, this);
        friendRecycle.setAdapter(friendsAdapter);
        if(getActivity()!=null) {
            DividerItemDecoration itemDecoration =
                    new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
            itemDecoration.setDrawable(requireActivity().getDrawable(R.drawable.layer));

            friendRecycle.addItemDecoration(itemDecoration);
        }CM.HideProgressLoader();
    }

    @Override
    public void onClickViewEvent(View view, int position) {
        ImageView unfriend = view.findViewById(R.id.unfriend);
        RelativeLayout layout = view.findViewById(R.id.card);
        if (view == unfriend) {
            CM.ShowDialogueWithCustomAction(
                    getActivity(),
                    "Are you sure, you want to unfriend this user?",
                    "Yes", "No", true,
                    new DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            unfriend(position);
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    }
            );
        }

        if (view == layout) {
            openFriendDetailScreen(position);
        }
    }

    void unfriend(int position) {

        if (CM.isConnected(getActivity())) {
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("friend_id", friendsModelList.get(position).getDataUser());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CM.showProgressLoader(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.unfriend, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, String.valueOf(response));
                            CM.HideProgressLoader();
                            try {

                                if (response.getString("status").equals("true")) {

                                    try {
                                        friendsModelList.remove(position);
                                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                        getFriends();
                                    } catch (Exception e) {

                                    }


                                } else {
                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();

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
            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();

    }

    void openFriendDetailScreen(int position) {
        Intent intent = new Intent(getContext(), FriendDetailScreen.class);
        intent.putExtra("friendId", friendsModelList.get(position).getDataUser());
        startActivityForResult(intent, 20);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 20) {
            getFriends();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void searchFriend(List<FriendsModel> items, String searchItem) {
        temp.clear();
        for (FriendsModel item : items) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(searchItem.toLowerCase(Locale.ROOT))) {
                temp.add(item);
            }

        }
        if (temp.size() == 0) {
            friendRecycle.setVisibility(View.GONE);
            no_friends.setVisibility(View.VISIBLE);
        } else {
            friendRecycle.setVisibility(View.VISIBLE);
            no_friends.setVisibility(View.GONE);
        }

        friendsAdapter = new FriendsCommunityAdapter(CommunityFragment.this, mContext, temp, this);
        friendRecycle.setAdapter(friendsAdapter);
        friendsAdapter.notifyDataSetChanged();


    }

}
