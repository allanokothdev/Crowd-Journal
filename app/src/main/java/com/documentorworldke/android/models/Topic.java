package com.documentorworldke.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Topic implements Serializable {

    private String td; //ID
    private ArrayList<String> tags;

    public Topic() {
    }

    public Topic(String td, ArrayList<String> tags) {
        this.td = td;
        this.tags = tags;
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

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Topic topic = (Topic) obj;
        return td.matches(topic.getTd());
    }
}
