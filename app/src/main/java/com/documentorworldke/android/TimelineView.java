package com.documentorworldke.android;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.viewpager.TimelinePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimelineView extends AppCompatActivity {

    private final Context mContext = TimelineView.this;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    public static String[] categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_view);

        List<Post> objectList = (ArrayList<Post>) getIntent().getSerializableExtra(Constants.OBJECT_LIST);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Data Stories");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        categoryList = getResources().getStringArray(R.array.timeline);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        setUpViewPager(objectList);

    }

    private void setUpViewPager(List<Post> objectList) {
        TimelinePagerAdapter adapter = new TimelinePagerAdapter(TimelineView.this, objectList);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(categoryList[position])).attach();
    }
}
