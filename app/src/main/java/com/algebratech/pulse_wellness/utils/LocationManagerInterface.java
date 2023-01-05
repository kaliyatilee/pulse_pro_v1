package com.algebratech.pulse_wellness.utils;

import android.location.Location;


public interface LocationManagerInterface {
    String TAG = Constants.TAG;

    void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider);

}