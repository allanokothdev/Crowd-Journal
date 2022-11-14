package com.documentorworldke.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.documentorworldke.android.fragments.ListenerFragment;
import com.documentorworldke.android.fragments.SponsorFragment;
import com.documentorworldke.android.models.Space;

public class ViewPagerAdapter extends FragmentStateAdapter  {

    private final Space user;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Space user) {
        super(fragmentActivity);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return SponsorFragment.getInstance(user);
            case 1:
                return ListenerFragment.getInstance(user);
        }
        return new SponsorFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
