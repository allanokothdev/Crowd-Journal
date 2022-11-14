package com.documentorworldke.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.documentorworldke.android.fragments.ChatFragment;
import com.documentorworldke.android.fragments.ExploreFragment;
import com.documentorworldke.android.fragments.HomeFragment;
import com.documentorworldke.android.fragments.NotificationFragment;
import com.documentorworldke.android.fragments.SpaceFragment;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public MainViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new ExploreFragment();
            case 2:
                return new SpaceFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ChatFragment();
        }
        return new HomeFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }

}
