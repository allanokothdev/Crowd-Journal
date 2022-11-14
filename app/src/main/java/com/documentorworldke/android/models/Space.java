package com.documentorworldke.android.models;

import java.io.Serializable;
import java.util.List;

public class Space implements Serializable {

    private String id;          //ID
    private String title;       //TITLE
    private String topic;     //TOPIC
    private String color;     //COLOR
    private String date;    //DATE
    private String publisher;
    private List<User> panel;   //SPEAKERS

    public Space() {
    }


    public Space(String id, String title, String topic, String color, String date, String publisher, List<User> panel) {
        this.id = id;
        this.title = title;
        this.topic = topic;
        this.color = color;
        this.date = date;
        this.publisher = publisher;
        this.panel = panel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<User> getPanel() {
        return panel;
    }

    public void setPanel(List<User> panel) {
        this.panel = panel;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Space space = (Space) obj;
        return id.matches(space.getId());
    }
}
