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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.R;
import com.documentorworldke.android.UserProfile;
import com.documentorworldke.android.adapters.AgendaAdapter;
import com.documentorworldke.android.adapters.UserAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.Agenda;
import com.documentorworldke.android.models.Space;
import com.documentorworldke.android.models.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AgendaFragment extends Fragment {

    private Space space;
    private final List<Agenda> objectList = new ArrayList<>();

    public AgendaFragment() {
        // Required empty public constructor
    }

    public static AgendaFragment getInstance(Space space){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT, space);
        AgendaFragment fragment = new AgendaFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        space = (Space) getArguments().getSerializable(Constants.OBJECT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        objectList.add(new Agenda("Question to: Elon Musk","Donald","","Citizen Journalism","02:39","Regarding your previous statement, What are your future plans for future in regard to Citizen Journalism"));
        objectList.add(new Agenda("Question to: Kanye West","Omari","","Cancel Culture","04:39","You recently experienced the wrath of Cancel Culture, which led to you loosing the support of brands like Adidas and Balenciaga, What is..."));
        objectList.add(new Agenda("Question to: David Sacks","Sylvia","","Free Speech","09:39","Regarding your previous statement, What are your future plans for future in regard to Citizen Journalism"));
        objectList.add(new Agenda("Question to: Jack Dorsey","Kelly","","Healthy Conversations","12:39","Regarding your previous statement, What are your future plans for future in regard to Citizen Journalism"));
        objectList.add(new Agenda("Question to: Joe Rogan","Favour","","Hate Speech","17:39","Regarding your previous statement, What are your future plans for future in regard to Citizen Journalism"));
        objectList.add(new Agenda("Question to: Joe Rogan","Allan","","Account Amnesty","34:39","Regarding your previous statement, What are your future plans for future in regard to Citizen Journalism"));
        objectList.add(new Agenda("Question to: David Sacks","Steve","","Twitter Innovations","42:39","Regarding your prev"));
        objectList.add(new Agenda("Question to: Kanye West","Lineker","","Crypto Integration","47:39","I saw the CZ (Binance) is co-invested with you in Twitter, ccan we expect Crypto Integration  in the near future, and How do you intend to apply it"));
        objectList.add(new Agenda("Question to: Elon Musk","Uhuru","","Retrenchment","49:39","Will there be other another retrenchment, or you are done with that"));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AgendaAdapter adapter = new AgendaAdapter(getContext(), objectList);
        recyclerView.setAdapter(adapter);
        return view;
    }

}