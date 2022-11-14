package com.documentorworldke.android.listeners;

import android.widget.ImageView;
import com.documentorworldke.android.models.Darty;

import java.util.List;

public interface GalleryItemClickListener {

    void onGalleryItemClick(List<Darty> list, int position,  ImageView imageView);
}
