package com.algebratech.pulse_wellness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.algebratech.pulse_wellness.R;

public class ProfileFragment2 extends Fragment {

    private NestedScrollView layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (NestedScrollView) inflater.inflate(R.layout.fragment_profile2, container, false);


        return layout;
    }
}
