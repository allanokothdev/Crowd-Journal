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
import com.documentorworldke.android.models.Agenda;
import com.documentorworldke.android.models.Brand;

import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Agenda> stringList;

    public AgendaAdapter(Context mContext, List<Agenda> stringList){
        this.mContext = mContext;
        this.stringList = stringList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.agenda_item, parent, false);
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
        private final TextView subItemTextView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
        }

        void bind(int position) {

            Agenda agenda = stringList.get(position);
            Glide.with(mContext.getApplicationContext()).load(agenda.getPic()).placeholder(R.drawable.cover).into(imageView);
            textView.setText(agenda.getName());
            subTextView.setText(agenda.getRecipient());
            subItemTextView.setText(agenda.getSummary());
        }
    }

}


