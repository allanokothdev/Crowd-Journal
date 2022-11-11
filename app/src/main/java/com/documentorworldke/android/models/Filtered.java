package com.documentorworldke.android.models;

import java.io.Serializable;

public class Filtered  implements Serializable {

    String topic;
    long startDate;
    long endDate;

    public Filtered() {
    }

    public Filtered(String topic, long startDate, long endDate) {
        this.topic = topic;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
