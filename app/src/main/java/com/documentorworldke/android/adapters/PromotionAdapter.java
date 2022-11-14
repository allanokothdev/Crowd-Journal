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
import com.documentorworldke.android.listeners.PromotionItemClickListener;
import com.documentorworldke.android.models.Promotion;

import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Promotion> stringList;
    private final PromotionItemClickListener promotionItemClickListener;

    public PromotionAdapter(Context mContext, List<Promotion> stringList, PromotionItemClickListener promotionItemClickListener){
        this.mContext = mContext;
        this.stringList = stringList;
        this.promotionItemClickListener = promotionItemClickListener;
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

            Promotion promotion = stringList.get(position);
            imageView.setTransitionName(promotion.getPd());
            Glide.with(mContext.getApplicationContext()).load(promotion.getPic()).placeholder(R.drawable.placeholder).into(imageView);
            itemView.setOnClickListener(v -> promotionItemClickListener.onPromotionItemClick(promotion,imageView));

        }
    }



}
