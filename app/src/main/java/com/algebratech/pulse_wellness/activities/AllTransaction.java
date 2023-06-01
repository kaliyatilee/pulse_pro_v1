package com.algebratech.pulse_wellness.activities;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.DetailActivityScreen;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.NewsFeedAdapter;
import com.algebratech.pulse_wellness.adapters.TransactionAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.TransactionModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllTransaction extends AppCompatActivity implements NewsFeedListner {
    private Toolbar toolbarPolicy;
    private int requestCountmain = 1;
    private String userId;
    private SharedPreferences sharedPreferences;
    List<TransactionModel> transactionModels = new ArrayList<>();
    TextView no_transaction;
    private RequestQueue requestQueue;
    private TransactionAdapter transactionAdapter;
    private RecyclerView.Adapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transaction);

        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        setTitle("All Transactions");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        no_transaction = findViewById(R.id.no_transaction);
        recyclerView = findViewById(R.id.recycle_token);
        getAllTransactions(requestCountmain);

    }

    private void getAllTransactions(int requestCount) {

        if (CM.isConnected(AllTransaction.this)) {
            CM.showProgressLoader(AllTransaction.this);
            JSONObject object = new JSONObject();
            try {

                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.transaction_history, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            transactionModels = new ArrayList<>();
                            try {

                                if (response.getString("status").equals("success")) {

                                    Log.e("ALLTRANSACTIONS", String.valueOf(response));

                                    JSONArray array = new JSONArray(response.getString("data"));

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);
                                        String id = object.getString("id");
                                        String user_id = object.getString("user_id");
                                        String reward_id = object.getString("reward_id");
                                        String transaction_id = object.getString("transaction_id");
                                        String redeem_key = object.getString("redeem_key");
                                        String price = object.getString("price");
                                        String status = object.getString("status");
                                        String created_at = object.getString("created_at");
                                        String updated_at = object.getString("updated_at");
                                        String partner_id = object.getString("partner_id");
                                        String reward_name = object.getString("reward_name");
                                        String amount = object.getString("amount");
                                        String pulse_points = object.getString("pulse_points");
                                        String description = object.getString("description");
                                        String imageurl = object.getString("imageurl");
                                        String name = object.getString("name");

                                        TransactionModel transactionModel = new TransactionModel(id, user_id, reward_id, transaction_id, redeem_key, price, status, created_at, updated_at, partner_id, reward_name, amount, pulse_points, description, imageurl, name);
                                        transactionModels.add(transactionModel);
                                    }


                                } else {

                                }

                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());
                                CM.HideProgressLoader();
                            }

                            if (transactionModels.size() == 0) {
                                no_transaction.setVisibility(View.VISIBLE);
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
            Toast.makeText(AllTransaction.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setdataAdapter() {
        transactionAdapter = new TransactionAdapter(transactionModels, getContext(), this);
        mAdapter = transactionAdapter;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        no_transaction.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(mAdapter);
        CM.HideProgressLoader();
    }

    @Override
    public void onClickViewEvent(View view, int position) {
        Button button = view.findViewById(R.id.showCode);
        if (view == button) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Redeem Token");
            builder.setMessage(transactionModels.get(position).getRedeem_key())
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            LayoutInflater factory = LayoutInflater.from(AllTransaction.this);
            final View v = factory.inflate(R.layout.qr_layout, null);
            builder.setView(v);
            ImageView imageView = v.findViewById(R.id.qr_code);
            AlertDialog alert = builder.create();

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = qrCodeWriter.encode(transactionModels.get(position).getRedeem_key(), BarcodeFormat.QR_CODE, 300, 300);
                Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
                for (int x = 0; x < 300; x++) {
                    for (int y = 0; y < 300; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            alert.show();
        }
    }
}