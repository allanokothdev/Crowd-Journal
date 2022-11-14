package com.documentorworldke.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.R;
import com.documentorworldke.android.listeners.TopicItemClickListener;
import com.documentorworldke.android.models.Topic;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Topic> objectList;
    private final TopicItemClickListener topicItemClickListener;

    public TopicsAdapter(Context mContext, List<Topic> objectList, TopicItemClickListener topicItemClickListener) {
        this.mContext = mContext;
        this.objectList = objectList;
        this.topicItemClickListener = topicItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.topics_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
        }
        void bind(int position) {
            Topic topic = objectList.get(position);
            textView.setText(topic.getTd());
            itemView.setOnClickListener(v -> topicItemClickListener.onTopicItemClick(topic,textView));
        }
    }


}
