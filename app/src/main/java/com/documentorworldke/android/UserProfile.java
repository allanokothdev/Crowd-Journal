package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.documentorworldke.android.adapters.PostAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfile extends AppCompatActivity implements PostItemClickListener, View.OnClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final Context mContext = UserProfile.this;
    private Boolean collapsed = false;
    private CardView cardView;
    private PostAdapter adapter;
    private final List<Post> objectList = new ArrayList<>();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("object");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        AppBarLayout app_bar = findViewById(R.id.app_bar);
        cardView = findViewById(R.id.cardView);
        ImageView moreImageView = findViewById(R.id.moreImageView);
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView messageTextView = findViewById(R.id.messageTextView);
        getWindow().setStatusBarColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
       
        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if ( verticalOffset < -26) {
                if (!collapsed) {
                    cardView.animate().scaleX(0f).scaleY(0f).setDuration(200).start();
                    collapsed = true;
                }
            } else {
                if (collapsed) {
                    cardView.animate().scaleX(1).scaleY(1f).setDuration(200).start();
                    collapsed = false;
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.posts_divider)));
        recyclerView.addItemDecoration(divider);
        adapter = new PostAdapter(mContext, objectList, this);
        recyclerView.setAdapter(adapter);

        moreImageView.setOnClickListener(this);
        imageView.setTransitionName(user.getId());
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).centerCrop().dontAnimate().listener(new RequestListener<Drawable>() {@Override public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }@Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }}).into(imageView);
        textView.setText(user.getName());
        subTextView.setText(mContext.getString(R.string.username,user.getUsername()));
        fetchObjects(user.getId());
    }


    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.POSTS).orderBy("timestamp",Query.Direction.DESCENDING).whereArrayContains("tags", objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyItemRemoved(objectList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects(user.getId());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onPostItemClick(Post post, CardView cardView) {
        Intent intent = new Intent(mContext, Sidebar.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",post);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, post.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.floatingActionButton){
            startActivity(new Intent(mContext, CreatePost.class));
        }
    }
}