package com.documentorworldke.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.R;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.ObjectItemClickListener;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.models.Notification;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    public static final int DEFAULT_NOTIFICATION = 0;
    public static final int POST_NOTIFICATION = 1;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final List<Notification> objectList;
    private final PostItemClickListener postItemClickListener;
    private final ObjectItemClickListener objectItemClickListener;

    public NotificationAdapter(Context mContext, List<Notification> objectList, PostItemClickListener postItemClickListener, ObjectItemClickListener objectItemClickListener) {
        this.objectList = objectList;
        this.mContext = mContext;
        this.objectItemClickListener = objectItemClickListener;
        this.postItemClickListener = postItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = objectList.get(position);
        if (notification.getNt() == 0){
            return DEFAULT_NOTIFICATION;
        }else {
            return POST_NOTIFICATION;
        }
    }

    @NonNull
    @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType== POST_NOTIFICATION){
            return new PostViewHolder(LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false));
        }else {
            return new DefaultViewHolder(LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Notification notification = objectList.get(position);
        if (notification.getNt() == 0){
            ((DefaultViewHolder) holder).bind(position);
        }else {
            ((PostViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder {

        private final ImageView typeImageView;
        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;

        public DefaultViewHolder(@NonNull View itemView) {
            super(itemView);

            typeImageView = itemView.findViewById(R.id.typeImageView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
        }

        void bind(int position) {
            Notification notification = objectList.get(position);
            fetchUserObject(notification.getNb(),imageView, textView);
            subTextView.setText(notification.getNs());
            Glide.with(mContext.getApplicationContext()).load(R.drawable.ic_likes).into(typeImageView);
            itemView.setOnClickListener(v -> objectItemClickListener.onObjectDisplayClick("qwertyuiop",imageView));
        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private final ImageView typeImageView;
        private final CardView cardView;
        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            typeImageView = itemView.findViewById(R.id.typeImageView);
            imageView = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
        }

        void bind(int position) {
            Notification notification = objectList.get(position);
            fetchUserObject(notification.getNb(),imageView, textView);
            subTextView.setText(notification.getNs());
            Glide.with(mContext.getApplicationContext()).load(R.drawable.ic_likes).into(typeImageView);
            fetchPostObject(notification.getNo(),itemView, cardView);
        }
    }

    private void fetchPostObject(String objectID, final View itemView, final CardView cardView){
        firebaseFirestore.collection(Constants.POSTS).document(objectID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Post post = documentSnapshot.toObject(Post.class);
                itemView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post, cardView));
            }
        });
    }

    private void fetchUserObject(String publisherID, final ImageView imageView, final TextView textView){
        firebaseFirestore.collection(Constants.USERS).document(publisherID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
                textView.setText(user.getName());
                if (user.isVerification()){
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,R.drawable.baseline_verified_24,0);
                }else {
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,0,0);
                }
            }
        });
    }


    public String getLastItemKey(){
        return objectList.get(objectList.size() - 1).getNd();
    }

    public String getFirstItemKey(){
        return objectList.get(0).getNd();
    }
}
