package com.documentorworldke.android.models;

import java.io.Serializable;

public class Participant implements Serializable {

    private String id;  //ID
    private User user;  //USER
    private String role;  //ROLE
    private boolean speaking; //VERIFICATION

    public Participant() {
    }


    public Participant(String id, User user, String role, boolean speaking) {
        this.id = id;
        this.user = user;
        this.role = role;
        this.speaking = speaking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSpeaking() {
        return speaking;
    }

    public void setSpeaking(boolean speaking) {
        this.speaking = speaking;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Participant user = (Participant) obj;
        return id.matches(user.getId());
    }
}
