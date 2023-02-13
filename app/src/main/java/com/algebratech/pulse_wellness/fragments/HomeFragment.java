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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.AddFriendActivity;
import com.algebratech.pulse_wellness.activities.AddUserGoals;
import com.algebratech.pulse_wellness.activities.DetailActivitySummary;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.activities.SelectDisease;
import com.algebratech.pulse_wellness.adapters.ActivitiesSummaryAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.SlidingModel;
import com.algebratech.pulse_wellness.models.TodaysActivityModel;
import com.algebratech.pulse_wellness.models.WellnessPlanModel;
import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.veepoo.protocol.VPOperateManager;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;
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
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private Intent intent;
    private int[] myImageList = new int[]{R.drawable.banner_1, R.drawable.banner_calculator, R.drawable.banner_3};
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    Context mContext;
    private BluetoothManager mBManager;
    private BluetoothAdapter mBAdapter;
    private BluetoothLeScanner mBScanner;
    TextView stepsTextview, txtWalk, distance;
    ProgressBar progressBar, runningProgress, myPlanKcalPro, myPlanKMPro, weightPro, kcalPro, stepsPro;
    private final int REQUEST_CODE = 1;
    VPOperateManager mVpoperateManager;
    private int deviceNumber = -1;
    private String deviceVersion;
    private String deviceTestVersion;
    int watchDataDay = 3;
    int contactMsgLength = 0;
    int allMsgLenght = 4;
    boolean isNewSportCalc = false;
    boolean isSleepPrecision = false;
    Boolean isdeviceConnected = false;
    //database helper object
    private DBHelper db;
    private Cursor c;
    View root;
    private String amount, today;
    int progress = 30;
    private List<WellnessPlanModel> wellnessPlanModels = new ArrayList<>();
    String userId;
    Button addGoals;
    TextView total_lose_weight, total_running, total_calories, total_steps;
    LinearLayout userGoals;
    TextView currentSteps, currentKcal, currentRunning;
    String steps, distances, kcals, hasMac;
    String sum_of_calories_for_day = "0";
    String sum_of_steps_for_day = "0";
    String sum_of_distance_for_day = "0";
    RecyclerView activitiesSummary;
    private ActivitiesSummaryAdapter activitiesSummaryAdapter;
    private RecyclerView.Adapter mAdapter;
    TextView seeAll;
    List<TodaysActivityModel> todaysActivityModels = new ArrayList<>();

    TextView myPLanKcal, bmi, bmi_text, myPLanCurrentKcal, myPlanKm, weeklyKm, addGoalText, goalWeight, goalKcal, goalSteps, myPlanKcalProText, myPlanKMProText, weightProText, runningProgressText, stepsProText, kcalProText, noActvity;
    TextView txtPlanStatus,txtDistancePlanStatus;
    boolean onFragment = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e("IDIGIT_21_07_View", "HOME CLASS CALLED");
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              //  initData();
                init();

                onFragment = true;
                DashBoardAPI();
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
            progressBar = root.findViewById(R.id.progressBar);
            runningProgress = root.findViewById(R.id.runningProgress);
            addGoals = root.findViewById(R.id.addGoals);
            noActvity = root.findViewById(R.id.noActvity);

            distance = root.findViewById(R.id.distance);

            mVpoperateManager = mVpoperateManager.getMangerInstance(mContext.getApplicationContext());
            imageModelArrayList = new ArrayList<>();
            imageModelArrayList = populateList();
            NUM_PAGES = imageModelArrayList.size();

            currentSteps = root.findViewById(R.id.currentSteps);
            currentKcal = root.findViewById(R.id.currentKcal);
            currentRunning = root.findViewById(R.id.currentRunning);
            total_lose_weight = root.findViewById(R.id.total_lose_weight);
            total_running = root.findViewById(R.id.total_running);
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

            userGoals = root.findViewById(R.id.userGoals);
            activitiesSummary = root.findViewById(R.id.activitiesSummary);
            seeAll = root.findViewById(R.id.seeAll);

            DashBoardAPI();


            myEdit = sharedPreferences.edit();
            SQLiteDatabase.loadLibs(getContext());
            db = new DBHelper(getContext());

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            today = df.format(c);

//            try {
//                Log.d(Constants.TAG + "DatabaseRes", db.getTotal(sharedPreferences.getString("userID", "")));
//                Log.d(Constants.TAG + "DatabaseRes", String.valueOf(db.checkToday(sharedPreferences.getString("userID", ""))));
//            } catch (Exception e) {
//                Log.d(Constants.TAG + "DatabaseRes", e.getMessage());
//            }
            // final DatabaseHelper db = new DatabaseHelper(getContext());
            // getting initial points from firebase





            addGoals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AddUserGoals.class);
                    startActivityForResult(intent, 2);
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


//not usefull for now
//    private void activityOptions() {
//        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
//        View mView = getLayoutInflater().inflate(R.layout.activity_active, null);
//        mBuilder.setIcon(R.drawable.logo_now);
//        mBuilder.setView(mView);
//        mBuilder.setTitle("Select Activity");
//
//        CardView card1 = mView.findViewById(R.id.outdoorRunning);
//        CardView card2 = mView.findViewById(R.id.card2);
//        CardView card3 = mView.findViewById(R.id.card3);
//        CardView card4 = mView.findViewById(R.id.card4);
//        final AlertDialog dialog = mBuilder.create();
//
//        card1.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//
//                boolean isOn = checkBLE();
//                if (isOn) {
//                    Intent intent = new Intent(mContext, OutdoorRunActivity.class);
//                    startActivity(intent);
//                } else {
//                    StaticMethods.showNotification(view, "Wearable not paired");
//                }
//
//
//            }
//        });
//
//        card3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                boolean isOn = checkBLE();
//                if (isOn) {
//                    Intent intent = new Intent(mContext, OutdoorCyclingActivity.class);
//                    startActivity(intent);
//                } else {
//                    StaticMethods.showNotification(view, "Wearable not paired");
//                }
//
//            }
//        });
//
//        card2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                boolean isOn = checkBLE();
//                if (isOn) {
//                    Intent intent = new Intent(mContext, TreadmillActivity.class);
//                    startActivity(intent);
//                } else {
//                    StaticMethods.showNotification(view, "Wearable not paired");
//                }
//            }
//        });
//
//        card4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                boolean isOn = checkBLE();
//                if (isOn) {
//                    Intent intent = new Intent(mContext, OutdoorWalkingActivity.class);
//                    startActivity(intent);
//                } else {
//                    StaticMethods.showNotification(view, "Wearable not paired");
//                }
//
//
//            }
//        });
//
//        dialog.show();
//
//    }

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


//    private void getWellnessPlan() {
//
//        JSONObject object = new JSONObject();
//        try {
//            object.put("user_id", userId);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getMyWellnessPlan, object,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(Constants.TAG, String.valueOf(response));
//
//                        try {
//                            if (response.getString("status").equals("true")) {
//                                JSONArray array = new JSONArray(response.getString("data"));
//                                for (int i = 0; i < array.length(); i++) {
//
//                                    JSONObject object = array.getJSONObject(i);
//                                    String plan_name = object.getString("plan_name");
//                                    String steps = object.getString("steps");
//                                    String daily_distance = object.getString("daily_distance");
//                                    String calories_burnt = object.getString("calories_burnt");
//                                    String frequency_of_activity = object.getString("frequency_of_activity");
//                                    String daily_calorie_intake = object.getString("daily_calorie_intake");
//                                    String daily_reminder = object.getString("daily_reminder");
//                                    String duration_of_exercise = object.getString("duration_of_exercise");
//                                    String recommended_calorie_deficit = object.getString("recommended_calorie_deficit");
//                                    Log.e("User Deatils", plan_name + steps + daily_distance + calories_burnt + frequency_of_activity + daily_calorie_intake + daily_reminder + duration_of_exercise + recommended_calorie_deficit);
//                                    WellnessPlanModel wellnessPlanModel = new WellnessPlanModel(plan_name, steps, daily_distance, calories_burnt, frequency_of_activity, daily_calorie_intake, daily_reminder, duration_of_exercise, duration_of_exercise, recommended_calorie_deficit);
//                                    wellnessPlanModels.add(wellnessPlanModel);
//                                }
//
////                                stepss.setText(wellnessPlanModels.get(0).getSteps());
////                                distances.setText(wellnessPlanModels.get(0).getDaily_distance());
////                                calories_burnt.setText(wellnessPlanModels.get(0).getCalories_burnt());
////                                frequency_of_activity.setText(wellnessPlanModels.get(0).getFrequency_of_activity());
////                                daily_calorie_intake.setText(wellnessPlanModels.get(0).getDaily_calorie_intake());
////                                daily_reminder.setText(wellnessPlanModels.get(0).getDaily_reminder());
////
////                                Log.e("Daily Reminder", wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport());
////                                if (wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport() != null &&
////                                        wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport() != "null") {
////                                    participation_in_rigorous_school_sport.setText(wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport());
////                                }
////                                if (wellnessPlanModels.get(0).getRecommended_calorie_deficit() != null &&
////                                        wellnessPlanModels.get(0).getRecommended_calorie_deficit() != "null") {
////                                    recommended_calorie_deficit.setText(wellnessPlanModels.get(0).getRecommended_calorie_deficit());
////                                }
////                                if (wellnessPlanModels.get(0).getDuration_of_exercise() != null &&
////                                        wellnessPlanModels.get(0).getDuration_of_exercise() != "null") {
////                                    duration_of_exercise.setText(wellnessPlanModels.get(0).getDuration_of_exercise());
////                                }
//
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (wellnessPlanModels.size() == 0) {
//                            //  no_plan.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d("Error", "Error: " + error.getMessage());
//                if (error.getMessage().contains(Api.baseurl)) {
//                    Toast.makeText(getContext(), "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(jsonObjectRequest);
//
//    }


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
                                            currentSteps.setText(sum_of_steps_for_day);
                                            sum_of_calories_for_day = objectArray.getString("sum_of_calories_for_day");
                                            currentKcal.setText(sum_of_calories_for_day);
                                            String sum_of_distance_for_week = objectArray.getString("sum_of_distance_for_week");
                                            String sum_of_steps_for_week = objectArray.getString("sum_of_steps_for_week");
                                            String sum_of_calories_for_week = objectArray.getString("sum_of_calories_for_week");
                                            myPLanKcal.setText(wellness_plan_calories);
                                            myPLanCurrentKcal.setText(sum_of_calories_for_week);
                                            myPlanKm.setText(wellness_plan_distance);
                                            weeklyKm.setText(sum_of_distance_for_week);

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
                                        bmi.setTextColor(getResources().getColor(R.color.blue));
                                    } else if (intBMI > 18 && intBMI < 25) {
                                        bmi.setText("You are normal");
                                        bmi.setTextColor(getResources().getColor(R.color.primary));
                                    } else if (intBMI > 25 && intBMI < 30) {
                                        bmi.setText("You are overweight");
                                        bmi.setTextColor(getResources().getColor(R.color.yellow));
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
        sum_of_steps_for_day = intent.getStringExtra("steps");
        sum_of_distance_for_day = intent.getStringExtra("distances");
        sum_of_calories_for_day = intent.getStringExtra("kcals");
        String connect = intent.getStringExtra("connect");

        Log.e("STEPSSSSS", sum_of_steps_for_day);
        Log.e("KCALLL", sum_of_calories_for_day);
        Log.e("DISTANCEEE", sum_of_distance_for_day);

        if (onFragment) {
            if (!sum_of_calories_for_day.equals("")) {
                currentKcal.setText(sum_of_calories_for_day);
                goalKcal.setText(sum_of_calories_for_day);
            }

            if (!sum_of_steps_for_day.equals("")) {
                currentSteps.setText(sum_of_steps_for_day);
                goalSteps.setText(sum_of_steps_for_day);
            }

            if (!sum_of_distance_for_day.equals(""))
                currentRunning.setText(sum_of_distance_for_day);

        }


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

