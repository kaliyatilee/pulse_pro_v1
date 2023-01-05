package com.algebratech.pulse_wellness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.algebratech.pulse_wellness.adapters.LeaderboardTabAdapter;
import com.algebratech.pulse_wellness.adapters.MyAdapter;
import com.google.android.material.tabs.TabLayout;

public class FragmentTabLeaderboardFriendlist extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment_tab_leaderboard_friendlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.leaderBoardTab);
        viewPager = view.findViewById(R.id.viewPagerLeaderBoard);
        viewPager.setCurrentItem(1);
        tabLayout.addTab(tabLayout.newTab().setText("Leaderboard"));
        tabLayout.addTab(tabLayout.newTab().setText("My Friends"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final LeaderboardTabAdapter leaderboardTabAdapter = new LeaderboardTabAdapter(getActivity(), getChildFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(leaderboardTabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }


}
