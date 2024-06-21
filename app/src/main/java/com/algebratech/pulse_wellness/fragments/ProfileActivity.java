package com.algebratech.pulse_wellness.fragments;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.Camera2Activity;
import com.algebratech.pulse_wellness.activities.CreateProfileActivity;
import com.algebratech.pulse_wellness.activities.MainActivity;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.activities.SubscriptionPlansListingScreen;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.SubscriptionPlanModel;
import com.algebratech.pulse_wellness.network.VolleyMultipartRequest;
import com.algebratech.pulse_wellness.utils.BitmapUtils;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.IntentUtils;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;
    private TextView member_name, member_coins, member_country, member_time, member_bmi, member_height, member_weight, member_dob, member_email, member_bp, member_wthRatio, member_hba1c;
    private CircleImageView profilePic;
    private RelativeLayout profilePicRel;
    private Button activityToken, show_my_plan;
    private ImageView profileEdit;
    SubscriptionPlanModel subscriptionPlanModel;
    String user_id, path, card_status, subscription_id;
    int plan_id;
    private Toolbar toolbarPolicy;
    static final int REQUEST_PERMISSION_KEY = 1;
    com.google.android.material.floatingactionbutton.FloatingActionButton edtProfile;
    //permissions constants
    int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private FragmentManager FM;
    private FragmentTransaction FT;
    private static final int CAMERA_REQUEST = 9514;


    private Options options;
    private ArrayList<String> returnValue = new ArrayList<String>();
    String coins = "0";
    RelativeLayout progressBar;
    private RequestQueue rQueue;
    private ArrayList<HashMap<String, String>> arraylist;
    String sub_token;
    JSONObject json_sub_token;
    int CODE = 0;
    String subStatus;
    boolean isExpired = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        setTitle("Profile");
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        card_status = sharedPreferences.getString("card_status", null);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        myEdit = sharedPreferences.edit();
        user_id = sharedPreferences.getString("userID", "");
        subscription_id = sharedPreferences.getString("subscriptions_id", "");
        sub_token = sharedPreferences.getString("sub_token", "");
        try {
            json_sub_token = new JSONObject(sub_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        plan_id = sharedPreferences.getInt("plan_id", 0);


        member_country = findViewById(R.id.member_country);
        edtProfile = findViewById(R.id.edtProfile);
        member_name = findViewById(R.id.member_name);
        member_coins = findViewById(R.id.member_coins);
        member_time = findViewById(R.id.member_time);
        member_bmi = findViewById(R.id.member_bmi);
        member_height = findViewById(R.id.member_height);
        member_weight = findViewById(R.id.member_weight);
        member_dob = findViewById(R.id.member_dob);
        member_email = findViewById(R.id.member_email);
        member_wthRatio = findViewById(R.id.member_wthRatio);
        member_bp = findViewById(R.id.member_bp);
        member_hba1c = findViewById(R.id.member_hba1c);


        profileEdit = findViewById(R.id.profileEdit);
        profilePic = findViewById(R.id.profilePic);
        activityToken = findViewById(R.id.activityToken);
        show_my_plan = findViewById(R.id.show_my_plan);
        profilePicRel = findViewById(R.id.profilePicRel);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        setData();

        if (!json_sub_token.toString().equals("{}")) {

            show_my_plan.setVisibility(View.VISIBLE);
        }

        show_my_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });


        activityToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_token_activation, null);
                mBuilder.setIcon(R.mipmap.ic_launcher);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        options = Options.init()
                .setRequestCode(CAMERA_REQUEST)
                .setCount(1)
                .setFrontfacing(false)
                .setMode(Options.Mode.All)
                .setSpanCount(1)
                .setVideoDurationLimitinSeconds(30)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("pix/akshay");

        edtProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pix.start((FragmentActivity) context, options);
                Intent intent = new Intent(ProfileActivity.this, Camera2Activity.class);
                intent.putExtra(IntentUtils.CAMERA_VIEW_SHOW_PICK_IMAGE_BUTTON, true);
                intent.putExtra(IntentUtils.IS_STATUS, true);
                startActivityForResult(intent, CAMERA_REQUEST);

            }
        });

//        profilePicRel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(ProfileActivity.this, Camera2Activity.class);
//                intent.putExtra(IntentUtils.CAMERA_VIEW_SHOW_PICK_IMAGE_BUTTON, true);
//                intent.putExtra(IntentUtils.IS_STATUS, true);
//                startActivityForResult(intent, CAMERA_REQUEST);
//
//            }
//        });


        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(ProfileActivity.this, CreateProfileActivity.class), 1001);
            }
        });

    }


    private void setData() {

        member_country.setText(sharedPreferences.getString("country", null));
        Log.d("SinceWhatWhat", sharedPreferences.getString("dateCreated", null).substring(0, 10));
        member_time.setText("Member since  " + StaticMethods.correctDate(sharedPreferences.getString("dateCreated", null).substring(0, 10), "yyyy-MM-dd", "dd MMM yyyy"));
        member_name.setText(sharedPreferences.getString("firstname", null) + " " + sharedPreferences.getString("lastname", null));
        member_dob.setText(StaticMethods.correctDate(sharedPreferences.getString("dob", null), "yyyy/MM/dd", "dd MMM yyyy"));
        member_height.setText(sharedPreferences.getString("height", null) + " cm");
        member_weight.setText(sharedPreferences.getString("weight", null) + " Kg");
        member_bmi.setText(sharedPreferences.getString("bmi", null) + "Kg/m2");
        member_email.setText(sharedPreferences.getString("email", null));
        Glide.with(this).load(sharedPreferences.getString("profileURL", null)).error(R.drawable.placeholder).into(profilePic);
        member_wthRatio.setText(sharedPreferences.getString("wthRatio", "") + " cm");
        member_hba1c.setText(sharedPreferences.getString("hba1c", "") + " %");
        member_bp.setText(sharedPreferences.getString("bp", ""));
        if (CM.isConnected(ProfileActivity.this)) {
            getCoins();
        } else
            Toast.makeText(ProfileActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            setData();
        }

        try {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

        } catch (Exception e) {
            Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
            //Toast.makeText(context,"Please use a image , not a video.",Toast.LENGTH_LONG).show();
        }
        try {

            if (!returnValue.isEmpty()) {

                Log.d(Constants.TAG + "PATH", returnValue.get(0).toString());

                File f = new File(returnValue.get(0));
                int len = f.getAbsolutePath().length();
                String extension = String.valueOf(f.getAbsolutePath().subSequence(len - 3, len));
                Bitmap bitmap;

                if (extension.equals("mp4") | extension.equals("mkv")) {

//                        Log.d(Constants.TAG + "File", f.getAbsolutePath().toString());
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            bitmap = ThumbnailUtils.createVideoThumbnail(f, new Size(500, 500), null);
//                        } else {
//                            bitmap = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
//                            Log.d(Constants.TAG + "File", f.getAbsolutePath().toString());
//                        }
//                        Glide.with(context).load(bitmap).into(profilePic);
                    Toast.makeText(ProfileActivity.this, "Please use a image , not a video.", Toast.LENGTH_LONG).show();


                }

                if (extension.equals("jpg") | extension.equals("png") | extension.equals("peg")) {
                    Log.d(Constants.TAG + "File", f.getAbsolutePath().toString());
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                    Log.d(Constants.TAG + "Bitmap", BitmapUtils.decodeImage(bitmap));
                    Picasso.with(this).load("file:" + f.getAbsolutePath()).error(R.drawable.placeholder).into(profilePic);
                    Uri uri = Uri.fromFile(new File(f.getAbsolutePath()));
                    String displayName = String.valueOf(Calendar.getInstance().getTimeInMillis() + "." + extension);

                    if (CM.isConnected(ProfileActivity.this)) {
                        uploadProfilePic(displayName, uri);
                    } else
                        Toast.makeText(ProfileActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


                }


            }

        } catch (Exception e) {
            Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
            Toast.makeText(ProfileActivity.this, "Please use a image , not a video.", Toast.LENGTH_LONG).show();
        }


    }

    private void uploadProfilePic(final String pdfname, Uri pdffile) {
        CM.showProgressLoader(ProfileActivity.this);
        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Api.add_profile_pic,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            CM.HideProgressLoader();
                            Log.d(Constants.TAG + "RES", new String(response.data));
                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                Toast.makeText(ProfileActivity.this, "Profile uploaded successfully", Toast.LENGTH_SHORT).show();
                                Log.d(Constants.TAG + "URL", jsonObject.getString("url"));
                                myEdit.putString("profileURL", jsonObject.getString("url"));
                                myEdit.apply();
//                                ((MainActivity) getApplicationContext()).syncProfile();


                                new MainActivity().syncProfile();

                                jsonObject.toString().replace("\\\\", "");

                                if (jsonObject.getString("status").equals("true")) {
                                    Log.d("come::: >>>  ", "yessssss");
                                    arraylist = new ArrayList<HashMap<String, String>>();
                                    JSONArray dataArray = jsonObject.getJSONArray("data");

                                    JSONObject jsonObjectData = new JSONObject(String.valueOf(jsonObject.getJSONArray("data")));


                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject dataobj = dataArray.getJSONObject(i);

                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", user_id);  //add string parameters
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("uploaded_file", new DataPart(pdfname, inputData));
                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ProfileActivity.this);
            rQueue.add(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void showBottomSheetDialog() {
        getSubscription();
    }

    void getSubscription() {

        if (CM.isConnected(ProfileActivity.this)) {
            CM.showProgressLoader(ProfileActivity.this);
            subStatus = sharedPreferences.getString("status", "");
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getusersubscriptionplans, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e("RESPONSE", response.toString());
                                if (response.getString("status").equals("true")) {

                                    JSONArray array = new JSONArray(response.getString("data"));

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject sub = array.getJSONObject(i);

                                        String id = sub.getString("id");
                                        String plan_name = sub.getString("plan_name");
                                        String product_id = sub.getString("product_id");
                                        String price_id = sub.getString("price_id");
                                        String amount = sub.getString("amount");
                                        String description = sub.getString("description");
                                        String days = sub.getString("days");
                                        String created_at = sub.getString("created_at");
                                        String updated_at = sub.getString("updated_at");
                                        String expiry_date = sub.getString("expiry_date");
                                        String subscriptions_id = sub.getString("subscriptions_id");

                                        subscriptionPlanModel = new SubscriptionPlanModel(id, plan_name, amount, description, days, created_at, updated_at, expiry_date, false);

                                    }

//                                String date = subscriptionPlanModel.getExpiry_date();
//                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                                try {
//                                    Date strDate = sdf.parse(date);
//                                    if (new Date().after(strDate)) {
//                                        isExpired = true;
//                                    } else {
//                                        isExpired = false;
//                                    }
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }

                                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialog);
                                    View view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.activity_my_subscriptionplan, null);
                                    bottomSheetDialog.setContentView(view);
                                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    CM.HideProgressLoader();
                                    bottomSheetDialog.show();
                                    TextView planName = view.findViewById(R.id.planName);
                                    TextView cancelStatus = view.findViewById(R.id.cancelStatus);
                                    TextView planPrice = view.findViewById(R.id.planPrice);
                                    TextView endDate = view.findViewById(R.id.endDate);
                                    Button cancel = view.findViewById(R.id.cancel);
                                    Button Change = view.findViewById(R.id.Change);
                                    planName.setText(subscriptionPlanModel.getPlan_name());
                                    planPrice.setText(subscriptionPlanModel.getAmount() + "$/Month");
                                    endDate.setText(subscriptionPlanModel.getExpiry_date());


                                    if (subStatus.equals("cancel")) {
                                        //active
                                        cancel.setVisibility(View.GONE);
                                        Change.setText("Subscribe Plan");
                                        cancelStatus.setVisibility(View.VISIBLE);
                                    }


                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CancelSub(bottomSheetDialog);
                                        }
                                    });

//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                android.text.format.DateFormat df = new android.text.format.DateFormat();
//                                Date d = sdf.parse();
                                    endDate.setText(subscriptionPlanModel.getExpiry_date());
                                    Change.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            bottomSheetDialog.hide();
                                            Intent intent = new Intent(ProfileActivity.this, SubscriptionPlansListingScreen.class);
                                            startActivity(intent);
                                        }
                                    });

                                } else {

                                    Toast.makeText(ProfileActivity.this, response.getString("No plan found"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(ProfileActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }


    private void getCoins() {
        CM.showProgressLoader(ProfileActivity.this);
        StringRequest sr2 = new StringRequest(Request.Method.POST, Api.getcoins, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(Constants.TAG, String.valueOf(response));
                CM.HideProgressLoader();
                member_coins.setText(response + " pts");

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
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(sr2);

    }

    @Override
    public void onBackPressed() {
        setResult(CODE);
        super.onBackPressed();
    }

    void CancelSub(BottomSheetDialog bottomSheetDialog) {
        if (CM.isConnected(ProfileActivity.this)) {
            CM.showProgressLoader(ProfileActivity.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", user_id);
                object.put("subscriptions_id", subscription_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.cancelSubscribe, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e("cancelSubscribe", response.toString());
                                CM.HideProgressLoader();
                                if (response.getString("status").equals("success")) {
                                    myEdit.putString("status", "cancel");
                                    myEdit.apply();
                                    CODE = 100;
                                    bottomSheetDialog.hide();

                                    CM.ShowDialogueWithCustomAction(
                                            ProfileActivity.this,
                                            "Plan cancel successfully",
                                            "Ok", "", false,
                                            new DialogClickListener() {
                                                @Override
                                                public void onPositiveClick() {
//                                                show_my_plan.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onNegativeClick() {

                                                }
                                            }
                                    );

                                }

                            } catch (JSONException e) {
                                bottomSheetDialog.hide();
                                CM.HideProgressLoader();
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    bottomSheetDialog.hide();
                    VolleyLog.d("Error", "Error: " + error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(ProfileActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }
}
