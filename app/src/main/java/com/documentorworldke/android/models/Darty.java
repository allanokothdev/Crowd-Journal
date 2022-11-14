package com.documentorworldke.android.models;

import java.io.Serializable;

public class Darty implements Serializable {

    private String id;

    public Darty() {
    }

    public Darty(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Darty darty = (Darty) obj;
        return id.matches(darty.getId());
    }
}
