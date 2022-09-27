package com.documentorworldke.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {

    private String id;
    private String conversationID;
    private String topic;
    private ArrayList<String> tags;
    private String publisher;
    private User user;
    private String text;
    private String image;
    private long timestamp;
    private String address;
    private String country;
    private String type;
    private String token;
    private double latitude;
    private double longitude;
    private int shares;
    private int likes;
    private int comments;

    public Post() {
    }

    public Post(String id, String conversationID, String topic, ArrayList<String> tags, String publisher, User user, String text, String image, long timestamp, String address, String country, String type, String token, double latitude, double longitude, int shares, int likes, int comments) {
        this.id = id;
        this.conversationID = conversationID;
        this.topic = topic;
        this.tags = tags;
        this.publisher = publisher;
        this.user = user;
        this.text = text;
        this.image = image;
        this.timestamp = timestamp;
        this.address = address;
        this.country = country;
        this.type = type;
        this.token = token;
        this.latitude = latitude;
        this.longitude = longitude;
        this.shares = shares;
        this.likes = likes;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Post post = (Post) obj;
        return id.matches(post.getId());
    }

}
