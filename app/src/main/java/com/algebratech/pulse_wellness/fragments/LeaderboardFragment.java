package com.algebratech.pulse_wellness.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.inuker.bluetooth.library.BluetoothService.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.DetailActivityScreen;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.FriendDetailScreen;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.adapters.FriendsCommunityAdapter;
import com.algebratech.pulse_wellness.adapters.LeaderBoardAdapter;
import com.algebratech.pulse_wellness.adapters.NewsFeedAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.FriendsModel;
import com.algebratech.pulse_wellness.models.LeaderBoardModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardFragment extends Fragment implements NewsFeedListner {
    View root;
    private Context mContext;
    private RecyclerView leaderBoardRecycle;
    private final List<LeaderBoardModel> leaderBoardModels = new ArrayList<>();
    private RecyclerView.Adapter leaderAdapter;
    private LinearLayout cardView;
    private String userId;
    private SharedPreferences sharedPreferences;
    private TextView no_data;
    private RequestQueue requestQueue;
    int count = 0;
    private CircleImageView circleImageView1, circleImageView2, circleImageView3;
    RelativeLayout profilePicRel2, profilePicRel3, profilePicRel1;
    ImageView crown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_leaderboard_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    void init() {
        if (root != null) {
            mContext = getContext();
            leaderBoardRecycle = root.findViewById(R.id.recycle_leader);
            no_data = root.findViewById(R.id.no_data);
            leaderBoardRecycle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            sharedPreferences = getActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
            circleImageView1 = root.findViewById(R.id.profilePic);
            circleImageView2 = root.findViewById(R.id.profilePic2);
            circleImageView3 = root.findViewById(R.id.profilePic3);
            userId = sharedPreferences.getString("userID", null);

            profilePicRel2 = root.findViewById(R.id.profilePicRel3);
            profilePicRel3 = root.findViewById(R.id.profilePicRel2);
            profilePicRel1 = root.findViewById(R.id.profilePicRel);
            crown = root.findViewById(R.id.crown);

            profilePicRel2.setVisibility(View.GONE);
            profilePicRel3.setVisibility(View.GONE);
            profilePicRel1.setVisibility(View.GONE);

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    getLeaders();
//                }
//            }, 500);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getLeaders() {

        if (CM.isConnected(getActivity())) {
            {
                CM.showProgressLoader(getActivity());


                JSONObject object = new JSONObject();
                try {
                    object.put("user_id", userId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getLeaderBoard, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    leaderBoardModels.clear();
                                    JSONArray array = new JSONArray(response.getString("data"));
                                    if (!array.isNull(0)) {
                                        Log.e("LEADERBOARD", response.toString());
                                        for (int i = 0; i < array.length(); i++) {

                                            JSONObject object = array.getJSONObject(i);
                                            String user_id = object.getString("user_id");
                                            String friend_id = object.getString("friend_id");
                                            String steps = object.getString("steps");
                                            String firstname = object.getString("firstname");
                                            String lastname = object.getString("lastname");
                                            String profileurl = object.getString("profileurl");
                                            count = count + 1;
                                            LeaderBoardModel leaderBoardModel = new LeaderBoardModel(user_id, friend_id, steps, firstname, lastname, profileurl, count);
                                            leaderBoardModels.add(leaderBoardModel);
                                        }

                                    }
                                } catch (Exception e) {
                                    CM.HideProgressLoader();
                                    Log.e(Constants.TAG, e.getMessage());
                                    leaderBoardRecycle.setVisibility(View.GONE);
                                    no_data.setVisibility(View.VISIBLE);
                                }

                                if (leaderBoardModels.size() == 0) {
                                    CM.HideProgressLoader();
                                    leaderBoardRecycle.setVisibility(View.GONE);
                                    no_data.setVisibility(View.VISIBLE);
                                } else
                                    setdataAdapter();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CM.HideProgressLoader();
                        leaderBoardRecycle.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                    }
                });

                requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(jsonObjectRequest);
            }
        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setdataAdapter() {
        CM.HideProgressLoader();

        if (leaderBoardModels.size() > 0) {
            leaderBoardRecycle.setVisibility(View.VISIBLE);
            profilePicRel1.setVisibility(View.VISIBLE);
            crown.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.GONE);
            Glide.with(mContext).load(leaderBoardModels.get(0).getProfileurl()).error(R.drawable.placeholder).into(circleImageView1);
        }

        if (leaderBoardModels.size() > 1) {
            profilePicRel2.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(leaderBoardModels.get(1).getProfileurl()).error(R.drawable.placeholder).into(circleImageView2);
        } else
            profilePicRel2.setVisibility(View.GONE);

        if (leaderBoardModels.size() > 2) {
            profilePicRel3.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(leaderBoardModels.get(2).getProfileurl()).error(R.drawable.placeholder).into(circleImageView3);

        } else
            profilePicRel3.setVisibility(View.GONE);


        leaderAdapter = new LeaderBoardAdapter(getActivity(), leaderBoardModels, this);
        leaderBoardRecycle.setAdapter(leaderAdapter);

        if (leaderBoardModels.size() > 0 && getActivity() != null) {
            DividerItemDecoration itemDecoration =
                    new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
            itemDecoration.setDrawable(requireActivity().getDrawable(R.drawable.layer));
            leaderBoardRecycle.addItemDecoration(itemDecoration);
        }
    }

    @Override
    public void onClickViewEvent(View view, int position) {

        cardView = view.findViewById(R.id.cardd);
        if (view == cardView) {
            Intent intent = new Intent(getContext(), DetailActivityScreen.class);
            intent.putExtra("url", leaderBoardModels.get(position).getProfileurl());
            intent.putExtra("steps", leaderBoardModels.get(position).getSteps());
            intent.putExtra("name", leaderBoardModels.get(position).getFirstname() + " " + leaderBoardModels.get(position).getLastname());
            intent.putExtra("friendId", leaderBoardModels.get(position).getUser_id());
            startActivity(intent);
        }
    }
}