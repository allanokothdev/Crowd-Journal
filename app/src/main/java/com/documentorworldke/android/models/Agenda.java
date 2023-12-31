package com.documentorworldke.android.models;

import java.io.Serializable;

public class Agenda implements Serializable {

    private String recipient;
    private String name;
    private String pic;
    private String topic;
    private String time;
    private String summary;

    public Agenda() {
    }

    public Agenda(String recipient, String name, String pic, String topic, String time, String summary) {
        this.recipient = recipient;
        this.name = name;
        this.pic = pic;
        this.topic = topic;
        this.time = time;
        this.summary = summary;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
