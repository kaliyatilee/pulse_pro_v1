package com.algebratech.pulse_wellness.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.algebratech.pulse_wellness.fragments.HomeFragment;
import com.algebratech.pulse_wellness.fragments.NewsFeedFragment;

public class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
                return newsFeedFragment;
            case 1:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
