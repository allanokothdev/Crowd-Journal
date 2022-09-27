package com.documentorworldke.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Notification implements Serializable {

    private String id;
    private String image;
    private String title;
    private String message;
    private String topic;
    private String token;
    private String objectID;
    private ArrayList<String> tags;

    public Notification(){ }

    public Notification(String id, String image, String title, String message, String topic, String token, String objectID, ArrayList<String> tags) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.message = message;
        this.topic = topic;
        this.token = token;
        this.objectID = objectID;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Notification chat = (Notification)obj;
        return id.matches(chat.getId());
    }
}
