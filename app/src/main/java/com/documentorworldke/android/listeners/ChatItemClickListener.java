package com.documentorworldke.android.listeners;

import android.widget.ImageView;

import com.documentorworldke.android.models.Chat;

public interface ChatItemClickListener {

    void onChatItemClick(Chat chat, ImageView imageView);
}
