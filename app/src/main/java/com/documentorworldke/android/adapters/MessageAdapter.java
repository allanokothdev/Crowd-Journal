package com.documentorworldke.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.R;
import com.documentorworldke.android.models.Messages;
import com.documentorworldke.android.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final List<Messages> objectList;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public MessageAdapter(Context mContext, List<Messages> objectList) {
        this.mContext = mContext;
        this.objectList = objectList;
    }

    @Override
    public int getItemViewType(int position) {

        Messages messages = objectList.get(position);

        if ((messages.getSender().equals(currentUserID) && (messages.getType()==0))){ //text Right

            return MSG_TYPE_RIGHT;

        }else if ((!messages.getSender().equals(currentUserID) && (messages.getType()==0))){ //TEXT left

            return MSG_TYPE_LEFT;

        }else {

            return MSG_TYPE_LEFT;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){

            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false));

        }else if (viewType == MSG_TYPE_LEFT){

            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false));

        }else {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try {
            final Messages messages = objectList.get(position);

            if ((messages.getSender().equals(currentUserID) && (messages.getType()==0))){ //text Right
                ((ViewHolder) holder).bind(position);

            }else if ((!messages.getSender().equals(currentUserID) && (messages.getType()==0))){ //text left
                ((ViewHolder) holder).bind(position);

            }
        }catch (Exception e){e.printStackTrace();}
    }



    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public TextView timeTextView;
        public TextView messageTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }

        void bind(int position) {

            final Messages messages = objectList.get(position);
            messageTextView.setText(messages.getMessage());
            getUserDetails(messages.getUser(), textView, imageView);
        }
    }

    private void getUserDetails(User user, final TextView textView, final ImageView imageView){
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(user.getName());
    }

    public String getLastItemKey(){
        return objectList.get(objectList.size() - 1).getMd();
    }

    public String getFirstItemKey(){
        return objectList.get(0).getMd();
    }


}
