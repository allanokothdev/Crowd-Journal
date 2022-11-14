package com.documentorworldke.android.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.R;
import com.documentorworldke.android.Sidebar;
import com.documentorworldke.android.UserProfile;
import com.documentorworldke.android.adapters.NotificationAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.ObjectItemClickListener;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.Notification;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements PostItemClickListener, UserItemClickListener, ObjectItemClickListener{

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private ListenerRegistration registration;
    private final List<Notification> objectList = new ArrayList<>();
    private NotificationAdapter adapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        adapter = new NotificationAdapter(getContext(),objectList,this,this);
        recyclerView.setAdapter(adapter);
        fetchObjects();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fetchMoreObjects(adapter.getFirstItemKey());
            }
        });
        return view;
    }

    private void fetchObjects(){
        Query query = firebaseFirestore.collection(Constants.NOTIFICATIONS).orderBy("nd",Query.Direction.DESCENDING).whereEqualTo("nr", currentUserID).limitToLast(20);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Notification object = documentChange.getDocument().toObject(Notification.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Notification object = documentChange.getDocument().toObject(Notification.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Notification object = documentChange.getDocument().toObject(Notification.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyItemRemoved(objectList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    private void fetchMoreObjects(String lastKey){
        Query query = firebaseFirestore.collection(Constants.NOTIFICATIONS).orderBy("nm",Query.Direction.DESCENDING).whereEqualTo("nr",currentUserID).startAfter(lastKey).limitToLast(20);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Notification object = documentChange.getDocument().toObject(Notification.class);
                        if (!objectList.contains(object)){
                            objectList.add(0,object);
                            adapter.notifyItemInserted(0);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Notification object = documentChange.getDocument().toObject(Notification.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Notification object = documentChange.getDocument().toObject(Notification.class);
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
    public void onPostItemClick(Post post, CardView cardView) {

        Intent intent = new Intent(getContext(), Sidebar.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",post);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(cardView, post.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());

    }

    @Override
    public void onUserItemClick(User user, ImageView imageView) {
        Intent intent = new Intent(getContext(), UserProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",user);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(imageView, user.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onObjectDisplayClick(String string, ImageView imageView) {

    }
}
