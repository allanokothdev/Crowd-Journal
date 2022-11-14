package com.documentorworldke.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.models.Space;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.documentorworldke.android.R;
import com.documentorworldke.android.UserProfile;
import com.documentorworldke.android.adapters.UserAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class ListenerFragment extends Fragment implements UserItemClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private UserAdapter adapter;
    private final List<User> objectList = new ArrayList<>();

    public ListenerFragment() {
        // Required empty public constructor
    }

    public static ListenerFragment getInstance(Space user){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT, user);
        ListenerFragment fragment = new ListenerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        Space space = (Space) getArguments().getSerializable(Constants.OBJECT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listener, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        adapter = new UserAdapter(getContext(), objectList, this);
        recyclerView.setAdapter(adapter);
        fetchObjects("Listener");
        return view;
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.USERS).orderBy("id", Query.Direction.DESCENDING).whereArrayContains("tags",objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        User object = documentChange.getDocument().toObject(User.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        User object = documentChange.getDocument().toObject(User.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        User object = documentChange.getDocument().toObject(User.class);
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
        fetchObjects("Listener");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onUserItemClick(User user, ImageView imageView) {
        Intent intent = new Intent(requireContext(), UserProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,user);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), Pair.create(imageView, user.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}