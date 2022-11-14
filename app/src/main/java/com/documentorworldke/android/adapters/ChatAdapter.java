package com.documentorworldke.android.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.R;
import com.documentorworldke.android.listeners.ChatItemClickListener;
import com.documentorworldke.android.models.Chat;
import com.documentorworldke.android.utils.PostGetTimeAgo;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Chat> objectList;
    private final ChatItemClickListener chatItemClickListener;

    public ChatAdapter(Context mContext, List<Chat> objectList, ChatItemClickListener chatItemClickListener){
        this.mContext = mContext;
        this.objectList = objectList;
        this.chatItemClickListener = chatItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_listitem, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subItemTextView;
        private final TextView subTextView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            subItemTextView = itemView.findViewById(R.id.subItemTextView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            subTextView = itemView.findViewById(R.id.subTextView);
        }

        void bind(int position) {

            Chat chat = objectList.get(position);
            imageView.setTransitionName(chat.getUd());
            subTextView.setText(chat.getMessage());
            subItemTextView.setText(PostGetTimeAgo.postGetTimeAgo(chat.getTimestamp(), mContext));
            Glide.with(mContext.getApplicationContext()).load(chat.getPic()).placeholder(R.drawable.cover).into(imageView);
            textView.setText(chat.getTitle());
            itemView.setOnClickListener(v -> chatItemClickListener.onChatItemClick(chat, imageView));

            if (chat.isRead()){
                subTextView.setTypeface(null, Typeface.NORMAL);
            } else {
                subTextView.setTypeface(null, Typeface.BOLD);
            }

        }
    }

    public String getLastItemKey(){ return objectList.get(objectList.size() - 1).getUd(); }
    public String getFirstItemKey(){ return objectList.get(0).getUd(); }

}
