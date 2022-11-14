package com.documentorworldke.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.adapters.TopicsAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.TopicItemClickListener;
import com.documentorworldke.android.models.Topic;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopicList extends AppCompatActivity implements TopicItemClickListener {

    private final Context mContext = TopicList.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final List<Topic> topicList = new ArrayList<>();
    private TopicsAdapter topicsAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);
        user = GetUser.getUser(mContext,currentUserID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Topics");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.posts_divider)));
        recyclerView.addItemDecoration(divider);
        topicsAdapter = new TopicsAdapter(mContext, topicList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(topicsAdapter);
        fetchTopics(user.getCountry());
    }

    private void fetchTopics(String location){

        Query query = firebaseFirestore.collection(Constants.TOPICS).orderBy("td",Query.Direction.ASCENDING).whereArrayContains("tags", location);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Topic object = documentChange.getDocument().toObject(Topic.class);
                        if (!topicList.contains(object)){
                            topicList.add(object);
                            topicsAdapter.notifyItemInserted(topicList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Topic object = documentChange.getDocument().toObject(Topic.class);
                        if (topicList.contains(object)){
                            topicList.set(topicList.indexOf(object),object);
                            topicsAdapter.notifyItemChanged(topicList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Topic object = documentChange.getDocument().toObject(Topic.class);
                        if (topicList.contains(object)){
                            topicList.remove(object);
                            topicsAdapter.notifyItemRemoved(topicList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onTopicItemClick(Topic topic, TextView textView) {
        startActivity(new Intent(mContext, TopicDetail.class).putExtra(Constants.OBJECT_ID,topic.getTd()));

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTopics(user.getCountry());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

}
