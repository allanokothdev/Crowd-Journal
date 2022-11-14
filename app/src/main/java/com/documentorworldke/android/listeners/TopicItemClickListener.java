package com.documentorworldke.android.listeners;

import android.widget.TextView;

import com.documentorworldke.android.models.Topic;

public interface TopicItemClickListener {
    void onTopicItemClick(Topic topic, TextView textView);
}
