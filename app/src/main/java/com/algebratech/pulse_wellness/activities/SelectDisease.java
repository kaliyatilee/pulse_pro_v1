package com.algebratech.pulse_wellness.activities;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.DiseaseAdapter;
import com.algebratech.pulse_wellness.adapters.LikeAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.DiseaseModel;
import com.algebratech.pulse_wellness.models.OrganizationModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.FeedCommentListner;
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

import java.util.ArrayList;
import java.util.List;

public class SelectDisease extends AppCompatActivity implements NewsFeedListner {
    private Button next;
    TextView skip;
    SharedPreferences sharedPreferences;
    List<DiseaseModel> diseaseModels = new ArrayList<>();
    String userId;
    RecyclerView recycle_diseases;
    private DiseaseAdapter diseaseAdapter;
    private RecyclerView.Adapter mAdapter;
    private TextView diseaseButton;
    private List<String> array = new ArrayList<>();
    private RequestQueue requestQueue;
    String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_disease);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        recycle_diseases = findViewById(R.id.recycle_diseases);
        next = findViewById(R.id.next);
        skip = findViewById(R.id.skip);
        userNotes();

        if (CM.isConnected(SelectDisease.this)) {
            getDisease();
        } else
            Toast.makeText(SelectDisease.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event = "skip";
                WellnessPlan();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event = "next";
                WellnessPlan();
            }
        });
    }

    private void userNotes() {
        Dialog dialog = new Dialog(SelectDisease.this);
        dialog.setContentView(R.layout.dialog_common_custom_msg);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnyes = dialog.findViewById(R.id.btnYes);

        btnNo.setVisibility(View.GONE);
        TextView txtMsg = dialog.findViewById(R.id.cDialog_txtMsg);

        txtMsg.setText(R.string.mcdMessage);
        btnyes.setText("I understood");

        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(SelectDisease.this, "Thank you !!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();






//
//        CM.ShowDialogueWithCustomAction(
//                SelectDisease.this,
//                SelectDisease.this.getString(R.string.mcdMessage),
//                "I Understood", "", false,
//                new DialogClickListener() {
//                    @Override
//                    public void onPositiveClick() {
//
////                        finish();
//                    }
//
//                    @Override
//                    public void onNegativeClick() {
//
//                    }
//                }
//        );
    }

    private void getDisease() {
        CM.showProgressLoader(SelectDisease.
                this);
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getDisease, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("true")) {
                                CM.HideProgressLoader();
                                Log.e("Disease", response.toString());
                                JSONArray array = new JSONArray(response.getString("data"));
                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);

                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String created_at = object.getString("created_at");
                                    String updated_at = object.getString("updated_at");

                                    DiseaseModel diseaseModel = new DiseaseModel(id, name, created_at, updated_at, false);
                                    diseaseModels.add(diseaseModel);
                                }
                                setDataAdapter();

                            }

                        } catch (Exception e) {

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

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);


    }

    void setDataAdapter() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_diseases);
        diseaseAdapter = new DiseaseAdapter(diseaseModels, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectDisease.this));
        mAdapter = diseaseAdapter;
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClickViewEvent(View view, int position) {
        diseaseButton = view.findViewById(R.id.disease);
        if (view == diseaseButton) {
            if (!diseaseModels.get(position).getSelected()) {
                diseaseModels.get(position).setSelected(true);
                array.add(diseaseModels.get(position).getId());
            } else {
                diseaseModels.get(position).setSelected(false);
                array.remove(diseaseModels.get(position).getId());
            }

            mAdapter.notifyItemChanged(position);


        }
    }


    private void WellnessPlan() {

        if (CM.isConnected(SelectDisease.this)) {
            CM.showProgressLoader(this);
            JSONObject object = new JSONObject();
            try {
                if (event.equals("next")) {
                    object.put("user_id", userId);
                    if (array.size() > 0) {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < array.size(); i++) {
                            jsonArray.put(array.get(i));
                        }
                        object.put("disease_id", jsonArray);
                    }
                }
                if (event.equals("skip")) {
                    object.put("user_id", userId);
                }
                Log.e("OBJECTSS", object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.generateWellnessPlan, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            CM.HideProgressLoader();
                            Log.d(Constants.TAG, String.valueOf(response));
                            Log.d(Constants.TAG, String.valueOf(object));

                            try {
                                if (response.getString("status").equals("true")) {
                                    startActivity(new Intent(SelectDisease.this, generatingWellnessPlan.class));
                                    finish();
                                }

                            } catch (JSONException e) {
                                CM.HideProgressLoader();
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    VolleyLog.d("Error", "Error: " + error.toString());
                }
            });
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(SelectDisease.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }
}