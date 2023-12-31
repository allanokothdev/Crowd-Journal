package com.documentorworldke.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Topic implements Serializable {

    private String td; //ID
    private ArrayList<String> tags;
    private int rating;

    public Topic() {
    }

    public Topic(String td, ArrayList<String> tags, int rating) {
        this.td = td;
        this.tags = tags;
        this.rating = rating;
    }

    public String getTd() {
        return td;
    }

    public void setTd(String td) {
        this.td = td;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Topic topic = (Topic) obj;
        return td.matches(topic.getTd());
    }
}
