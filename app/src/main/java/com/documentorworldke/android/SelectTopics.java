package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Post;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

public class SelectTopics extends AppCompatActivity implements View.OnClickListener{

    private final Context mContext = SelectTopics.this;
    private RelativeLayout container;
    private TagFlowLayout tagFlowLayout;
    private Button button;
    private AVLoadingIndicatorView progressBar;
    private String[] topics;
    private final ArrayList<String> stringArrayList = new ArrayList<>();
    private Post post;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_topics);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            post = (Post) bundle.getSerializable(Constants.OBJECT);
            topics = getResources().getStringArray(R.array.topics);

            final LayoutInflater mInflater = LayoutInflater.from(mContext);
            container = findViewById(R.id.container);
            tagFlowLayout = findViewById(R.id.tagFlowLayout);
            progressBar = findViewById(R.id.progressBar);
            button = findViewById(R.id.button);
            button.setOnClickListener(this);
            tagFlowLayout.setAdapter(new TagAdapter<String>(topics)
            {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView textView = (TextView) mInflater.inflate(R.layout.values_listitem, tagFlowLayout, false);
                    textView.setText(s);
                    return textView;
                }

                @Override public void onSelected(int position, View view) {
                    super.onSelected(position, view);
                    view.setSelected(true);
                    stringArrayList.add(topics[position]);
                    if (stringArrayList.size()>0){
                        button.setVisibility(View.VISIBLE);
                    }else {
                        button.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(container, "Select at least 1 Topic", Snackbar.LENGTH_SHORT);snackbar.show();
                    }
                }

                @Override public void unSelected(int position, View view) {
                    super.unSelected(position, view);
                    view.setSelected(false);

                    stringArrayList.remove(topics[position]);
                    if (stringArrayList.size()>0){
                        button.setVisibility(View.VISIBLE);
                    }else {
                        button.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(container, "Select at least 1 Topic", Snackbar.LENGTH_SHORT);snackbar.show();
                    }
                }

                @Override public int getCount() { return super.getCount(); }
                @Override public String getItem(int position) {
                    return super.getItem(position);
                }
                @NonNull @Override public String toString() {
                    return super.toString();
                }
            });
        }catch (Exception e){ e.printStackTrace();}
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            progressBar.setVisibility(View.VISIBLE);
            post.setTopic(stringArrayList.get(0));
            stringArrayList.addAll(post.getTags());
            post.setTags(stringArrayList);
            FirebaseFirestore.getInstance().collection(Constants.POSTS).document(post.getId()).set(post).addOnSuccessListener(unused -> {
                progressBar.setVisibility(View.GONE);
                finishAfterTransition();
            });
        }
    }


}

