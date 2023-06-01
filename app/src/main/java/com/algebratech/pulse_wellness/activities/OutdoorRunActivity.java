package com.algebratech.pulse_wellness.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.algebratech.pulse_wellness.Comment_activity;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.interfaces.ActivityCallback;
import com.algebratech.pulse_wellness.interfaces.addActivityApi;
import com.algebratech.pulse_wellness.models.commentModel;
import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.ImageUtil;
import com.algebratech.pulse_wellness.utils.IntentUtils;
import com.algebratech.pulse_wellness.utils.LocationManagerInterface;
import com.algebratech.pulse_wellness.utils.SmartLocationManager;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fxn.pix.Pix;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.listener.data.ISportModelOriginListener;
import com.veepoo.protocol.listener.data.ISportModelStateListener;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.HeartData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.SportModelOriginHeadData;
import com.veepoo.protocol.model.datas.SportModelOriginItemData;
import com.veepoo.protocol.model.datas.SportModelStateData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.model.settings.SportModelSetting;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.inuker.bluetooth.library.BluetoothService.getContext;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class
OutdoorRunActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, LocationManagerInterface, IBleWriteResponse {

    private static final int CAMERA_REQUEST = 9514;
    String poID;
    String userId;
    TextView tv;

    WriteResponse writeResponse = new WriteResponse();
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    private static final int REQUEST_LOCATION = 1;
    boolean isGPSEnable = false;
    LocationManager locationManager;
    Location location;
    TextView counter;
    Chronometer simpleChronometer;
    TextView kmCovered, disSteps, avHeart;
    CardView stop;
    boolean status;
    // private Timer myTimer, recordSteps;
    boolean isActive = false;


    private int deviceNumber = -1;
    private String deviceVersion;
    private String deviceTestVersion;
    VPOperateManager mVpoperateManager;

    int watchDataDay = 3;
    int contactMsgLength = 0;
    int allMsgLenght = 4;
    boolean isNewSportCalc = true;
    boolean isSleepPrecision = false;
    boolean isInitialised = false;
    String avarageHeartRate;

    int initSteps = 0;
    double initKcals = 0.0;
    double initDistance = 0.0;
    int finalSteps = 0;
    double finalKcals = 0.0;
    double finalDistance = 0.0;

    double activityDistance;
    double activityKcals;
    int activitySteps;
    String activityDuration;
    Intent intent;

    private GoogleMap mMap;
    //GoogleApiClient.Builder mGoogleApiClient;


    private ArrayList<LatLng> points; //added

    private Polyline polyline_path;


    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Dialog mLocationDialog;
    private String lati;
    private final String longi = "";
    double latitud;
    double longitud;
    double Current_latitud = 0.0;
    double Current_longitud = 0.0;
    SmartLocationManager mLocationManager;

    private Polyline line;
    private ArrayList<LatLng> routePoints;
    private LatLng latLng;
    private boolean drawlines = true;
    ImageView mapimg;

    private DBHelper db;
    String today;
    String path;
    //    ImageView camerarun;
    private TextToSpeech tts;
    private long COUNTER_TIME = 11000;
    private RelativeLayout taptostart;
    private CountDownTimer mainCounter;
    boolean doubleBackToExitPressedOnce = false;

    private ArrayList<String> returnValue = new ArrayList<String>();
    private String camera_file = "0";
    private String base64String = "";
    TextView stepss, distancee, caloriess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.outdoor_run_activity);
//        myTimer = new Timer();
//        recordSteps = new Timer();
        stepss = findViewById(R.id.steps);
        caloriess = findViewById(R.id.calories);
        distancee = findViewById(R.id.distance);
        status = false;
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        userId = sharedPreferences.getString("userID", "");
        SQLiteDatabase.loadLibs(getApplicationContext());
        db = new DBHelper(getApplicationContext());

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
        today = df.format(date);

        counter = findViewById(R.id.countdown);
        mapimg = findViewById(R.id.mapimg);
        simpleChronometer = findViewById(R.id.simpleChronometer);

        kmCovered = findViewById(R.id.kmCovered);
        stop = findViewById(R.id.stop);
        tv = findViewById(R.id.tv);
//        camerarun = findViewById(R.id.camerarun);
        taptostart = findViewById(R.id.relcount);

        registerReceiver(broadcastReceiver, new IntentFilter(DeviceConnect.BROADCAST_ACTION));
        if (!isMyServiceRunning(DeviceConnect.class)) {
            this.startService(intent);
        }


        mVpoperateManager = VPOperateManager.getMangerInstance(this.getApplicationContext());
        startCount();
        taptostart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainCounter.cancel();
                TTS("Activity started");
                counter.setVisibility(View.GONE);
                findViewById(R.id.relcount).setVisibility(View.GONE);
                getReadings();


            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                StopActivity();

            }
        });


//        camerarun.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Camera2Activity.class);
//                intent.putExtra(IntentUtils.CAMERA_VIEW_SHOW_PICK_IMAGE_BUTTON, true);
//                intent.putExtra(IntentUtils.IS_STATUS, true);
//                startActivityForResult(intent, CAMERA_REQUEST);
//
//            }
//        });


        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) {
            mapFrag.getMapAsync(this);
        }
        mLocationManager = new SmartLocationManager(getApplicationContext(),
                this,
                this,
                SmartLocationManager.ALL_PROVIDERS,
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                1 * 1000,
                1 * 1000,
                SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE);
        routePoints = new ArrayList<LatLng>();


    }


    void StopActivity() {

        drawlines = false;
        simpleChronometer.stop();
//                myTimer.cancel();
//                recordSteps.cancel();
        zoomToSeeWholeTrack();

        VPOperateManager.getMangerInstance(getApplicationContext()).stopSportModel(writeResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(SportModelStateData sportModelStateData) {

            }
        });

        VPOperateManager.getMangerInstance(getApplicationContext()).startDetectHeart(writeResponse, new IHeartDataListener() {

            @Override
            public void onDataChange(HeartData heartData) {

            }
        });

        final int elapsed = (int) (SystemClock.elapsedRealtime() - simpleChronometer.getBase());
        final int seconds = (elapsed / 1000) % 60;
        final int minutes = (elapsed / (1000 * 60)) % 60;
        final int hours = (elapsed / (1000 * 60 * 60)) % 24;

        final double Timeinseconds = StaticMethods.hoursToseconds(hours) + StaticMethods.munitesToseconds(minutes) + seconds;
        final double avaragePace = StaticMethods.roundTwoDecimals(StaticMethods.avaragePace(activityDistance, Timeinseconds));
        final double mt = StaticMethods.roundWholeNumber(avaragePace % 60);
        final int hr = StaticMethods.roundWholeNumber(avaragePace / 60);

        final String avgPace;

        if (hr > 0) {
            avgPace = "" + hr + "hr" + mt + "min";
        } else {
            avgPace = +mt + "min";
        }


        mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                Bitmap resize = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() - 50);

                base64String = ImageUtil.convert(resize);
                Log.e("Trynos=>Map", base64String);
//                        mapimg.setVisibility(View.VISIBLE);
//                        mapimg.setImageBitmap(ImageUtil.convert(base64String));
                // intent.putExtra("map",base64String);


            }
        });

        String timeTaken = "" + hours + ":" + minutes + ":" + seconds;
        //StaticMethods.showNotification(v, "Generating a report.....");
        if (!isActive) {

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String activityStartTime = df.format(date);


            myEdit.putString("Activity Name", "OutdoorRun");
            myEdit.putString("Activity Time", activityStartTime);
            myEdit.putString("Activity Status", "Ended");
            myEdit.apply();


            isActive = true;


            new addActivityApi().endActivityApi(userId, timeTaken, String.valueOf(activityDistance), String.valueOf(activityKcals), avarageHeartRate, String.valueOf(activitySteps), avgPace, camera_file, today, "Outdoor Running", OutdoorRunActivity.this, new ActivityCallback() {
                @Override
                public void success() {
                    try {
                        CM.HideProgressLoader();


                        db.insertActivities("Outdoor Running", userId, timeTaken, activitySteps,
                                String.valueOf(activityDistance), avgPace, avarageHeartRate,
                                String.valueOf(activityKcals), today, routePoints.toString(), base64String, camera_file, false);


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), RunResultsActivity.class);
                                intent.putExtra("timeTaken", "" + timeTaken);
                                intent.putExtra("distance", "" + activityDistance);
                                intent.putExtra("kcals", "" + activityKcals);
                                intent.putExtra("avarageHeartRate", "" + avarageHeartRate);
                                intent.putExtra("steps", "" + activitySteps);
                                intent.putExtra("avgPace", "" + avgPace);
                                intent.putExtra("camera_file", "" + camera_file);
                                intent.putExtra("act_id", today);
                                intent.putExtra("activity", "Outdoor Walking");
                                startActivity(intent);
                                finish();

                            }
                        }, 1000);


                    } catch (Exception e) {
                    }
                }

                @Override
                public void failure() {
                    CM.HideProgressLoader();
                    //TODO add dialog here to ask end activity again
                    StopActivity();
                }
            });

        }


        //findViewById(R.id.mapview).setVisibility(View.GONE);
    }


    private void zoomToSeeWholeTrack() {
//        val bounds = LatLngBounds.Builder()
//        for(polyline in pathPoints) {
//            for(pos in polyline) {
//                bounds.include(pos)
//                Log.d("Trynos",pos.toString());
//            }
//        }
//
//        map?.moveCamera(
//                CameraUpdateFactory.newLatLngBounds(
//                        bounds.build(),
//                        mapView.width,
//                        mapView.height,
//                        (mapView.height * 0.05f).toInt()
//                )
//        )

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        LatLngBounds.Builder builderOfBounds = new LatLngBounds.Builder();
        for (int z = 0; z < routePoints.size(); z++) {
            LatLng point = routePoints.get(z);
            builderOfBounds.include(point);
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builderOfBounds.build(), width, height, padding));

        mCurrLocationMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(routePoints.get(0));
        markerOptions.title("Start Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start));
        mGoogleMap.addMarker(markerOptions);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(routePoints.get(routePoints.size() - 1));
        markerOptions2.title("Finish Position");
        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_end));
        mGoogleMap.addMarker(markerOptions2);


    }


    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider) {


        if (drawlines) {


            if (mLocation.getLatitude() != Current_latitud && mLocation.getLongitude() != Current_longitud) {
                try {

                    Log.d("Trynos=>Location", Current_latitud + " -- " + Current_longitud);
                    Current_latitud = mLocation.getLatitude();
                    Current_longitud = mLocation.getLongitude();
                    latLng = new LatLng(Current_latitud, Current_longitud);
                    routePoints.add(latLng);
                    Log.d("Trynos=>LocationPoints", routePoints.toString());
                    PolylineOptions pOptions = new PolylineOptions()
                            .width(6)
                            .color(getResources().getColor(R.color.colorPrimary))
                            .geodesic(true);

                    for (int z = 0; z < routePoints.size(); z++) {
                        LatLng point = routePoints.get(z);
                        pOptions.add(point);
                    }

                    line = mGoogleMap.addPolyline(pOptions);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    routePoints.add(latLng);
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_mylocation));
                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


                } catch (Exception e) {
                    Log.e("Trynos=>Location", e.getMessage());
                }

            }


        }

    }

    @Override
    public void onResponse(int i) {

    }


    class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("write cmd status:" + code);

        }
    }

    private final IABleConnectStatusListener mBleConnectStatusListener = new IABleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == com.inuker.bluetooth.library.Constants.STATUS_CONNECTED) {
                Logger.t(TAG).i("STATUS_CONNECTED");
                Log.e("STATUS_CONNECTED", "STATUS_CONNECTED");
            } else if (status == com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED) {
                Logger.t(TAG).i("STATUS_DISCONNECTED");
                Log.e("STATUS_DISCONNECTED", "STATUS_DISCONNECTED");
            }
        }
    };

    private void getReadings() {
        //TODO this is for resume time testing if its work then use this in all activities
        Intent intent = getIntent();
        int hr = intent.getIntExtra("hour", -1);
        int min = intent.getIntExtra("min", -1);
        int sec = intent.getIntExtra("sec", -1);
        //initialise timer

        if (hr > -1 && min > -1 && sec > -1) {
            simpleChronometer.setBase(SystemClock.elapsedRealtime() - (hr * 3600000 + min * 60000 + sec * 1000));
        } else
            simpleChronometer.setBase(SystemClock.elapsedRealtime());


        simpleChronometer.setVisibility(View.VISIBLE);
        simpleChronometer.start();
        boolean is24Hourmodel = true;

        Date date = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String activityStartTime = String.valueOf(date.getTime());


        myEdit.putString("Activity Name", "OutdoorRun");
        myEdit.putString("Activity Time", activityStartTime);
        myEdit.putString("Activity Status", "Started");
        myEdit.apply();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                VPOperateManager.getMangerInstance(OutdoorRunActivity.this).readSportModelState(OutdoorRunActivity.this, new ISportModelStateListener() {


                    @Override
                    public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                        int SportsMode = sportModelStateData.getSportModeType();
                        if (SportsMode == 0) {
                            StopActivity();
                        }

                    }
                });


            }
        }, 0, 8000);

        VPOperateManager.getMangerInstance(this).confirmDevicePwd(writeResponse, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                String message = "PwdData:\n" + pwdData.toString();
                Logger.t(TAG).i(message);
                //  sendMsg(message, 1);

                deviceNumber = pwdData.getDeviceNumber();
                deviceVersion = pwdData.getDeviceVersion();
                deviceTestVersion = pwdData.getDeviceTestVersion();

            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                Logger.t(TAG).i(message);
                //  sendMsg(message, 2);
                EFunctionStatus newCalcSport = functionSupport.getNewCalcSport();
                if (newCalcSport != null && newCalcSport.equals(SUPPORT)) {
                    isNewSportCalc = true;
                } else {
                    isNewSportCalc = true;
                }
                watchDataDay = functionSupport.getWathcDay();
                contactMsgLength = functionSupport.getContactMsgLength();
                allMsgLenght = functionSupport.getAllMsgLength();
                isSleepPrecision = functionSupport.getPrecisionSleep() == SUPPORT;
            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                String message = "FunctionSocailMsgData:\n" + functionSocailMsgData.toString();
                Logger.t(TAG).i(message);
                //  sendMsg(message, 3);
            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                String message = "FunctionCustomSettingData:\n" + customSettingData.toString();
                Logger.t(TAG).i(message);
                //   sendMsg(message, 4);
            }
        }, "0000", is24Hourmodel);

        VPOperateManager.getMangerInstance(this).startDetectHeart(writeResponse, new IHeartDataListener() {
            @Override
            public void onDataChange(HeartData heartData) {

                try {
                    avarageHeartRate = String.valueOf(heartData.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        VPOperateManager.getMangerInstance(this).startMultSportModel(writeResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                Log.e("mode", String.valueOf(sportModelStateData.getSportModeType()));
            }
        }, new SportModelSetting(1));


        VPOperateManager.getMangerInstance(this).readSportModelState(writeResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                sportModelStateData.getSportModeType();
                Log.e("aaaaa", "aaaaaaaaaaaaa");
            }
        });


        VPOperateManager.getMangerInstance(this).startSportModel(writeResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(final SportModelStateData sportModelStateData) {
                VPOperateManager.getMangerInstance(getApplicationContext()).readSportModelOrigin(writeResponse, new ISportModelOriginListener() {
                    @Override
                    public void onReadOriginProgress(float v) {

                    }

                    @Override
                    public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

                    }

                    @Override
                    public void onHeadChangeListListener(final SportModelOriginHeadData sportModelOriginHeadData) {
                    }

                    @Override
                    public void onItemChangeListListener(List<SportModelOriginItemData> list) {

                    }

                    @Override
                    public void onReadOriginComplete() {

                    }
                });

            }
        });
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSmartWatchServeUI(intent);
        }
    };

    private void updateSmartWatchServeUI(Intent intent) {
        String steps = intent.getStringExtra("steps");
        String distances = intent.getStringExtra("distances");
        String kcals = intent.getStringExtra("kcals");
        String connect = intent.getStringExtra("connect");

        if (!connect.equals("connected")) {
            //TODO add sharedpre here
        }

        if (!isInitialised) {
            isInitialised = true;
            try {
                initSteps = Integer.parseInt(steps);
                initDistance = Double.parseDouble(distances);
                initKcals = Double.parseDouble(kcals);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {

            finalDistance = Double.parseDouble(distances);
            finalKcals = Double.parseDouble(kcals);
            finalSteps = Integer.parseInt(steps);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        activityDistance = StaticMethods.roundTwoDecimals(finalDistance - initDistance);
        activityKcals = StaticMethods.roundTwoDecimals(finalKcals - initKcals);
        activitySteps = finalSteps - initSteps;

        stepss.setText(String.valueOf(activitySteps));

        String tempkcal = String.format("%.2f", activityKcals);
        String tempdis = String.format("%.2f", activityDistance);
        caloriess.setText(tempkcal);
        distancee.setText(tempdis);

        myEdit.putString("Steps", String.valueOf(activitySteps));
        myEdit.putString("Kcals", kcals);
        myEdit.putString("Distance", distances);
        myEdit.apply();


        if (connect.contains("reco")) {
            //   showConnectionDialog("Reconecting to wearable");
        }
        if (connect.contains("dis")) {
            //   showConnectionDialog("Disconnected");

            //  profile_image.setBorderColor(ResourcesCompat.getColor(mContext.getResources(), R.color.red, null));
        }
        if (connect.contains("connecting")) {

            //   showConnectionDialog("Connected to V19");
            //  profile_image.setBorderColor(ResourcesCompat.getColor(mContext.getResources(), R.color.orange, null));
            //showdialog();
        }

    }

    private void showConnectionDialog(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startCount() {
        counter.setVisibility(View.VISIBLE);
        findViewById(R.id.relcount).setVisibility(View.VISIBLE);
        mainCounter = new CountDownTimer(COUNTER_TIME, 1000) {

            public void onTick(long millisUntilFinished) {

                COUNTER_TIME = millisUntilFinished;
                final String downn = String.valueOf(millisUntilFinished / 1000);
                final int testdown = (int) (millisUntilFinished / 1000);
                counter.setText(downn);

                if (testdown <= 7 && testdown != 0) {
                    Log.e("onTick++++", "TIMER_DOWN");
                    TTS(downn);
                } else if (testdown == 10) {
                    Log.e("onTick++++", "TIMER_START");
                    TTS("Your activity starts in");
                } else if (testdown == 0) {
                    Log.e("onTick++++", "TIMER_CLOSED");
                    TTS("Activity started");
                    counter.setVisibility(View.GONE);
                    findViewById(R.id.relcount).setVisibility(View.GONE);
                    getReadings();

                }

            }

            public void onFinish() {

                //    kmCovered.setVisibility(View.GONE);
                //simpleChronometer.setVisibility(View.VISIBLE);
                //simpleChronometer.setFormat("HH:MM:SS");

//                mDatabase = FirebaseDatabase.getInstance().getReference("WalkingActivity").child(userId);
//                poID = mDatabase.push().getKey();
            }
        }.start();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void TTS(final String data) {
        tts = new TextToSpeech(OutdoorRunActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.UK);
                    tts.speak(data, 1, null);
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // mGoogleMap.setMaxZoomPreference(7);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(false);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }


    @Override
    public void onLocationChanged(Location location) {


//        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
//
//        latitud = location.getLatitude();
//        longitud = location.getLongitude();
//        //Place current location marker
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.footprints));
//        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//        //move map camera
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // path = data.getStringExtra(IntentUtils.EXTRA_PATH_RESULT);
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
        } catch (Exception e) {
            Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
        }


        try {

            if (!returnValue.isEmpty()) {

                Log.e(Constants.TAG + "PATH", returnValue.get(0));

                File f = new File(returnValue.get(0));
                int len = f.getAbsolutePath().length();
                String extension = String.valueOf(f.getAbsolutePath().subSequence(len - 3, len));


                if (extension.equals("mp4") | extension.equals("mkv")) {
                    camera_file = "0";
                    Toast.makeText(OutdoorRunActivity.this, "Please use a image , not a video.", Toast.LENGTH_LONG).show();
                }

                if (extension.equals("jpg") | extension.equals("png")) {
                    camera_file = f.getAbsolutePath();
                }


            }

        } catch (Exception e) {
            Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
            Toast.makeText(OutdoorRunActivity.this, "Please use a image , not a video.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            TTS("Activty cancel");
            finish();
            stopActivity();
            return;
        }
        TTS("Press back twice to cancel Activty");
        Toast.makeText(getApplicationContext(), "Press back twice to cancel Activty", Toast.LENGTH_LONG).show();
        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 2000);
    }


    private void stopActivity() {

        VPOperateManager.getMangerInstance(getApplicationContext()).stopSportModel(writeResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(SportModelStateData sportModelStateData) {
            }
        });

    }


}


