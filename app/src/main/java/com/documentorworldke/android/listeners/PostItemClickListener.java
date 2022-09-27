package com.documentorworldke.android.listeners;

import androidx.cardview.widget.CardView;

import com.documentorworldke.android.models.Post;

public interface PostItemClickListener {

    void onPostItemClick(Post post, CardView cardView);
}
