package com.documentorworldke.android.models;

import java.io.Serializable;

public class Chat implements Serializable {

    private String ud;  //ID
    private String pic;
    private String title;
    private boolean read; //TYPE
    private Long timestamp;    //TIME
    private String message;  //MESSAGE


    public Chat(){ }

    public Chat(String ud, String pic, String title, boolean read, Long timestamp, String message) {
        this.ud = ud;
        this.pic = pic;
        this.title = title;
        this.read = read;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getUd() {
        return ud;
    }

    public void setUd(String ud) {
        this.ud = ud;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Chat chat = (Chat)obj;
        return ud.matches(chat.getUd());
    }
}
