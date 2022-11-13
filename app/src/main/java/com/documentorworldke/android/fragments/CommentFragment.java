package com.documentorworldke.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.documentorworldke.android.R;
import com.documentorworldke.android.adapters.CommentAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Comments;
import com.documentorworldke.android.models.Post;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private CommentAdapter adapter;
    private Post post;
    private final List<Comments> objectList = new ArrayList<>();

    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment getInstance(Post post){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.POSTS, post);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        post = (Post) getArguments().getSerializable(Constants.POSTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        try {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
            adapter = new CommentAdapter(requireContext(),objectList, post);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            fetchObjects(post.getId());
        } catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.COMMENTS).orderBy("id", Query.Direction.DESCENDING).whereArrayContains("tags",objectID).whereEqualTo("reported",false);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Comments object = documentChange.getDocument().toObject(Comments.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Comments object = documentChange.getDocument().toObject(Comments.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Comments object = documentChange.getDocument().toObject(Comments.class);
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
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects(post.getId());
    }
}