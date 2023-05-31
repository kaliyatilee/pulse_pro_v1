package com.algebratech.pulse_wellness.activities;

import static android.bluetooth.BluetoothProfile.GATT;
import static com.algebratech.pulse_wellness.services.DeviceConnect.BROADCAST_ACTION;
import static com.algebratech.pulse_wellness.utils.Constants.TAG;
import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.algebratech.pulse_wellness.FragmentTabLeaderboardFriendlist;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.gridActivityAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.fragment_tab_newsfeed_dashboard;
import com.algebratech.pulse_wellness.fragments.CommunityFragment;
import com.algebratech.pulse_wellness.fragments.RewardsFragment;
import com.algebratech.pulse_wellness.models.WeightMonitoringModel;
import com.algebratech.pulse_wellness.models.gridActivityModel;
import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.services.DeviceSyncService;
import com.algebratech.pulse_wellness.services.MyFirebaseInstanceIDService;
import com.algebratech.pulse_wellness.services.NotificationService;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.stripe.android.PaymentConfiguration;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBeginPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBpListItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBpListPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerPrivateBpPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerRateItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerRateListPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSportItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSportPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerStepItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerStepPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerTodaySumSportPacket;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.db.HrpDataDao;
import com.wosmart.ukprotocollibary.model.hrp.HrpData;
import com.wosmart.ukprotocollibary.model.sleep.SleepData;
import com.wosmart.ukprotocollibary.model.sport.SportData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUESTCODE_TURNON_GPS = 199;
    private FragmentTransaction fragmentTransaction;
    TextView tvUsername;
    static BottomNavigationView bottomNavigationView;
    BottomNavigationView navView;
    private RelativeLayout qr_scan;

    FragmentManager FM;
    FragmentTransaction FT;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    Intent intent;
    ImageView watch_icon;

    String hasMac, userId;
    boolean doubleBackToExitPressedOnce = false;
    Dialog dialog;
    private AppUpdateManager mAppUpdateManager;
    private static final int MY_REQUEST_CODE = 444;
    private FloatingActionButton fab;
    private ImageView notifications, search;
    LinearLayout bottomSheetParent;
    BottomSheetBehavior mBottomSheetBehaviour;
    boolean isActivityReady = false;
    boolean isActivityClicked = false;
    String clickedActivity,clickedActivityCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, NotificationService.class));
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        notifications = findViewById(R.id.notifications);
        search = findViewById(R.id.search);
        bottomSheetParent = findViewById(R.id.bottom_sheet_parent);

        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetParent);
        mBottomSheetBehaviour.setPeekHeight(0);


        GridView gridView = bottomSheetParent.findViewById(R.id.gridView);

        ArrayList<gridActivityModel> gridActivityModels = new ArrayList<gridActivityModel>();
        gridActivityModels.add(new gridActivityModel("Outdoor Run", R.drawable.runningg));
        gridActivityModels.add(new gridActivityModel("Outdoor Walk", R.drawable.walking));
        gridActivityModels.add(new gridActivityModel("Indoor Run", R.drawable.runningg));
        gridActivityModels.add(new gridActivityModel("Indoor Walk", R.drawable.walking));
        gridActivityModels.add(new gridActivityModel("Hiking", R.drawable.hiking));
        gridActivityModels.add(new gridActivityModel("Stair Stepper", R.drawable.stair_stepper));
        gridActivityModels.add(new gridActivityModel("Outdoor Cycle", R.drawable.cycling));
        gridActivityModels.add(new gridActivityModel("Stationary Bike", R.drawable.stationary_bike));
        gridActivityModels.add(new gridActivityModel("Elliptical", R.drawable.elliptical_workout));
        gridActivityModels.add(new gridActivityModel("Rowing Machine", R.drawable.rowing_machine));

        gridActivityAdapter adapter = new gridActivityAdapter(this, gridActivityModels);
        gridView.setAdapter(adapter);


        registerReceiver(broadcastReceiver, new IntentFilter(DeviceConnect.BROADCAST_ACTION));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (hasMac == null) {
                    Toast.makeText(MainActivity.this, "Please connect your wearable device", Toast.LENGTH_SHORT).show();
                    return;
                }


                BluetoothManager btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                List<BluetoothDevice> connectedDevices = btManager.getConnectedDevices(GATT);
                boolean isConnected = false;
                if (connectedDevices != null) {
                    for (int j = 0; j < connectedDevices.size(); j++) {
                        if (connectedDevices.get(j).getAddress().equalsIgnoreCase(hasMac)) {
                            isConnected = true;
                            break;
                        }
                    }
                }

                if(!WristbandManager.getInstance(MainActivity.this).isConnect()){
                    Toast.makeText(MainActivity.this, "Please connect your wearable device", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isConnected) {
                    Toast.makeText(MainActivity.this, "Please connect your wearable device", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (i == 0) {
                    //here
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Outdoor Run");
                        intent.putExtra("activityCode", "1");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Outdoor Run";
                        clickedActivityCode = "1";
                    }



                } else if (i == 1) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Outdoor Walk");
                        intent.putExtra("activityCode", "2");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Outdoor Walk";
                        clickedActivityCode = "2";
                    }
                } else if (i == 2) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Intent intent = new Intent(MainActivity.this, IndoorRunActivity.class);
//                    startActivity(intent);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Indoor Run");
                        intent.putExtra("activityCode", "3");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Indoor Run";
                        clickedActivityCode = "3";
                    }


                } else if (i == 3) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Intent intent = new Intent(MainActivity.this, IndoorWalkActivity.class);
//                    startActivity(intent);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Indoor Walk");
                        intent.putExtra("activityCode", "4");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Indoor Walk";
                        clickedActivityCode = "4";
                    }
                } else if (i == 4) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Intent intent = new Intent(MainActivity.this, HikingActivity.class);
//                    startActivity(intent);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Hiking");
                        intent.putExtra("activityCode", "5");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Hiking";
                        clickedActivityCode = "5";
                    }
//                    Intent intent = new Intent(MainActivity.this, WatchActivities.class);
//                    intent.putExtra("activity", "Hiking");
//                    intent.putExtra("activityCode", "5");
//                    startActivity(intent);
                } else if (i == 5) {
//                    Intent intent = new Intent(MainActivity.this, StairStepperActivity.class);
//                    startActivity(intent);
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Stair Stepper");
                        intent.putExtra("activityCode", "6");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Stair Stepper";
                        clickedActivityCode = "6";
                    }
//                    Intent intent = new Intent(MainActivity.this, WatchActivities.class);
//                    intent.putExtra("activity", "Stair Stepper");
//                    intent.putExtra("activityCode", "6");
//                    startActivity(intent);
                } else if (i == 6) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Intent intent = new Intent(MainActivity.this, OutdoorCyclingActivity.class);
//                    startActivity(intent);
//                    Intent intent = new Intent(MainActivity.this, WatchActivities.class);
//                    intent.putExtra("activity", "Outdoor Cycle");
//                    intent.putExtra("activityCode", "7");
//                    startActivity(intent);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Outdoor Cycle");
                        intent.putExtra("activityCode", "7");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Outdoor Cycle";
                        clickedActivityCode = "7";
                    }
                } else if (i == 7) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Intent intent = new Intent(MainActivity.this, StationaryBikeActivity.class);
//                    startActivity(intent);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Stationary Bike");
                        intent.putExtra("activityCode", "8");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Stationary Bike";
                        clickedActivityCode = "8";
                    }
//                    Intent intent = new Intent(MainActivity.this, WatchActivities.class);
//                    intent.putExtra("activity", "Stationary Bike");
//                    intent.putExtra("activityCode", "8");
//                    startActivity(intent);
                } else if (i == 8) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Intent intent = new Intent(MainActivity.this, TreadmillActivity.class);
//                    startActivity(intent);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Treadmill");
                        intent.putExtra("activityCode", "9");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Treadmill";
                        clickedActivityCode = "9";
                    }
//                    Intent intent = new Intent(MainActivity.this, WatchActivities.class);
//                    intent.putExtra("activity", "Treadmill");
//                    intent.putExtra("activityCode", "9");
//                    startActivity(intent);
                } else if (i == 9) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Intent intent = new Intent(MainActivity.this, RowingMachineActivity.class);
//                    startActivity(intent);
                    if(isActivityReady) {
                        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
                        intent.putExtra("activity", "Rowing Machine");
                        intent.putExtra("activityCode", "10");
                        startActivity(intent);
                    }
                    else{
                        CM.showProgressLoader(MainActivity.this);
                        isActivityClicked = true;
                        clickedActivity = "Rowing Machine";
                        clickedActivityCode = "1o";
                    }
//                    Intent intent = new Intent(MainActivity.this, WatchActivities.class);
//                    intent.putExtra("activity", "Rowing Machine");
//                    intent.putExtra("activityCode", "10");
//                    startActivity(intent);
                }
            }
        });


        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                intent.putExtra("fcm", "");
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddFriendActivity.class);
                startActivity(intent);
            }
        });
//        Log.e("Refreshed token", "Refreshed token: " + refreshedToken);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
//                Intent intent = new Intent(MainActivity.this, gridDemo.class);
//                startActivity(intent);
            }
        });

        try {

            mAppUpdateManager = AppUpdateManagerFactory.create(this);
            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo result) {

                    if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        try {
                            mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, MY_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


        //activityOptions();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new fragment_tab_newsfeed_dashboard());
        fragmentTransaction.commit();

        tvUsername = findViewById(R.id.username);
        bottomNavigationView = findViewById(R.id.nav_view);
        //  qr_scan = findViewById(R.id.qr_scan);
        watch_icon = findViewById(R.id.watch_icon);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        //showBadge(this, R.id.navigation_dashboard, "1");

        //Notification Intent

        try {

            String notiIntent = getIntent().getExtras().getString("frgToLoad");

            switch (notiIntent) {
                case "friendrequest":
                    bottomNavigationView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
                    FM = getSupportFragmentManager();
                    FT = FM.beginTransaction();
                    FT.replace(R.id.content_frame, new CommunityFragment()).commit();
                    FM.popBackStack();
                    break;
            }

        } catch (Exception e) {

        }

        //Bluetooth Listener

        dialog = new Dialog(MainActivity.this, R.style.ConnectivityDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_connectivity);

        Button btndialog_act = dialog.findViewById(R.id.connectact);
        btndialog_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                turnGPSOn();

            }
        });


        if (isBluetoothEnabled()) {
            Log.d(TAG + "Bluetooth", "Bluetooth on...");
            dialog.dismiss();
        } else {
            Log.d(TAG + "Bluetooth", "Bluetooth off...");
            dialog.show();
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);


        //SharePref
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        userId = sharedPreferences.getString("userID", null);

        hasMac = sharedPreferences.getString("macAddress", null);

        bottomNavigationView.getMenu().findItem(R.id.newsFeed).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListerner);

        syncProfile();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            this.startForegroundService ( serviceIntent );
//        } else {

//        }
//        Intent deviceConnect = new Intent(this, DeviceSyncService.class);
//        startService(deviceConnect);
        //  ContextCompat.startForegroundService(MainActivity.this,deviceConnect);

         if (hasMac != null && !hasMac.equals("0")) {
                  Intent serviceIntent1 = new Intent(this, DeviceSyncService.class);
                  this.startService(serviceIntent1);

          }


        watch_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                Intent intent = new Intent(MainActivity.this, ScanWearableActivity.class);
                startActivity(intent);
            }
        });


        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51KBMBgKQDMir4i4yeXf9G9kpdfvPXorAWuzlLcfj0kAXXgmsnlCPbBP3P9L2Tx7OIbhd80NDzGtm5CQxeB3a3xDq00WXI32uB4"
        );


    }

    public void syncProfile() {
//        tvUsername.setText(sharedPreferences.getString("firstname", null));
//        Glide.with(getApplicationContext()).load(sharedPreferences.getString("profileURL", null)).error(R.drawable.placeholder).into(profile_image);
        try {
            syncProfileOnline();
        } catch (Exception e) {

        }


    }

    private void syncProfileOnline() {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.profsync, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, String.valueOf(response));

                        try {

                            if (response.getString("status").equals("success")) {

                                JSONObject userData = (JSONObject) response.get("user");

                                myEdit.putString("Pulse_Points", userData.getString("loyaltpoints"));
                                myEdit.putString("macAddress", userData.getString("macAddress"));
                                myEdit.putString("fullname", userData.getString("firstname") + " " + userData.getString("lastname"));
                                myEdit.putString("firstname", userData.getString("firstname"));
                                myEdit.putString("lastname", userData.getString("lastname"));
                                myEdit.putString("dob", userData.getString("dob"));
                                myEdit.putString("bmi", userData.getString("bmi"));
                                myEdit.putString("weight", userData.getString("weight"));
                                myEdit.putString("height", userData.getString("height"));
                                myEdit.putString("gender", userData.getString("gender"));
                                myEdit.putString("country", userData.getString("country"));
                                myEdit.putString("phone", userData.getString("phone"));
                                myEdit.putString("profileURL", userData.getString("profileurl"));
                                myEdit.putString("dateCreated", userData.getString("created_at"));
                                myEdit.putString("sub_expire", userData.getString("sub_expire"));
                                myEdit.putString("master_target", "300");
                                //myEdit.putString("master_target", userData.getString("master_target"));
                                myEdit.putString("email", userData.getString("email"));
                                myEdit.putString("wthRatio", userData.getString("wthRatio"));
                                myEdit.putString("hba1c", userData.getString("hba1c"));
                                myEdit.putString("bp", userData.getString("bp"));
                                myEdit.apply();

                            } else {


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListerner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selecteFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:

                            Log.e("IDIGIT_21_07_View", "HOME ICON CLICKED");

                            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            FM = getSupportFragmentManager();
                            FT = FM.beginTransaction();
                            FT.replace(R.id.content_frame, new fragment_tab_newsfeed_dashboard()).commit();
                            FM.popBackStack();
                            // startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

                            break;
                        case R.id.navigation_dashboard:
                            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                            myEdit.putInt("friendreq_not", 1);
//                            myEdit.remove("friendreq_not");
                            myEdit.apply();
                            FM = getSupportFragmentManager();
                            FT = FM.beginTransaction();
                            FT.replace(R.id.content_frame, new FragmentTabLeaderboardFriendlist()).commit();
                            FM.popBackStack();
                            //   CommunityFragment
                            break;
                        case R.id.nav_profile:
                            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            FM = getSupportFragmentManager();
                            FT = FM.beginTransaction();
                            FT.replace(R.id.content_frame, new SettingFragment()).commit();
                            FM.popBackStack();

                            break;

                        case R.id.store:
                            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            FM = getSupportFragmentManager();
                            FT = FM.beginTransaction();
                            FT.replace(R.id.content_frame, new RewardsFragment()).commit();
                            FM.popBackStack();
                            break;

                        case R.id.newsFeed:
                            Log.e("IDIGIT_21_07_View", "HOME ICON CLICKED");
                            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            FM = getSupportFragmentManager();
                            FT = FM.beginTransaction();
                            FT.replace(R.id.content_frame, new fragment_tab_newsfeed_dashboard()).commit();
                            FM.popBackStack();
                            break;

                    }
                    // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selecteFragment).commit();
                    return true;
                }
            };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void activityOptions() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_connectivity, null);
        //mBuilder.setIcon(R.drawable.logo);
        mBuilder.setView(mView);
        //mBuilder.setTitle("Select Activity");
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }
        Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("IDIGIT_21_07_RECEIVE", "Main Activity");
            updateSmartWatchServeUI(intent);
        }
    };

    private void updateSmartWatchServeUI(Intent intent) {
        String steps = intent.getStringExtra("steps");
        String distances = intent.getStringExtra("distance");
        String kcals = intent.getStringExtra("kcals");
        String connect = intent.getStringExtra("connect");


        Log.d(TAG,"MainActivities STEPS"+steps);
        Log.d(TAG,"MainActivities distances"+distances);
        if (!(distances == null)){
            isActivityReady = true;
        }
        else{
            isActivityReady = false;
        }

        if (isActivityReady){
            if (isActivityClicked) {
                CM.HideProgressLoader();
                Intent intent1 = new Intent(MainActivity.this, WatchActivities.class);
                intent1.putExtra("activity", clickedActivity);
                intent1.putExtra("activityCode", clickedActivityCode);
                startActivity(intent);
            }

        }


        Log.d(TAG,connect);

        if (connect.contains("connected")) {
            Log.e("IDIGIT_21_07_CONNECTED", "DEVICE IS CONNECTED");
            //profile_image.setBorderColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            watch_icon.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.green, null));
             //hidedialog();
            //  successDialog();
            //checkForResumeActivity();
        }
        if (connect.contains("reco")) {
            //profile_image.setBorderColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
            watch_icon.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.green, null));
            //hidedialog();
        }
        if (connect.contains("dis")) {
            //profile_image.setBorderColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
            watch_icon.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.red, null));
        }
        if (connect.contains("connecting")) {
            //profile_image.setBorderColor(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            watch_icon.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.orange, null));
            //showdialog();
        }

    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            try {
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG + "Bluetooth", "Bluetooth off...");
                            dialog.show();
                            //setButtonText("Bluetooth off");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d(TAG + "Bluetooth", "Turning Bluetooth off...");
                            //setButtonText("Turning Bluetooth off...");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG + "Bluetooth", "Bluetooth on...");
                            dialog.dismiss();
                            //setButtonText("Bluetooth on");
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG + "Bluetooth", "Turning Bluetooth on...");
                            //setButtonText("Turning Bluetooth on...");
                            break;
                    }
                }
            } catch (Exception e) {
                //  Log.e("Trynos Bluetooth Dia",e.getMessage());
            }


        }
    };

    private void turnGPSOn() {
        LocationRequest locationRequest = LocationRequest.create();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d("GPS_main", "OnSuccess");
                // GPS is ON
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Log.d("GPS_main", "GPS off");
                // GPS off
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MainActivity.this, REQUESTCODE_TURNON_GPS);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode != RESULT_OK) {
            //Toast.makeText(this,"cancel",Toast.LENGTH_LONG).show();
        }
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            dialog.dismiss();
        }

        if (requestCode == REQUESTCODE_TURNON_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // GPS was turned on;
                    dialog.dismiss();
                    break;
                case Activity.RESULT_CANCELED:
                    // User rejected turning on the GPS
                    dialog.show();
                    break;
                default:
                    break;
            }
        }

    }


    public static void showBadge(Context context,
                                 @IdRes int itemId, String value) {
        if (!value.equals("0")) {
            removeBadge(itemId);
            BottomNavigationItemView itemView = MainActivity.bottomNavigationView.findViewById(itemId);
            View badge = LayoutInflater.from(context).inflate(R.layout.badge_view, bottomNavigationView, false);

            TextView text = badge.findViewById(R.id.notifications_badge);
            text.setText(value);
            itemView.addView(badge);
        }
    }

    public static void removeBadge(@IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    void checkForResumeActivity() {
        Log.e("Started and dis", "Started and disconnected");
        String activityStatus = sharedPreferences.getString("Activity Status", "");
        Log.e(activityStatus.toString(), activityStatus.toString());
        if (activityStatus.equals("Started")) {
            Log.e("Started and dis", "Started and disconnected");
            String activityTime = sharedPreferences.getString("Activity Time", "");
            String activityName = sharedPreferences.getString("Activity Name", "");
            String activityCode = sharedPreferences.getString("Activity Code", "");

            androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("You started " + activityName + "Would you like to resume this activity?");
            builder1.setCancelable(true);

            Log.e("RESUME", "RESUMEMAIN");
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            ResumeActivity(activityName, activityTime, activityCode);
                        }
                    });

            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    myEdit.putString("Activity Status", "Ended");
                    myEdit.apply();
                }
            });
            androidx.appcompat.app.AlertDialog alert11 = builder1.create();
            alert11.show();

        } else
            Log.e("Started and dis", activityStatus);

    }

    void ResumeActivity(String name, String time, String code) {


        Long Time = Long.valueOf(time);

        //
//        Long temp = Calendar.getInstance().getTime().getTime() - Time;
//
//        final int seconds = (int) (temp / 1000) % 60;
//        final int minutes = (int) ((temp / (1000 * 60)) % 60);
//        final int hours = (int) ((temp / (1000 * 60 * 60)) % 24);
//

        //here
        // if (Objects.equals(name, "Outdoor Run")) {
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(MainActivity.this, WatchActivities.class);
        intent.putExtra("activity", name);
        intent.putExtra("activityCode", code);
        intent.putExtra("isResume", true);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
        intent.putExtra("Time", Time);
        startActivity(intent);
        //   }
//        else if (Objects.equals(name, "Outdoor Walk")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, IndoorRunActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Indoor Run")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, HikingActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Indoor Walk")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, IndoorWalkActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Hiking")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, OutdoorCyclingActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Stair Stepper")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, OutdoorWalkingActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Outdoor Cycle")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, RowingMachineActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Stationary Bike")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, StairStepperActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Treadmill")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, StationaryBikeActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        } else if (Objects.equals(name, "Rowing Machine")) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, TreadmillActivity.class);
//            intent.putExtra("hour", hours);
//            intent.putExtra("min", minutes);
//            intent.putExtra("sec", seconds);
//            startActivity(intent);
//        }


        //        else if (i == 1) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, OutdoorWalkingActivity.class);
//            startActivity(intent);
//        } else if (i == 2) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, IndoorRunActivity.class);
//            startActivity(intent);} else if (i == 3) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, IndoorWalkActivity.class);
//            startActivity(intent);
//        } else if (i == 4) {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, HikingActivity.class);
//            startActivity(intent);
//        } else if (i == 5)
//        {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, StairStepperActivity.class);
//            startActivity(intent);
//        } else if (i == 6)
//        {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, OutdoorCyclingActivity.class);
//            startActivity(intent);
//        } else if (i == 7)
//        {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, StationaryBikeActivity.class);
//            startActivity(intent);
//        } else if (i == 8)
//        {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, TreadmillActivity.class);
//            startActivity(intent);
//        } else if (i == 9)
//        {
//            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            Intent intent = new Intent(MainActivity.this, RowingMachineActivity.class);
//            startActivity(intent);
//        }
    }

    private void showBottomSheetDialog() {
        if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {

            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }


    }

//
}
