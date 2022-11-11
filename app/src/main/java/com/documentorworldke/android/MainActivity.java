package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.adapters.PostAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.Token;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements PostItemClickListener, View.OnClickListener {

    private final Context mContext = MainActivity.this;
    private final String TAG = this.getClass().getSimpleName();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final List<Post> objectList = new ArrayList<>();
    private PostAdapter adapter;
    private ImageView moreImageView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = GetUser.getUser(mContext,currentUserID);
        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        moreImageView = findViewById(R.id.moreImageView);
        moreImageView.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.posts_divider)));
        recyclerView.addItemDecoration(divider);
        adapter = new PostAdapter(mContext, objectList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fetchObjects(user.getCountry());
        fetchUserInfo(currentUserID);
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.POSTS).orderBy("id", Query.Direction.DESCENDING).whereArrayContains("tags",objectID);
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
        fetchObjects(user.getCountry());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    private void fetchUserInfo(String currentUserID){
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                SharedPreferences.Editor editor = getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
                editor.putString(Constants.PIC, user.getPic());
                editor.putString(Constants.NAME, user.getName());
                editor.putString(Constants.EMAIL, user.getEmail());
                editor.putString(Constants.COUNTRY, user.getCountry());
                editor.putString(Constants.TOKEN, user.getToken());
                editor.putBoolean(Constants.VERIFICATION, user.isVerification());
                editor.apply();

                fetchToken(user);

                SharedPreferences.Editor localeEditor = getSharedPreferences(Constants.COUNTRY,Context.MODE_PRIVATE).edit();
                localeEditor.putString(Constants.COUNTRY, user.getCountry());
                localeEditor.apply();
            } else {
                startActivity(new Intent(mContext, UpdateUserProfile.class));
            }
        });
    }

    private void fetchToken(User user){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String token = task.getResult();
                String msg = getString(R.string.msg_token_fmt, token);
                Timber.d(msg);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token",token);
                FirebaseDatabase.getInstance().getReference(Constants.TOKEN).child(currentUserID).updateChildren(hashMap);

                Token groupToken = new Token(token);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getCountry()).child(user.getId()).setValue(groupToken);

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

    private void subscribeTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(task -> { });
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
        if (view.getId() == R.id.moreImageView) {
            PopupMenu popupMen = new PopupMenu(mContext, moreImageView);
            popupMen.getMenuInflater().inflate(R.menu.menu_main, popupMen.getMenu());
            popupMen.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_order:
                        if (user != null){
                            Bundle updateBundle = new Bundle();
                            updateBundle.putSerializable("object", user);
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
