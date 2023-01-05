package com.algebratech.pulse_wellness.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.algebratech.pulse_wellness.fragments.CommunityFragment;
import com.algebratech.pulse_wellness.fragments.LeaderboardFragment;

public class LeaderboardTabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public LeaderboardTabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
                return leaderboardFragment;
            case 1:
                CommunityFragment communityFragment = new CommunityFragment();
                return communityFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
