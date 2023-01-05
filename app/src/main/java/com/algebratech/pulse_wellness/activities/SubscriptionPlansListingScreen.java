package com.algebratech.pulse_wellness.activities;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.subscriptionAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.fragments.ProfileActivity;
import com.algebratech.pulse_wellness.models.SubscriptionPlanModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.FeedCommentListner;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.CardParams;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlansListingScreen extends AppCompatActivity implements FeedCommentListner {
    private Toolbar toolbarPolicy;
    private String plan_id, card_status;
    private String userId;
    SharedPreferences.Editor myEdit;
    private SharedPreferences sharedPreferences;
    private RecyclerView subscription_recycle;
    private RequestQueue requestQueue;
    private int requestCountmain = 1;
    private subscriptionAdapter subscriptionAdapter;
    List<SubscriptionPlanModel> subscriptionPlanModelsList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    CardView cardView;
    WebView webView;
    EditText number, expiry, cvv;
    Button Add;
    ArrayList<String> listOfPattern = new ArrayList<String>();
    String ptVisa, ptMasterCard, ptAmeExp, ptDinClb, ptDiscover, ptJcb;
    private EditText editTextCreditCardNumber;
    private ProgressBar progress;
    private Stripe stripe;
    Button getNow;
    Button AddCard;
    ScrollView scroll;
    int CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plans_listing_screen);

        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        subscription_recycle = findViewById(R.id.subscription_recycle);
        setSupportActionBar(toolbarPolicy);
        scroll = findViewById(R.id.scroll);
        progress = findViewById(R.id.progress);
        setTitle("Subscription Plans");
        cardString();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        userId = sharedPreferences.getString("userID", "");
        card_status = sharedPreferences.getString("card_status", "");
        getPlans();

        getNow = findViewById(R.id.getNow);
        AddCard = findViewById(R.id.addCard);


        AddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        getNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subscribe();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(CODE);
        super.onBackPressed();
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SubscriptionPlansListingScreen.this, R.style.BottomSheetDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_add_card, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();

        number = view.findViewById(R.id.card_number);
        expiry = view.findViewById(R.id.expiry);
        cvv = view.findViewById(R.id.cvv);
        Add = view.findViewById(R.id.Add);

        number.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ccNum = s.toString();
                ccNum = ccNum.replaceAll("[\\s-]+", "");
                for (String p : listOfPattern) {
                    if (ccNum.matches(p)) {
                        if (p.equals(ptVisa)) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visaa, 0);
                        }
                        if (p.equals(ptMasterCard)) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.maestro, 0);
                        }
                        if (p.equals(ptAmeExp)) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.american, 0);
                        }
                        if (p.equals(ptDinClb)) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dinnersclub, 0);
                        }
                        if (p.equals(ptDiscover)) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.discover, 0);
                        }
                        if (p.equals(ptJcb)) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.jcb, 0);
                        }

                        break;
                    }
                }
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }


            }


        });

        expiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = s.toString();
                if (current.length() == 2 && start == 1) {
                    expiry.setText(current + "/");
                    expiry.setSelection(current.length() + 1);
                } else if (current.length() == 2 && before == 1) {
                    current = current.substring(0, 1);
                    expiry.setText(current);
                    expiry.setSelection(current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!number.getText().toString().isEmpty() && !expiry.getText().toString().isEmpty() && !cvv.getText().toString().isEmpty()) {
                    String month = expiry.getText().toString().substring(0, 2);
                    String year = expiry.getText().toString().substring(expiry.getText().toString().indexOf("/") + 1);

                    add(number.getText().toString(), month, year, cvv.getText().toString());
                    bottomSheetDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Please enter all details first", Toast.LENGTH_SHORT).show();

            }
        });
    }


    void getPlans() {
        if (CM.isConnected(SubscriptionPlansListingScreen.this)) {
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getsubscriptionplans, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progress.setVisibility(View.GONE);
                                getNow.setVisibility(View.VISIBLE);

                                if (response.getString("status").equals("true")) {
                                    Log.e("Subscription list", response.toString());
                                    Log.e("CARD", card_status);
                                    JSONArray array = new JSONArray(response.getString("data"));
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String plan_name = object.getString("plan_name");
                                        String amount = object.getString("amount");
                                        String description = object.getString("description");
                                        String days = object.getString("days");
                                        String created_at = object.getString("created_at");
                                        String updated_at = object.getString("updated_at");
                                        String expiry_date = object.optString("expiry_date");

                                        SubscriptionPlanModel subscriptionPlanModel = new SubscriptionPlanModel(
                                                id, plan_name, amount, description, days, created_at, updated_at, expiry_date, i == 0);

                                        subscriptionPlanModelsList.add(subscriptionPlanModel);

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
                    Log.e("Error", error.toString());

                }
            });

            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
            requestCountmain++;
        } else
            Toast.makeText(SubscriptionPlansListingScreen.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }

    void setDataAdapter() {
        plan_id = subscriptionPlanModelsList.get(0).getId();
        subscriptionAdapter = new subscriptionAdapter(subscriptionPlanModelsList, getContext(), this);
        subscription_recycle.setHasFixedSize(true);
        subscription_recycle.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = subscriptionAdapter;
        subscription_recycle.setAdapter(mAdapter);

        if (subscriptionPlanModelsList != null && !subscriptionPlanModelsList.isEmpty()) {
            subscriptionPlanModelsList.get(0).setSelected(true);
            plan_id = subscriptionPlanModelsList.get(0).getId();

            webView = (WebView) findViewById(R.id.simpleWebView);
            webView.getSettings().setJavaScriptEnabled(true);
            WebSettings webSettings = webView.getSettings();
            webSettings.setDefaultFixedFontSize(18);

            String head1 = "<head><style>@font-face {font-family: 'arial';src: url('file:///android_asset/fonts/muli_semibold.ttf');}body {font-family: 'verdana';}</style></head>";
            String string = subscriptionPlanModelsList.get(0).getDescription();
            String text = "<html>" + head1
                    + "<body style='font-family: arial'>" + string
                    + "</body></html>";
            webView.loadDataWithBaseURL("", text, "text/html", "utf-8", "");
            scroll.setVisibility(View.VISIBLE);
            if (card_status.equals("1")) {
                getNow.setVisibility(View.VISIBLE);
                AddCard.setVisibility(View.GONE);
            } else {
                getNow.setVisibility(View.GONE);
                AddCard.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onClickViewEvent(View view, int position, int subPosition) {
        cardView = view.findViewById(R.id.subscription_card);
        if (view == cardView) {

            for (int i = 0; i < subscriptionPlanModelsList.size(); i++) {
                subscriptionPlanModelsList.get(i).setSelected(i == position);
                plan_id = subscriptionPlanModelsList.get(position).getId();
            }
            plan_id = subscriptionPlanModelsList.get(position).getId();
            mAdapter.notifyDataSetChanged();

            String head1 = "<head><style>@font-face {font-family: 'arial';src: url('file:///android_asset/fonts/muli_semibold.ttf');}body {font-family: 'verdana';}</style></head>";
            String string = subscriptionPlanModelsList.get(position).getDescription();
            String text = "<html>" + head1
                    + "<body style='font-family: arial'>" + string
                    + "</body></html>";
            webView.loadDataWithBaseURL("", text, "text/html", "utf-8", "");


        }
    }

    private void add(String number, String month, String year, String cvv) {
        CM.showProgressLoader(SubscriptionPlansListingScreen.this);
        Stripe striep = new Stripe(getContext(), "pk_test_51KBMBgKQDMir4i4yeXf9G9kpdfvPXorAWuzlLcfj0kAXXgmsnlCPbBP3P9L2Tx7OIbhd80NDzGtm5CQxeB3a3xDq00WXI32uB4");

        CardParams cardParams = new CardParams(number, Integer.parseInt(month), Integer.parseInt(year), cvv);

        striep.createCardToken(cardParams, new ApiResultCallback<Token>() {
            @Override
            public void onSuccess(@NonNull Token token) {

                Log.e("token", token.getId());
                Log.e("userid", userId);
                addCard(token.getId());
            }

            @Override
            public void onError(@NonNull Exception e) {
                CM.HideProgressLoader();
                Log.e("token", "error in token");
            }
        });

    }

    void addCard(String token) {
        if (CM.isConnected(SubscriptionPlansListingScreen.this)) {
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("source_token", token);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.addcard, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                CM.HideProgressLoader();
                                myEdit.putString("card_status", "1");
                                card_status = "1";
                                myEdit.apply();
                                AddCard.setVisibility(View.GONE);
                                getNow.setVisibility(View.VISIBLE);
                                Log.e("api token", response.toString());
                                Subscribe();
                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", error.toString());

                }
            });

            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
            requestCountmain++;
        } else
            Toast.makeText(SubscriptionPlansListingScreen.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    private void Subscribe() {
        if (CM.isConnected(SubscriptionPlansListingScreen.this)) {
            CM.showProgressLoader(SubscriptionPlansListingScreen.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("plan_id", plan_id);
                Log.e("SUBSCRIBE", String.valueOf(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.subscribe, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject subToken = new JSONObject();
                            String subscription_id;
                            Log.e("SUBSCRIBE", String.valueOf(response));
                            try {
                                subToken = response.getJSONObject("token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (response.getString("status").equals("success")) {
                                    CODE = 200;
                                    CM.HideProgressLoader();
                                    subscription_id = subToken.getString("subscriptions_id");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SubscriptionPlansListingScreen.this);
                                    LayoutInflater factory = LayoutInflater.from(SubscriptionPlansListingScreen.this);
                                    final View v = factory.inflate(R.layout.subscription_success, null);
                                    builder.setView(v);
                                    Button button = v.findViewById(R.id.ok);
                                    AlertDialog alert = builder.create();
                                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    JSONObject finalSubToken = subToken;
                                    String subStatus = finalSubToken.getString("status");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            myEdit.putString("sub_token", String.valueOf(finalSubToken));
                                            Log.e("sub_token", String.valueOf(finalSubToken));
                                            myEdit.putString("subscriptions_id", subscription_id);
                                            myEdit.putString("status", subStatus);
                                            Log.e("subscriptions_id", subscription_id);
//                                        Intent i = new Intent(getContext(), ProfileActivity.class);
//                                        startActivity(i);
                                            myEdit.apply();
                                            alert.dismiss();
                                            onBackPressed();
                                        }
                                    });
                                    alert.show();
                                } else {

                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();

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
                    Log.e("Error 1234", "Error 1111: " + error.toString());
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(SubscriptionPlansListingScreen.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }

    void cardString() {
        ptVisa = "^4[0-9]{6,}$";
        listOfPattern.add(ptVisa);
        ptMasterCard = "^5[1-5][0-9]{5,}$";
        listOfPattern.add(ptMasterCard);
        ptAmeExp = "^3[47][0-9]{5,}$";
        listOfPattern.add(ptAmeExp);
        ptDinClb = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
        listOfPattern.add(ptDinClb);
        ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
        listOfPattern.add(ptDiscover);
        ptJcb = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
        listOfPattern.add(ptJcb);
    }
}