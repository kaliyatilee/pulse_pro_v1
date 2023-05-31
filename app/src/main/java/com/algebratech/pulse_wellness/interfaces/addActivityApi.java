package com.algebratech.pulse_wellness.interfaces;

import static com.algebratech.pulse_wellness.utils.Constants.TAG;
import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.OutdoorWalkingActivity;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.activities.RunResultsActivity;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.utils.CM;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class addActivityApi {


    public void endActivityApi(String userId, String timeTaken, String distance, String kcals, String avarageHeartRate, String steps, String avgPace, String camera_file, String act_id, String activityName, Activity context, ActivityCallback activityCallback) {
        Log.d(TAG,"avarageHeartRate"+avarageHeartRate);
        if (CM.isConnected(context)) {
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("act_id", act_id);
                object.put("distance", distance);
                object.put("steps", steps);
                object.put("kcals", kcals);
                object.put("duration", timeTaken);
                object.put("avarage_heart_rate", avarageHeartRate);
                object.put("avg_pace", avgPace);
                object.put("activity", activityName);
                object.put("camera_file", camera_file);

                Log.e(TAG,"End activity"+ object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.addActivityDetails, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d(TAG,response.toString());
                                if (response.getString("status").equals("success")) {
                                    Log.d(TAG,"activityCallback Success");
                                    activityCallback.success();
                                    Log.d(TAG,"activityCallback Success");
                                } else {
                                    Log.d(TAG,"activityCallback Failure");
                                    activityCallback.failure();
                                }


                            } catch (Exception e) {
                                Log.d(TAG,"activityCallback Exception Failure");
                                activityCallback.failure();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //error
                    activityCallback.failure();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(context, R.string.noInternet, Toast.LENGTH_SHORT).show();
    }

}


