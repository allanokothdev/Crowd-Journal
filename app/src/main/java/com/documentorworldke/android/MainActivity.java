package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Topic;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.documentorworldke.android.viewpager.CustomViewPager;
import com.documentorworldke.android.viewpager.MainViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String TAG = this.getClass().getSimpleName();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private long pressedTime;
    private TextView textView;
    private CustomViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private final Context mContext = MainActivity.this;
    private ImageView moreImageView;
    private User user;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        moreImageView = findViewById(R.id.moreImageView);
        textView = findViewById(R.id.textView);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        viewPager = findViewById(R.id.viewPager);
        moreImageView.setOnClickListener(this);

        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        textView.setText(R.string.app_name);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_topics).setChecked(true);
                        textView.setText(R.string.menu_topic);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_spaces).setChecked(true);
                        textView.setText(R.string.menu_spaces);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_notification).setChecked(true);
                        textView.setText(R.string.menu_notification);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_chat).setChecked(true);
                        textView.setText(getResources().getString(R.string.menu_chat));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fetchUserInfo(currentUserID);
    }





    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.navigation_home:
                viewPager.setCurrentItem(0, false);
                textView.setText(getResources().getString(R.string.app_name));
                break;
            case R.id.navigation_topics:
                viewPager.setCurrentItem(1, false);
                textView.setText(getResources().getString(R.string.menu_topic));
                break;
            case R.id.navigation_spaces:
                viewPager.setCurrentItem(2, false);
                textView.setText(getResources().getString(R.string.menu_spaces));
                break;
            case R.id.navigation_notification:
                viewPager.setCurrentItem(3, false);
                textView.setText(getResources().getString(R.string.menu_notification));
                break;
            case R.id.navigation_chat:
                viewPager.setCurrentItem(4, false);
                textView.setText(getResources().getString(R.string.menu_chat));
                break;
        }
        return true;
    }


    @Override public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(mContext, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

    }

    private void fetchUserInfo(String currentUserID){
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                User userObject = documentSnapshot.toObject(User.class);
                assert userObject != null;
                user = userObject;
                GetUser.saveUser(mContext,userObject);
                fetchToken();
            } else {
                startActivity(new Intent(mContext, UpdateUserProfile.class));
            }
        });
    }


    private void fetchToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String token = task.getResult();
                String msg = getString(R.string.msg_token_fmt, token);
                Timber.d(msg);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token",token);
                FirebaseDatabase.getInstance().getReference(Constants.TOKEN).child(currentUserID).updateChildren(hashMap);

                SharedPreferences.Editor editor = getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
                editor.putString(Constants.TOKEN, token);
                editor.apply();

                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("token",token);
                FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).update(userMap);

            }else {
                Timber.tag(TAG).w(task.getException(), "Fetching FCM registration token failed");
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.moreImageView) {
            Bundle updateBundle = new Bundle();
            updateBundle.putSerializable("object", user);
            PopupMenu popupMen = new PopupMenu(mContext, moreImageView);
            popupMen.getMenuInflater().inflate(R.menu.menu_main, popupMen.getMenu());
            popupMen.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_brands:
                        if (user != null){
                            startActivity(new Intent(mContext, BrandList.class).putExtras(updateBundle));
                        }
                        break;
                    case R.id.action_order:
                        if (user != null){
                            startActivity(new Intent(mContext, UserProfile.class).putExtras(updateBundle));
                        }
                        break;
                    case R.id.action_sign_out:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Sign Out");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            firebaseAuth.signOut();
                            finishAfterTransition();
                        });
                        builder.setNegativeButton("Hell No", (dialog, which) -> {
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                }
                return true;
            });
            popupMen.show();
        } else if (view.getId()==R.id.floatingActionButton){
            startActivity(new Intent(mContext, CreatePost.class));
        }
    }
}