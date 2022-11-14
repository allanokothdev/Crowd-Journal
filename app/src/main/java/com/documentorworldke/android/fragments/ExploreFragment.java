package com.documentorworldke.android.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.R;
import com.documentorworldke.android.Sidebar;
import com.documentorworldke.android.TopicDetail;
import com.documentorworldke.android.adapters.PostAdapter;
import com.documentorworldke.android.adapters.TopicsAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.listeners.TopicItemClickListener;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.Topic;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.documentorworldke.android.utils.RecyclerItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment implements PostItemClickListener, TopicItemClickListener {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final List<Topic> topicList = new ArrayList<>();
    private TopicsAdapter topicsAdapter;
    private final ArrayList<Post> objectList = new ArrayList<>();
    private PostAdapter adapter;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        @SuppressLint("SimpleDateFormat") String today = new SimpleDateFormat("ddMMMM").format(Calendar.getInstance().getTime());

        User user = GetUser.getUser(requireContext(),currentUserID);

        ImageView coverImageView = view.findViewById(R.id.coverImageView);
        TextView textView = view.findViewById(R.id.textView);
        Glide.with(requireActivity()).load(getImage(lowerCased(user.getCountry()))).placeholder(R.drawable.cover).into(coverImageView);
        textView.setText(getString(R.string.conversations, user.getCountry()));

        RecyclerView topicRecyclerView = view.findViewById(R.id.topicRecyclerView);
        topicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topicRecyclerView.addItemDecoration(new DividerItemDecoration(topicRecyclerView.getContext(),DividerItemDecoration.VERTICAL));

        topicsAdapter = new TopicsAdapter(getContext(), topicList, this);
        topicRecyclerView.setAdapter(topicsAdapter);
        fetchTopics(user.getCountry());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(getContext(),objectList,this);
        recyclerView.setAdapter(adapter);
        RecyclerItemDecoration recyclerItemDecoration = new RecyclerItemDecoration(requireActivity(),getResources().getDimensionPixelSize(R.dimen.header_height),true,getSectionCallback(objectList));
        recyclerView.addItemDecoration(recyclerItemDecoration);
        fetchObjects(today);
        return view;
    }
    private String lowerCased(String location){
        return location.toLowerCase().replace(" ","");
    }
    public int getImage(String imageName){ return getResources().getIdentifier(imageName, "drawable",requireActivity().getPackageName()); }


    private RecyclerItemDecoration.SectionCallback getSectionCallback(final ArrayList<Post> list)
    {
        return new RecyclerItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int pos) {
                return pos==0 || !list.get(pos).getTopic().equals(list.get(pos - 1).getTopic());
            }

            @Override
            public String getSectionHeaderName(int pos) {
                Post post = list.get(pos);
                return post.getTopic();
            }
        };
    }


    @Override
    public void onPostItemClick(Post post, CardView cardView) {
        Intent intent = new Intent(getContext(), Sidebar.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", post);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(cardView, post.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }


    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }


    private void fetchObjects(String today){
        Query query = firebaseFirestore.collection(Constants.POSTS).orderBy("id",Query.Direction.DESCENDING).whereArrayContains("tags", today);
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

    private void fetchTopics(String location){

        Query query = firebaseFirestore.collection(Constants.TOPICS).orderBy("td",Query.Direction.ASCENDING).whereArrayContains("tags", location);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Topic object = documentChange.getDocument().toObject(Topic.class);
                        if (!topicList.contains(object)){
                            topicList.add(object);
                            topicsAdapter.notifyItemInserted(topicList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Topic object = documentChange.getDocument().toObject(Topic.class);
                        if (topicList.contains(object)){
                            topicList.set(topicList.indexOf(object),object);
                            topicsAdapter.notifyItemChanged(topicList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Topic object = documentChange.getDocument().toObject(Topic.class);
                        if (topicList.contains(object)){
                            topicList.remove(object);
                            topicsAdapter.notifyItemRemoved(topicList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onTopicItemClick(Topic topic, TextView textView) {
        startActivity(new Intent(requireContext(), TopicDetail.class).putExtra(Constants.OBJECT_ID,topic.getTd()));

    }
}
