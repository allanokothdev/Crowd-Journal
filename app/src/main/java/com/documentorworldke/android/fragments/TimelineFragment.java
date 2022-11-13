package com.documentorworldke.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.R;
import com.documentorworldke.android.Sidebar;
import com.documentorworldke.android.adapters.HistoryAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.models.Post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment implements PostItemClickListener {

    private List<Post> objectList = new ArrayList<>();

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment getInstance(List<Post> objectList){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT_LIST, (Serializable) objectList);
        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;

        objectList = (ArrayList<Post>) getArguments().getSerializable(Constants.OBJECT_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        try {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
            HistoryAdapter adapter = new HistoryAdapter(requireContext(),objectList, this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

        } catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onPostItemClick(Post post, CardView cardView) {
        Intent intent = new Intent(requireContext(), Sidebar.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",post);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(cardView, post.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}