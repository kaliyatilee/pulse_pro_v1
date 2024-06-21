package com.algebratech.pulse_wellness.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.adapters.RewardsAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.models.RewardsModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RewardsFragment extends Fragment {

    View root;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    private String userEmail;
    private TextView textView, no_product;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter mAdapter;
    private List<RewardsModel> products;
    public LinearLayoutManager mLayoutManager;
    private TextView pontVies;
    private DBHelper db;
    private RequestQueue requestQueue;
    private EditText searchProduct;
    List<RewardsModel> temp = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rewards, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    void init() {
        if(root!=null) {
            requestQueue = Volley.newRequestQueue(getContext());
            recyclerView = root.findViewById(R.id.recyclerView);
            pontVies = root.findViewById(R.id.pontVies);
            no_product = root.findViewById(R.id.no_product);
            searchProduct = root.findViewById(R.id.search_product);
            sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
            myEdit = sharedPreferences.edit();
            SQLiteDatabase.loadLibs(getContext());
            db = new DBHelper(getContext());


            manager = new GridLayoutManager(getActivity(), 2);
            mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            products = new ArrayList<>();

            try {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        if (CM.isConnected(getActivity())) {
                            getProfile();
                            getProducts();
                        } else {
                            if (getActivity()!=null)
                                Toast.makeText(requireActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();
                        }
//                    }
//                },500);
            } catch (Exception e) {


            }

            searchProduct.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    getAllItems(products, charSequence.toString());
                    if (temp.size() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        no_product.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        no_product.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }
    }


    private void getProfile() {


        String coins = db.getTotal(sharedPreferences.getString("userID", ""));
        double coin = Double.parseDouble(coins);
        Log.d(Constants.TAG + "@Coins", String.valueOf(coin));

        //FirebaseDatabase.getInstance().getReference("Patients").child(StaticVariables.user_id).child("loyaltPoints").setValue(StaticMethods.roundTwoDecimals(coin));
        //pontVies.setText("  You have "+ StaticMethods.roundTwoDecimals(coin) +" points in your wallet");

        getCoins();
    }

    private void getProducts() {
        System.out.println("All rewards");
        CM.showProgressLoader(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Api.allrewards, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Response");
                System.out.println(response);
                try {

                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        String pulse_points = "0";
                        String id = object.getString("id");
                        String title = object.getString("reward_name");
                        if (object.getString("pulse_points").isEmpty() || object.getString("pulse_points") == " ") {
                            pulse_points = "0";
                        } else
                            pulse_points = String.valueOf(object.getDouble("pulse_points"));


                        String description = object.getString("description");
                        String imageurl = object.getString("imageurl");
//                        String merchant_name = object.getString("name");
                        String merchant_name = "";

                        RewardsModel product = new RewardsModel(id, title, imageurl, description, pulse_points,merchant_name);
                        products.add(product);
                        if (products.size() == 0) {
                            recyclerView.setVisibility(View.GONE);
                            searchProduct.setVisibility(View.GONE);
                            no_product.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            searchProduct.setVisibility(View.VISIBLE);
                            no_product.setVisibility(View.GONE);
                            mAdapter = new RewardsAdapter(getContext(), products);
                            recyclerView.setAdapter(mAdapter);
                        }

                        CM.HideProgressLoader();
                    }

                } catch (Exception e) {
                    System.out.println("This is the exception");
                    System.out.println(e.getMessage());
                    CM.HideProgressLoader();
                    Log.e(Constants.TAG, e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CM.HideProgressLoader();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", sharedPreferences.getString("userID", ""));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };


        requestQueue.add(sr);


    }


    private void getCoins() {
        CM.showProgressLoader(getActivity());
        StringRequest sr2 = new StringRequest(Request.Method.POST, Api.getcoins, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(Constants.TAG, String.valueOf(response));

                pontVies.setText("  You have " + response + " coins in your wallet");
                CM.HideProgressLoader();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CM.HideProgressLoader();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", sharedPreferences.getString("userID", ""));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(sr2);

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.allrewards,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.d(Constants.TAG, String.valueOf(response));
//
//                        try {
//
//                            JSONArray array = new JSONArray(response);
//                            for (int i = 0; i<array.length(); i++){
//
//                                JSONObject object = array.getJSONObject(i);
//
//                                String title = object.getString("reward_name");
//                                String pulse_points = String.valueOf(object.getDouble("pulse_points"));
//                                String description = object.getString("description");
//                                String imageurl = object.getString("imageurl");
//
//
//                                RewardsModel product = new RewardsModel(title,description,pulse_points,imageurl);
//                                products.add(product);
//                            }
//
//                        }catch (Exception e){
//
//                            Log.e(Constants.TAG,e.getMessage());
//                        }
//
//                        mAdapter = new RewardsAdapter(getContext(),products);
//                        recyclerView.setAdapter(mAdapter);
//                        progressbar.setVisibility(View.GONE);
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressbar.setVisibility(View.GONE);
//
//            }
//        });
//
//        requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(stringRequest);


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

    public void getAllItems(List<RewardsModel> items, String searchItem) {
        temp.clear();
        for (RewardsModel item : items) {
            if (item.getTitle().toLowerCase(Locale.ROOT).contains(searchItem.toLowerCase(Locale.ROOT))) {
                temp.add(item);
            }

        }
        mAdapter = new RewardsAdapter(getContext(), temp);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }
}
