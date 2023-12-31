package com.documentorworldke.android;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Darty;
import com.documentorworldke.android.models.Promotion;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewPicture extends AppCompatActivity implements View.OnClickListener {

    private final Context mContext = ViewPicture.this;
    private Promotion promotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        promotion = (Promotion) bundle.getSerializable("object");
        int objectPosition = intent.getIntExtra("objectPosition",0);
        List<Darty> galleryList = (ArrayList<Darty>) intent.getSerializableExtra("list");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_blue_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        Button button = findViewById(R.id.button);
        button.setText(promotion.getCta());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext, galleryList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(objectPosition, true);
        tabLayout.setupWithViewPager(viewPager);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.button){
            try {
                Intent indie = new Intent(Intent.ACTION_VIEW);
                indie.setData(Uri.parse(promotion.getLink()));
                startActivity(indie);
            } catch (ActivityNotFoundException ignored) {
            }
        }
    }


    public static class ViewPagerAdapter extends PagerAdapter {

        private final Context context;
        private final List<Darty> galleryList;

        public ViewPagerAdapter(Context context, List<Darty> galleryList) {
            this.context = context;
            this.galleryList = galleryList;
        }

        @Override
        public int getCount() {
            return galleryList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.gallery_view_item, null);
            ImageView imageView = view.findViewById(R.id.imageView);
            Darty darty = galleryList.get(position);
            Glide.with(context.getApplicationContext()).load(darty.getId()).placeholder(R.drawable.cover).into(imageView);
            container.addView(view);
            return view;
        }
    }


    private void hideSystemUi(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            hideSystemUi();
        }
    }

}

