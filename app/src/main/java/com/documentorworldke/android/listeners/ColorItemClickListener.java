package com.documentorworldke.android.listeners;

import android.widget.ImageView;
import android.widget.TextView;

import com.documentorworldke.android.models.Colorr;

public interface ColorItemClickListener {

    void onColorItemClick(Colorr colorr, ImageView imageView, TextView textView, int position);
}
