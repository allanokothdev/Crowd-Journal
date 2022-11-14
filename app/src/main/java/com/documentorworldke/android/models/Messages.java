package com.documentorworldke.android.models;

import java.io.Serializable;

public class Messages implements Serializable {

    private String md;  //message ID
    private String sender;  //message sender
    private String message;  //message text content
    private String pic;  //message picture content
    private int type;     //message type
    private long timestamp;    //message Time
    private User user;  //PUBLISHER
    private String objectID;
    private String objectText;

    private Messages() {
    }

    public Messages(String md, String sender, String message, String pic, int type, long timestamp, User user, String objectID, String objectText) {
        this.md = md;
        this.sender = sender;
        this.message = message;
        this.pic = pic;
        this.type = type;
        this.timestamp = timestamp;
        this.user = user;
        this.objectID = objectID;
        this.objectText = objectText;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getObjectText() {
        return objectText;
    }

    public void setObjectText(String objectText) {
        this.objectText = objectText;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Messages messages = (Messages) obj;
        return md.matches(messages.getMd());
    }
}
