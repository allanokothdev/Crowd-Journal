package com.documentorworldke.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.R;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder>{

    private final Context mContext;
    private final List<User> stringList;
    private final UserItemClickListener userItemClickListener;

    public AvatarAdapter(Context mContext, List<User> stringList, UserItemClickListener userItemClickListener){
        this.mContext = mContext;
        this.stringList = stringList;
        this.userItemClickListener = userItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.participant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final CircleImageView imageView;
        private final TextView textView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }

        void bind(int position) {
            try {
                User user = stringList.get(position);
                Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.cover).into(imageView);
                textView.setText(user.getName());
                itemView.setOnClickListener(view -> userItemClickListener.onUserItemClick(user,imageView));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
