package com.documentorworldke.android.models;

import java.io.Serializable;

public class Colorr implements Serializable {

    private String cd; //COLOR ID
    private String ct; //COLOR Title
    private String cs; //COLOR STRING

    public Colorr() {
    }

    public Colorr(String cd, String ct, String cs) {
        this.cd = cd;
        this.ct = ct;
        this.cs = cs;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Colorr colorr = (Colorr) obj;
        return cd.matches(colorr.getCd());
    }
}
