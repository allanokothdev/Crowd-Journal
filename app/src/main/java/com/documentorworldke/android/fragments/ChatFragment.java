package com.documentorworldke.android.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.ChatRoom;
import com.documentorworldke.android.R;
import com.documentorworldke.android.adapters.ChatAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.ChatItemClickListener;
import com.documentorworldke.android.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment implements ChatItemClickListener {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final List<Chat> objectList = new ArrayList<>();
    private ChatAdapter adapter;
    
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        try {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
            adapter = new ChatAdapter(getContext(),objectList, this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            fetchObjects(currentUserID);

            try { Collections.sort(objectList, (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
            }catch (Exception e){ e.printStackTrace();}
        } catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }


    private void fetchObjects(String objectID){
        try {
            Query query = FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(objectID).orderByKey();
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()){
                        Chat object = snapshot.getValue(Chat.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }@Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()){
                        Chat object = snapshot.getValue(Chat.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object), object);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }@Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Chat object = snapshot.getValue(Chat.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }@Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()){
                        Chat object = snapshot.getValue(Chat.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }@Override public void onCancelled(@NonNull DatabaseError error) { }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onChatItemClick(Chat chat, ImageView imageView) {
        Intent intent = new Intent(getContext(), ChatRoom.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",chat);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(imageView, chat.getUd()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}
