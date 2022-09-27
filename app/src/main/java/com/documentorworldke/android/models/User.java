package com.documentorworldke.android.models;

import java.io.Serializable;

public class User implements Serializable {

    private String id; //ID
    private String pic; //PIC
    private String name; //NAME
    private String username;
    private String email;
    private String phone;
    private String country;
    private String token;
    private boolean verification;
    private boolean reported;

    public User() {
    }

    public User(String id, String pic, String name, String username, String email, String phone, String country, String token, boolean verification, boolean reported) {
        this.id = id;
        this.pic = pic;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.token = token;
        this.verification = verification;
        this.reported = reported;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        User user = (User) obj;
        return id.matches(user.getId());
    }
}
