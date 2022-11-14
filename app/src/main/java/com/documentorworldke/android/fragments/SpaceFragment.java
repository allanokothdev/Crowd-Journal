package com.documentorworldke.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.R;
import com.documentorworldke.android.SpaceProfile;
import com.documentorworldke.android.UserProfile;
import com.documentorworldke.android.adapters.SpaceAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.SpaceItemClickListener;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.Space;
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

public class SpaceFragment extends Fragment implements SpaceItemClickListener, UserItemClickListener {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final List<Space> objectList = new ArrayList<>();
    private SpaceAdapter adapter;

    public SpaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_space, container, false);

        User user = GetUser.getUser(requireContext(), currentUserID);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.posts_divider)));
        recyclerView.addItemDecoration(divider);
        adapter = new SpaceAdapter(getContext(), objectList, this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fetchObjects();

        return view;
    }

    private void fetchObjects(){
        Query query = firebaseFirestore.collection(Constants.SPACES).orderBy("id",Query.Direction.DESCENDING);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Space object = documentChange.getDocument().toObject(Space.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Space object = documentChange.getDocument().toObject(Space.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Space object = documentChange.getDocument().toObject(Space.class);
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
        fetchObjects();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onSpaceItemClick(Space space, CardView cardView) {
        Intent intent = new Intent(getContext(), SpaceProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",space);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), Pair.create(cardView, space.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }

    @Override
    public void onUserItemClick(User user, ImageView imageView) {
        Intent intent = new Intent(requireContext(), UserProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",user);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), Pair.create(imageView, user.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}

