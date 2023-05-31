package com.algebratech.pulse_wellness.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.algebratech.pulse_wellness.R;
import com.google.gson.Gson;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSportItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerStepPacket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SharedPreferenceUtil {

    private SharedPreferences sharedPreferences;

    public SharedPreferenceUtil(Context context) {
        if (context != null) {
            this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        }
    }

    public String getStringPreference(String key, String defaultValue) {
        String value = null;
        if (sharedPreferences != null) {
            value = sharedPreferences.getString(key, defaultValue);
        }
        return value;
    }

    public boolean setStringPreference(String key, String value) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public int getIntegerPreference(String key, int defaultValue) {
        int value = 0;
        if (sharedPreferences != null) {
            value = sharedPreferences.getInt(key, defaultValue);
        }
        return value;
    }

    public boolean setIntegerPreference(String key, int value) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    public boolean setBooleanPreference(String key, Boolean value) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    public boolean getBooleanPreference(String key, Boolean defaultValue) {
        boolean value = false;
        if (sharedPreferences != null) {
            value = sharedPreferences.getBoolean(key, defaultValue);
        }
        return value;
    }

    public boolean removePreference(String key) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.remove(key);
            return editor.commit();
        }
        return false;
    }


    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void clear() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }

    public void setSportData(ArrayList<ApplicationLayerSportItemPacket> data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);

        SharedPreferences sharedPref = sharedPreferences;
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("SportData", json);
        editor.commit();
    }

    public void setStepData(ApplicationLayerStepPacket data) {

        Gson gson = new Gson();
        SharedPreferences sharedPref = sharedPreferences;

        String jsonSaved = sharedPref.getString("StepData", "");
        String jsonNewproductToAdd = gson.toJson(data);

        JSONArray jsonArrayProduct = new JSONArray();

        try {
            if (jsonSaved.length() != 0) {
                jsonArrayProduct = new JSONArray(jsonSaved);
            }
            jsonArrayProduct.put(new JSONObject(jsonNewproductToAdd));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //SAVE NEW ARRAY
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("StepData", String.valueOf(jsonArrayProduct));
        editor.commit();
    }

    public void setSleepData(ArrayList<ApplicationLayerSleepItemPacket> data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);

        SharedPreferences sharedPref = sharedPreferences;
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("SleepData", json);
        editor.commit();
    }


}

