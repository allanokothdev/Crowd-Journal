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
import com.documentorworldke.android.adapters.AvatarAdapter;
import com.documentorworldke.android.adapters.UserAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.Space;
import com.documentorworldke.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class ListenerFragment extends Fragment implements UserItemClickListener {

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
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Elon Musk","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Kanye West","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Allan Okoth","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","David Sacks","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));
        objectList.add(new User("elon","","Joe Rogan","elonmusk","elonmusk@twitter.com","+250790006118","United States","elon",true,true));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listener, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),5);
        recyclerView.setLayoutManager(linearLayoutManager);
        AvatarAdapter adapter = new AvatarAdapter(getContext(), objectList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
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