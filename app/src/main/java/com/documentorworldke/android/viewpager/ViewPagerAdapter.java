package com.documentorworldke.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.documentorworldke.android.fragments.AgendaFragment;
import com.documentorworldke.android.fragments.ListenerFragment;
import com.documentorworldke.android.fragments.SponsorFragment;
import com.documentorworldke.android.models.Space;

public class ViewPagerAdapter extends FragmentStateAdapter  {

    private final Space object;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Space object) {
        super(fragmentActivity);
        this.object = object;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return SponsorFragment.getInstance(object);
            case 1:
                return AgendaFragment.getInstance(object);
            case 2:
                return ListenerFragment.getInstance(object);
        }
        return ListenerFragment.getInstance(object);
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
