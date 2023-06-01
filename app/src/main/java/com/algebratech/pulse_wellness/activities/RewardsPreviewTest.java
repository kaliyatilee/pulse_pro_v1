package com.algebratech.pulse_wellness.activities;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.SliderAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.SliderDataModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RewardsPreviewTest extends AppCompatActivity {

    private Button btnRedeem;
    private TextView tvHeadline, tvDesc, tvPrice;

    private View parent_view;
    private TextView tv_qty;

    private SliderView sliderView;

    String url1, url2, url3, productId, userId;
    ArrayList<SliderDataModel> sliderDataArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_preview2);

        parent_view = findViewById(R.id.parent_view);

        sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        initToolbar();
        initComponent();

        setImageView();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rewards");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {


        tvHeadline = findViewById(R.id.tvHeadline);
        tvDesc = findViewById(R.id.tvDesc);
        tvPrice = findViewById(R.id.tvPrice);

        tvHeadline.setText(getIntent().getStringExtra("title"));
        tvPrice.setText(Math.round(Float.parseFloat(getIntent().getStringExtra("points"))) + " coins");
        tvDesc.setText(getIntent().getStringExtra("description"));
       // tvDesc.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        productId = getIntent().getStringExtra("prod_id");

        url1 = getIntent().getStringExtra("image");
        url2 = getIntent().getStringExtra("image");
        url3 = getIntent().getStringExtra("image");

        // Glide.with(getApplicationContext()).load(getIntent().getStringExtra("image")).into((ImageView) findViewById(R.id.image));

        findViewById(R.id.bt_add_to_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedeemProduct();
            }
        });


    }

    private void setImageView() {
        sliderView = findViewById(R.id.slider);
        sliderDataArrayList.add(new SliderDataModel(url1));
        sliderDataArrayList.add(new SliderDataModel(url2));
        sliderDataArrayList.add(new SliderDataModel(url3));
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void RedeemProduct() {
        if (CM.isConnected(RewardsPreviewTest.this)) {
            CM.showProgressLoader(RewardsPreviewTest.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", Integer.parseInt(userId));
                object.put("product_id", Integer.parseInt(productId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.redeem, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, String.valueOf(response));

                            try {
                                if (response.getString("status").equals("true")) {
                                    CM.HideProgressLoader();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RewardsPreviewTest.this);
                                    builder.setTitle("Redeem Success");
                                    builder.setMessage(response.getString("message"))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    CM.HideProgressLoader();
                                                }
                                            });
                                    LayoutInflater factory = LayoutInflater.from(RewardsPreviewTest.this);
                                    final View v = factory.inflate(R.layout.qr_layout, null);
                                    builder.setView(v);
                                    ImageView imageView = v.findViewById(R.id.qr_code);
                                    AlertDialog alert = builder.create();

                                    String qrString = response.getString("redeem_key");
                                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                    try {
                                        BitMatrix bitMatrix = qrCodeWriter.encode(qrString, BarcodeFormat.QR_CODE, 300, 300);
                                        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
                                        for (int x = 0; x<300; x++){
                                            for (int y=0; y<300; y++){
                                                bitmap.setPixel(x,y,bitMatrix.get(x,y)? Color.BLACK : Color.WHITE);
                                            }
                                        }
                                        imageView.setImageBitmap(bitmap);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    alert.show();


                                } else if (response.getString("status").equals("false")) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(RewardsPreviewTest.this);
//                                builder.setTitle("Redeem Fail");
//                                builder.setMessage(response.getString("message"))
//                                        .setCancelable(false)
//                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                CM.HideProgressLoader();
//                                            }
//                                        });
//                                AlertDialog alert = builder.create();
//                                alert.show();
//                                // Snackbar.make(parent_view, response.getString("message"), Snackbar.LENGTH_SHORT).show();

                                    CM.ShowDialogueWithCustomAction(
                                            RewardsPreviewTest.this,
                                            response.getString("message"),
                                            "Ok", "", false,
                                            new DialogClickListener() {
                                                @Override
                                                public void onPositiveClick() {
                                                    finish();
                                                }

                                                @Override
                                                public void onNegativeClick() {

                                                }
                                            }
                                    );

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
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(RewardsPreviewTest.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }
}
