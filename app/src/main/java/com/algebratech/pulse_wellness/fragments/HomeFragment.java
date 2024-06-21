package com.algebratech.pulse_wellness.fragments;

import static android.bluetooth.BluetoothProfile.GATT;
import static android.content.Context.BLUETOOTH_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import static com.inuker.bluetooth.library.utils.BluetoothUtils.registerReceiver;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.FunctionsActivity;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.AddFriendActivity;
import com.algebratech.pulse_wellness.activities.AddUserGoals;
import com.algebratech.pulse_wellness.activities.DetailActivitySummary;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.activities.ScanActivity;
import com.algebratech.pulse_wellness.activities.SelectDisease;
import com.algebratech.pulse_wellness.activities.WeightMonitoring;
import com.algebratech.pulse_wellness.adapters.ActivitiesSummaryAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.dashboardReports.HeartRateActivity;
import com.algebratech.pulse_wellness.dashboardReports.SleepActivity;
import com.algebratech.pulse_wellness.dashboardReports.StepsActivity;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.SlidingModel;
import com.algebratech.pulse_wellness.models.TodaysActivityModel;
import com.algebratech.pulse_wellness.models.WeightMonitoringModel;
import com.algebratech.pulse_wellness.models.WellnessPlanModel;
import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.services.DeviceSyncService;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.BarEntry;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.veepoo.protocol.VPOperateManager;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerPrivateBpPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerTemperatureControlPacket;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.sport.SportData;


import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ArrayList<SlidingModel> imageModelArrayList;
    private static final String TAG = "AlgosecServiceLog";
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private Intent intent,intent2;
    private int[] myImageList = new int[]{R.drawable.banner_1, R.drawable.banner_calculator, R.drawable.banner_3};
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    Context mContext;
    private BluetoothManager mBManager;
    private BluetoothAdapter mBAdapter;
    private BluetoothLeScanner mBScanner;
    TextView stepsTextview, txtWalk, distance,latestHeart,avarageHeart;
    ProgressBar progressBar, runningProgress, myPlanKcalPro, myPlanKMPro, weightPro, kcalPro, stepsPro;
    private final int REQUEST_CODE = 1;
    VPOperateManager mVpoperateManager;
    private int deviceNumber = -1;
    Boolean isdeviceConnected = false;
    ProgressBar progress_bar;
    //database helper object
    private DBHelper dbHelper;
    private Cursor c;
    View root;
    private String amount, today;
    int progress = 30;
    private List<WellnessPlanModel> wellnessPlanModels = new ArrayList<>();
    String userId;
    Button addGoals;
    TextView total_lose_weight, total_running, total_calories, total_steps ;
   // TextView bpReading;
    LinearLayout userGoals;
    TextView currentSteps, currentKcal, currentRunning,tmpCals,tmpSteps,tmpDistance,tvDeepSleep,progresPer;
    String steps, distances, kcals, hasMac;
    String sum_of_calories_for_day = "0";
    String sum_of_steps_for_day = "0";
    String sum_of_distance_for_day = "0";
    RecyclerView activitiesSummary;
    private ActivitiesSummaryAdapter activitiesSummaryAdapter;
    private RecyclerView.Adapter mAdapter;
    TextView seeAll,currentWeight,tmpReading,weightDate,weightMessage;
    CardView cardWeight,stepsCard,cardSleep,cardHeart;
    List<TodaysActivityModel> todaysActivityModels = new ArrayList<>();
    Intent intent1;
    String today_kcals,today_distance,today_steps,deepSleep,tmp_original,sync_status;


    TextView myPLanKcal, bmi, bmi_text, myPLanCurrentKcal, myPlanKm, weeklyKm, addGoalText, goalWeight, goalKcal, goalSteps, myPlanKcalProText, myPlanKMProText, weightProText, runningProgressText, stepsProText, kcalProText, noActvity;
    TextView txtPlanStatus,txtDistancePlanStatus,syncStatus;
    boolean onFragment = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e("IDIGIT_21_07_View", "HOME CLASS CALLED");
        super.onViewCreated(view, savedInstanceState);
       // db = new DBHelper(getContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
                onFragment = true;
                DashBoardAPI();
              //  initData();
            }
        }, 500);


    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    void init() {

        //readStepLocal();
        if (root != null && getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
            myEdit = sharedPreferences.edit();
            userId = sharedPreferences.getString("userID", null);
            hasMac = sharedPreferences.getString("macAddress", null);
            isdeviceConnected = false;
            mContext = getContext();
            intent = new Intent(mContext, DeviceConnect.class);
            intent2 = new Intent(mContext, DeviceSyncService.class);
            progressBar = root.findViewById(R.id.progressBar);
            runningProgress = root.findViewById(R.id.runningProgress);
            addGoals = root.findViewById(R.id.addGoals);
            noActvity = root.findViewById(R.id.noActvity);
            cardWeight = root.findViewById(R.id.cardWeight);
            stepsCard = root.findViewById(R.id.stepsCard);
            progress_bar = root.findViewById(R.id.progress_bar);
            syncStatus = root.findViewById(R.id.syncStatus);

            distance = root.findViewById(R.id.distance);
            tmpReading = root.findViewById(R.id.tmpReading);

            mVpoperateManager = mVpoperateManager.getMangerInstance(mContext.getApplicationContext());
            imageModelArrayList = new ArrayList<>();
            imageModelArrayList = populateList();
            NUM_PAGES = imageModelArrayList.size();

            currentSteps = root.findViewById(R.id.currentSteps);
            currentKcal = root.findViewById(R.id.currentKcal);
            currentRunning = root.findViewById(R.id.currentRunning);
            total_lose_weight = root.findViewById(R.id.total_lose_weight);
            total_running = root.findViewById(R.id.total_running);
          //  bpReading = root.findViewById(R.id.bpReading);
            cardSleep = root.findViewById(R.id.cardSleep);
            cardHeart = root.findViewById(R.id.cardHeart);
            currentWeight = root.findViewById(R.id.currentWeight);
            total_calories = root.findViewById(R.id.total_calories);
            total_steps = root.findViewById(R.id.total_steps);
            bmi = root.findViewById(R.id.bmi);
            txtPlanStatus = root.findViewById(R.id.txtPlanStatus);
            txtDistancePlanStatus = root.findViewById(R.id.txtDistancePlanStatus);
            bmi_text = root.findViewById(R.id.bmi_text);
            myPLanKcal = root.findViewById(R.id.myPLanKcal);
            myPLanCurrentKcal = root.findViewById(R.id.weeklyKcal);
            myPlanKm = root.findViewById(R.id.myPlanKm);
            weeklyKm = root.findViewById(R.id.weeklyKm);
            addGoalText = root.findViewById(R.id.addGoalText);
            goalWeight = root.findViewById(R.id.goalWeight);
            goalKcal = root.findViewById(R.id.goalKcal);
            goalSteps = root.findViewById(R.id.goalSteps);
            myPlanKcalPro = root.findViewById(R.id.myPlanKcalPro);
            myPlanKcalProText = root.findViewById(R.id.myPlanKcalProText);
            myPlanKMProText = root.findViewById(R.id.myPlanKMProText);
            myPlanKMPro = root.findViewById(R.id.myPlanKMPro);
            weightPro = root.findViewById(R.id.weightPro);
            kcalPro = root.findViewById(R.id.kcalPro);
            stepsPro = root.findViewById(R.id.stepsPro);

            weightProText = root.findViewById(R.id.weightProText);
            runningProgressText = root.findViewById(R.id.runningProgressText);
            stepsProText = root.findViewById(R.id.stepsProText);
            kcalProText = root.findViewById(R.id.kcalProText);
            tmpSteps = root.findViewById(R.id.tmpSteps);
            tmpCals = root.findViewById(R.id.tmpCals);
            tmpDistance = root.findViewById(R.id.tmpDistance);
            tvDeepSleep = root.findViewById(R.id.tvDeepSleep);

            userGoals = root.findViewById(R.id.userGoals);
            activitiesSummary = root.findViewById(R.id.activitiesSummary);
            seeAll = root.findViewById(R.id.seeAll);
            avarageHeart = root.findViewById(R.id.avarageHeart);
            weightDate = root.findViewById(R.id.weightDate);
            progresPer = root.findViewById(R.id.progresPer);
            weightMessage = root.findViewById(R.id.weightMessage);
            weightMessage.setVisibility(View.GONE);


             BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                     today_kcals = intent.getStringExtra("kcals");
                     today_distance = intent.getStringExtra("distance");
                     sync_status = intent.getStringExtra("sync_status");


                     try {
                         deepSleep = intent.getStringExtra("deepSleep");
                         tvDeepSleep.setText("Deep Sleep :"+deepSleep +" Hrs");
                     }
                     catch (Exception e){

                     }

                     today_steps = intent.getStringExtra("steps");
                     tmp_original = intent.getStringExtra("tempreture");

                    avarageHeart.setText(sharedPreferences.getString("latestHr","--")+" BPS");
                    tmpSteps.setText(today_steps);
                    tmpCals.setText(today_kcals);
                    tmpDistance.setText(today_distance+" KM");
                    tmpReading.setText(tmp_original);
                    syncStatus.setText(sync_status);


               try {
                        Double stepsPerc = Double.valueOf(today_steps);
                        stepsPerc = stepsPerc / 10000;
                        stepsPerc = stepsPerc * 100;

                        progress_bar.setProgress((int) Math.round(stepsPerc));
                        progresPer.setText(Math.round(stepsPerc) + "%");
                  }catch (Exception e){
                   }



                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter(DeviceConnect.BROADCAST_ACTION));


            DashBoardAPI();

            myEdit = sharedPreferences.edit();
            SQLiteDatabase.loadLibs(getContext());

            WeightMonitoringModel weightMonitoringModel = dbHelper.getLatestWeightReading();
            try {
                weightDate.setText("Recorded :"+StaticMethods.TimestampTodate(weightMonitoringModel.getDateRecorded()));
                currentWeight.setText(weightMonitoringModel.getWeight()+" kg");
                Long days = StaticMethods.calculateDaysBetweenTimeStamps(weightMonitoringModel.getDateRecorded(),StaticMethods.getCurrentTimeStamp());
                if (days >= 30){
                    cardWeight.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                    weightMessage.setVisibility(View.VISIBLE);
                }
            } catch (NullPointerException e) {
                weightDate.setText("---");
                currentWeight.setText("--");
                // Handle null data case here
            }





            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            today = df.format(c);

            stepsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent1 = new Intent(mContext, StepsActivity.class);
                    intent1.putExtra("today_steps",today_steps);
                    intent1.putExtra("today_kcals",today_kcals);
                    intent1.putExtra("today_distance",today_distance);
                    startActivity(intent1);
                }
            });
            addGoals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AddUserGoals.class);
                    startActivityForResult(intent, 2);
                }
            });
            cardWeight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(mContext, WeightMonitoring.class);
                    startActivity(intent1);

                }
            });
            cardSleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(mContext, SleepActivity.class);
                    startActivity(intent1);

                }
            });
            cardHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(mContext, HeartRateActivity.class);
                    startActivity(intent1);

                }
            });
            seeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), DetailActivitySummary.class);
                    startActivity(intent);
                }
            });


            initBLE();
        }
    }




    void setdataAdapter() {
        Log.e("adapter1", "adapter1");
        if (todaysActivityModels.size() > 0) {
            Log.e("adapter2", String.valueOf(todaysActivityModels.size()));
            noActvity.setVisibility(View.GONE);
            seeAll.setVisibility(View.VISIBLE);
            activitiesSummary.setVisibility(View.VISIBLE);
        }
        activitiesSummaryAdapter = new ActivitiesSummaryAdapter(getContext(), todaysActivityModels);
        mAdapter = activitiesSummaryAdapter;
        activitiesSummary.setLayoutManager(new LinearLayoutManager(getContext()));
        activitiesSummary.setAdapter(mAdapter);
        Log.e("adapter3", "adapter3");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            DashBoardAPI();
        }
    }

    private ArrayList<SlidingModel> populateList() {
        ArrayList<SlidingModel> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SlidingModel imageModel = new SlidingModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }
        return list;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBLE() {
        Log.e("LOGG", "INITBLE");
        mBManager = (BluetoothManager) mContext.getSystemService(BLUETOOTH_SERVICE);
        if (null != mBManager) {
            mBAdapter = mBManager.getAdapter();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBScanner = mBAdapter.getBluetoothLeScanner();
        }
        checkBLE();

    }

    private boolean checkBLE() {
        if (!BluetoothUtils.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }


    private void DashBoardAPI() {
        if (CM.isConnected(getActivity())) {
            CM.showProgressLoader(getActivity());
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                Log.e("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getDashboardData, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("status").equals("true")) {
                                    CM.HideProgressLoader();
                                    Log.e("HEREE", response.toString());
                                    JSONObject object = new JSONObject(response.getString("data"));

                                    JSONArray array = new JSONArray(object.getString("wellness_calulate_data"));

                                    if(array.length() == 0) {
                                        CM.ShowDialogueWithCustomAction(
                                            getActivity(),
                                            "It seems your wellness plan is not generated. It will take few steps to generate your wellness plan.",
                                            "Generate", "", false,
                                            new DialogClickListener() {
                                                @Override
                                                public void onPositiveClick() {
                                                    startActivity(new Intent(getActivity(), SelectDisease.class));
                                                }

                                                @Override
                                                public void onNegativeClick() {

                                                }
                                            }
                                        );
                                    } else {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject objectArray = array.getJSONObject(i);
                                            String wellness_plan_distance = objectArray.getString("wellness_plan_distance");
                                            String wellness_plan_steps = objectArray.getString("wellness_plan_steps");
                                            String wellness_plan_calories = objectArray.getString("wellness_plan_calories");
                                            String calories_burnt_calculation = objectArray.getString("calories_burnt_calculation");
                                            String sum_of_distance_for_day = objectArray.getString("sum_of_distance_for_day");

                                            sum_of_steps_for_day = objectArray.getString("sum_of_steps_for_day");

                                            sum_of_calories_for_day = objectArray.getString("sum_of_calories_for_day");

                                            String sum_of_distance_for_week = objectArray.getString("sum_of_distance_for_week");
                                            String sum_of_steps_for_week = objectArray.getString("sum_of_steps_for_week");
                                            String sum_of_calories_for_week = objectArray.getString("sum_of_calories_for_week");
                                            myPLanKcal.setText(wellness_plan_calories);
                                            myPLanCurrentKcal.setText(sum_of_calories_for_week);
                                            myPlanKm.setText(wellness_plan_distance);
                                            weeklyKm.setText(sum_of_distance_for_week);

                                            Double dayCal = Double.parseDouble(sum_of_steps_for_day) * 100 /10000;
                                            int dayCalt = (int) Math.round(dayCal);
                                            progress_bar.setProgress(dayCalt);
                                            progresPer.setText(dayCalt + " %");

                                            Double weekKCal = Double.parseDouble(sum_of_calories_for_week) * 100 / Double.parseDouble(calories_burnt_calculation);
                                            int tempweekKCal = (int) Math.round(weekKCal);
                                            myPlanKcalProText.setText(String.valueOf(tempweekKCal) + "%");
                                            myPlanKcalPro.setProgress(tempweekKCal);

                                            if(tempweekKCal >= 100) {
                                                myPlanKcalProText.setText("100 %");
                                                txtPlanStatus.setText("Well done, You have achieved your Weekly Goals.");
                                            } else {
                                                txtPlanStatus.setText("Great! You are almost there.");
                                            }

                                            Double weekKM = Double.parseDouble(sum_of_distance_for_week) * 100 / Double.parseDouble(wellness_plan_distance);
                                            int tempweekKM = (int) Math.round(weekKM);
                                            myPlanKMProText.setText(String.valueOf(tempweekKM) + "%");
                                            myPlanKMPro.setProgress(tempweekKCal);

                                            if(tempweekKM >= 100) {
                                                myPlanKMProText.setText("100 %");
                                                txtDistancePlanStatus.setText("Well done, You have achieved your Weekly Goals.");
                                            } else {
                                                txtDistancePlanStatus.setText("Great! You are almost there.");
                                            }


                                        }
                                    }


                                    String issetgoal = object.getString("issetgoal");
                                    String bmi_text_string = object.getString("bmi_text");
                                    String bmiText = object.getString("bmi");
                                    //bmi.setText(bmiText + " bmi");
                                    double intBMI = Double.valueOf(bmiText);
                                    if (intBMI < 18) {
                                        bmi.setText("You are underweight");
                                        bmi.setTextColor(getResources().getColor(com.potyvideo.library.R.color.blue));
                                    } else if (intBMI > 18 && intBMI < 25) {
                                        bmi.setText("You are normal");
                                        bmi.setTextColor(getResources().getColor(R.color.primary));
                                    } else if (intBMI > 25 && intBMI < 30) {
                                        bmi.setText("You are overweight");
                                        bmi.setTextColor(getResources().getColor(com.potyvideo.library.R.color.yellow));
                                    } else if (intBMI > 30 && intBMI < 35) {
                                        bmi.setText("You are obese");
                                        bmi.setTextColor(getResources().getColor(R.color.orange));
                                    } else if (intBMI > 34) {
                                        bmi.setText("You are extremely\nobese");
                                        bmi.setTextColor(getResources().getColor(R.color.red));
                                    }
                                    bmi_text.setText(bmi_text_string);
                                    if (issetgoal.equals("Yes")) {
                                        userGoals.setVisibility(View.VISIBLE);
                                        addGoalText.setVisibility(View.GONE);
                                        addGoals.setVisibility(View.GONE);
                                        JSONArray goalArray = new JSONArray(object.getString("setgoal_data"));

                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject objectArray = goalArray.getJSONObject(i);


                                            String setgoal_weight = objectArray.getString("setgoal_weight");
                                            String setgoal_steps = objectArray.getString("setgoal_steps");
                                            String setgoal_calories = objectArray.getString("setgoal_calories");
                                            String setgoal_running_distance = objectArray.getString("setgoal_running_distance");
                                            String sum_of_weight_for_day = objectArray.getString("sum_of_weight_for_day");
                                            sum_of_distance_for_day = objectArray.getString("sum_of_distance_for_day");
                                            sum_of_steps_for_day = objectArray.getString("sum_of_steps_for_day");
                                            sum_of_calories_for_day = objectArray.getString("sum_of_calories_for_day");

                                            total_lose_weight.setText(setgoal_weight);
                                            total_running.setText(setgoal_running_distance);
                                            total_calories.setText(setgoal_calories);
                                            total_steps.setText(setgoal_steps);
                                            goalWeight.setText(sum_of_weight_for_day);
                                            currentRunning.setText(sum_of_distance_for_day);
                                            goalKcal.setText(sum_of_calories_for_day);
                                            goalSteps.setText(sum_of_steps_for_day);


                                            Double weightLoose = Double.parseDouble(sum_of_weight_for_day) * 100 / Double.parseDouble(setgoal_weight);
                                            int tempWeightLoose = (int) Math.round(weightLoose);
                                            weightProText.setText(String.valueOf(tempWeightLoose) + "%");
                                            weightPro.setProgress(tempWeightLoose);


                                            Double runningPro = Double.parseDouble(sum_of_distance_for_day) * 100 / Double.parseDouble(setgoal_running_distance);
                                            int temprunningPro = (int) Math.round(runningPro);
                                            runningProgressText.setText(String.valueOf(temprunningPro) + "%");
                                            runningProgress.setProgress(temprunningPro);


                                            Double KcalPro = Double.parseDouble(sum_of_calories_for_day) * 100 / Double.parseDouble(setgoal_calories);
                                            int tempKcalPro = (int) Math.round(KcalPro);
                                            runningProgressText.setText(String.valueOf(tempKcalPro) + "%");
                                            runningProgress.setProgress(tempKcalPro);


                                            Double stepsProo = Double.parseDouble(sum_of_steps_for_day) * 100 / Double.parseDouble(setgoal_steps);
                                            int tempstepsPro = (int) Math.round(stepsProo);
                                            stepsProText.setText(String.valueOf(tempstepsPro) + "%");
                                            stepsPro.setProgress(tempstepsPro);
                                        }
                                    } else {
                                        userGoals.setVisibility(View.GONE);
                                        addGoalText.setVisibility(View.VISIBLE);
                                        addGoals.setVisibility(View.VISIBLE);
                                    }

                                } else {

                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                CM.HideProgressLoader();
                                e.printStackTrace();
                            }

                            getTodaysActivities();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    getTodaysActivities();
                    Log.e("activitysportsdata", error.toString());
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    90000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else
            if(getActivity()!=null)
                Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();


    }


    private void getTodaysActivities() {
        if (CM.isConnected(getActivity())) {
            CM.showProgressLoader(getActivity());
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getActivityDetailsLists, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("activitysportsdata", String.valueOf(response));
                            CM.HideProgressLoader();
                            try {
                                if (response.getString("status").equals("true")) {
                                    todaysActivityModels.clear();

                                    JSONArray array = new JSONArray(response.getString("data"));
                                    int count = array.length();
                                    if (count > 2) {
                                        count = 2;
                                    }
                                    for (int i = 0; i < count; i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String user_id = object.getString("user_id");
                                        String act_id = object.getString("act_id");
                                        String time_taken = object.getString("duration");
                                        String distance = object.getString("distance");
                                        String kcals = object.getString("kcals");
                                        String avarage_heart_rate = object.getString("avarage_heart_rate");
                                        String steps = object.getString("steps");
                                        String avg_pace = object.getString("avg_pace");
                                        String camera_file = object.getString("camera_file");
                                        String activity = object.getString("activity");
                                        String created_at = object.getString("created_at");
                                        String updated_at = object.getString("updated_at");
                                        String username = object.getString("username");
                                        String firstname = object.getString("firstname");
                                        String lastname = object.getString("lastname");
                                        String profileurl = object.getString("profileurl");


                                        TodaysActivityModel todaysActivityModel = new TodaysActivityModel(id, user_id, act_id, time_taken, distance, kcals, avarage_heart_rate, steps, avg_pace, camera_file, activity, created_at, updated_at, username, firstname, lastname, profileurl);
                                        todaysActivityModels.add(todaysActivityModel);


                                    }
                                    Log.d("UpsetdataAdapter", "UpsetdataAdapter");

                                    setdataAdapter();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    Log.e("activitysportsdata", error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else {
            if(getActivity()!=null)
                Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();
        }

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("LOGG", "BROADCASTRECEIVER");
            if (mBManager != null) {
                List<BluetoothDevice> connectedDevices = mBManager.getConnectedDevices(GATT);
                if (connectedDevices != null) {
                    for (int j = 0; j < connectedDevices.size(); j++) {
                        if (connectedDevices.get(j).getAddress().equalsIgnoreCase(hasMac)) {
                            updateSmartWatchServeUI(intent);
                            break;
                        }
                    }
                }
            }
        }
    };


    private void updateSmartWatchServeUI(Intent intent) {
//        sum_of_steps_for_day = intent.getStringExtra("steps");
//        sum_of_distance_for_day = intent.getStringExtra("distances");
//        sum_of_calories_for_day = intent.getStringExtra("kcals");
//        String connect = intent.getStringExtra("connect");
//
//        Log.e("STEPSSSSS", sum_of_steps_for_day);
//        Log.e("KCALLL", sum_of_calories_for_day);
//        Log.e("DISTANCEEE", sum_of_distance_for_day);
//
//        if (onFragment) {
//            if (!sum_of_calories_for_day.equals("")) {
//                currentKcal.setText(sum_of_calories_for_day);
//                goalKcal.setText(sum_of_calories_for_day);
//            }
//
//            if (!sum_of_steps_for_day.equals("")) {
//                currentSteps.setText(sum_of_steps_for_day);
//                goalSteps.setText(sum_of_steps_for_day);
//            }
//
//            if (!sum_of_distance_for_day.equals(""))
//                currentRunning.setText(sum_of_distance_for_day);
//
//        }


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("LOGG", "RESUME");
        try {
            isdeviceConnected = false;
            if (!isMyServiceRunning(DeviceConnect.class)) {
                getActivity().startService(intent);
            }
            if (!isMyServiceRunning(DeviceSyncService.class)) {
                getActivity().startService(intent2);
            }
            registerReceiver(broadcastReceiver, new IntentFilter(DeviceConnect.BROADCAST_ACTION));
        } catch (Exception e) {

        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("LOGG", "pause");
        try {
            isdeviceConnected = false;
            if (!isMyServiceRunning(DeviceConnect.class)) {
                getActivity().startService(intent);
            }
            if (!isMyServiceRunning(DeviceSyncService.class)) {
                getActivity().startService(intent2);
            }
            registerReceiver(broadcastReceiver, new IntentFilter(DeviceConnect.BROADCAST_ACTION));
        } catch (Exception e) {

        }
    }

    @Override
    public void onDetach() {
        isdeviceConnected = false;
        onFragment = false;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        isdeviceConnected = false;
        isdeviceConnected = false;
        super.onDestroy();
    }
}

