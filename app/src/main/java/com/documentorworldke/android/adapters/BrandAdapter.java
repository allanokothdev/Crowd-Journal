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
import com.documentorworldke.android.listeners.BrandItemClickListener;
import com.documentorworldke.android.models.Brand;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Brand> stringList;
    public BrandItemClickListener brandItemClickListener;

    public BrandAdapter(Context mContext, List<Brand> stringList, BrandItemClickListener brandItemClickListener){
        this.mContext = mContext;
        this.stringList = stringList;
        this.brandItemClickListener = brandItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
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

    public  class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
        }

        void bind(int position) {

            Brand brand = stringList.get(position);

            imageView.setTransitionName(brand.getBd());
            Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(imageView);
            textView.setText(brand.getTitle());
            subTextView.setText(brand.getTitle());
            itemView.setOnClickListener(v -> brandItemClickListener.onBrandItemClick(brand,imageView));
        }
    }

}


