package com.documentorworldke.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Comments implements Serializable {

    //COMMENT DETAILS
    private String id;  //comment id
    private String publisherID;
    private User user;
    private String token;
    private String image;
    private String text;  //text
    private long timestamp;  //comment time
    private String type;
    private boolean reported;
    private int shares;
    private int likes;
    private int comments;
    private ArrayList<String> tags;

    public Comments() {
    }

    public Comments(String id, String publisherID, User user, String token, String image, String text, long timestamp, String type, boolean reported, int shares, int likes, int comments, ArrayList<String> tags) {
        this.id = id;
        this.publisherID = publisherID;
        this.user = user;
        this.token = token;
        this.image = image;
        this.text = text;
        this.timestamp = timestamp;
        this.type = type;
        this.reported = reported;
        this.shares = shares;
        this.likes = likes;
        this.comments = comments;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Comments comment = (Comments)obj;
        return id.matches(comment.getId());
    }
}
