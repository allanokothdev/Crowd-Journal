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
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.PromotionDetail;
import com.documentorworldke.android.adapters.PromotionAdapter;
import com.documentorworldke.android.listeners.PromotionItemClickListener;
import com.documentorworldke.android.models.Promotion;
import com.documentorworldke.android.models.Space;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.documentorworldke.android.R;
import com.documentorworldke.android.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class SponsorFragment extends Fragment implements PromotionItemClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private PromotionAdapter adapter;
    private final List<Promotion> objectList = new ArrayList<>();

    public SponsorFragment() {
        // Required empty public constructor
    }

    public static SponsorFragment getInstance(Space space){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT, space);
        SponsorFragment fragment = new SponsorFragment();
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
        View view = inflater.inflate(R.layout.fragment_sponsor, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),3));
        adapter = new PromotionAdapter(getContext(), objectList, this);
        recyclerView.setAdapter(adapter);
        fetchPromotions();
        return view;
    }

    private void fetchPromotions(){
        Query query = firebaseFirestore.collection(Constants.PROMOTIONS).orderBy("pd", Query.Direction.DESCENDING);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Promotion object = documentChange.getDocument().toObject(Promotion.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Promotion object = documentChange.getDocument().toObject(Promotion.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Promotion object = documentChange.getDocument().toObject(Promotion.class);
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
        fetchPromotions();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }


    @Override
    public void onPromotionItemClick(Promotion promotion, ImageView imageView) {
        Intent intent = new Intent(requireContext(), PromotionDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,promotion);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(imageView, promotion.getPd()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}