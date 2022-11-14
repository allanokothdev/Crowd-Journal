package com.documentorworldke.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.R;
import com.documentorworldke.android.listeners.GalleryItemClickListener;
import com.documentorworldke.android.models.Darty;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Darty> stringList;
    private final GalleryItemClickListener galleryItemClickListener;

    public GalleryAdapter(Context mContext, List<Darty> stringList, GalleryItemClickListener galleryItemClickListener){
        this.mContext = mContext;
        this.stringList = stringList;
        this.galleryItemClickListener = galleryItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, parent, false);
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

        private final ImageView imageView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(int position) {

            final Darty darty = stringList.get(position);
            Glide.with(mContext).load(darty.getId()).placeholder(R.drawable.placeholder).into(imageView);
            itemView.setOnClickListener(v -> galleryItemClickListener.onGalleryItemClick(stringList,position, imageView));

        }
    }
}
