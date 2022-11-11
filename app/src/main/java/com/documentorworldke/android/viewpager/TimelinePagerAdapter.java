package com.documentorworldke.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.documentorworldke.android.fragments.MapsFragment;
import com.documentorworldke.android.fragments.TimelineFragment;
import com.documentorworldke.android.models.Post;

import java.util.ArrayList;
import java.util.List;


public class TimelinePagerAdapter extends FragmentStateAdapter  {

    private final List<Post> objectList;

    public TimelinePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Post> objectList) {
        super(fragmentActivity);
        this.objectList = objectList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return MapsFragment.getInstance(objectList);
            case 1:
                return TimelineFragment.getInstance(objectList);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
