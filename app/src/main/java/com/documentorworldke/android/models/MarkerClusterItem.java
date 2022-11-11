package com.documentorworldke.android.models;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.annotations.NotNull;
import com.google.maps.android.clustering.ClusterItem;


public class MarkerClusterItem implements ClusterItem {

    private LatLng latLng;
    private String title;
    private String postID;

    public MarkerClusterItem(@NonNull LatLng latLng, String title, @NonNull String postID) {
        this.latLng = latLng;
        this.title = title;
        this.postID = postID;
    }

    @NotNull
    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return postID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}