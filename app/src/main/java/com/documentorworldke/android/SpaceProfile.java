package com.documentorworldke.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.documentorworldke.android.adapters.UserAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.Space;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.viewpager.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SpaceProfile extends AppCompatActivity implements UserItemClickListener {

    private final Context mContext = SpaceProfile.this;
    private static String[] tabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Space space = (Space) bundle.getSerializable(Constants.OBJECT);
        tabList = getResources().getStringArray(R.array.spaces);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        UserAdapter adapter = new UserAdapter(mContext, space.getPanel(), this);
        recyclerView.setAdapter(adapter);
        setUpViewPager(viewPager,tabLayout,space);
    }

    private void setUpViewPager(ViewPager2 viewPager, TabLayout tabLayout, Space space) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, space);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabList[position])).attach();
        for (int i = 0; i < tabLayout.getTabCount(); i++){
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
            tabLayout.getTabAt(i).setCustomView(tv);
        }
    }

    @Override
    public void onUserItemClick(User user, ImageView imageView) {
        Intent intent = new Intent(mContext, UserProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,user);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(imageView, user.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}