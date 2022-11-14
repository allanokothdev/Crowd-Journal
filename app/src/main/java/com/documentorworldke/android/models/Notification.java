package com.documentorworldke.android.models;

import java.io.Serializable;

public class Notification implements Serializable {

    private String nd;    //NOTIFICATION ID
    private String nr;    //NOTIFICATION RECIPIENT ID
    private String nb;    //NOTIFICATION PUBLISHER ID
    private String no;    //NOTIFICATION OBJECT ID
    private String ns;    //NOTIFICATION SUMMARY
    private Integer nt;   //NOTIFICATION TYPE
    private long nm;      //TIME
    private boolean nk;   //NOTIFICATION CHECKED

    public Notification() {
    }

    public Notification(String nd, String nr, String nb, String no, String ns, Integer nt, long nm, boolean nk) {
        this.nd = nd;
        this.nr = nr;
        this.nb = nb;
        this.no = no;
        this.ns = ns;
        this.nt = nt;
        this.nm = nm;
        this.nk = nk;
    }

    public String getNd() {
        return nd;
    }

    public void setNd(String nd) {
        this.nd = nd;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getNb() {
        return nb;
    }

    public void setNb(String nb) {
        this.nb = nb;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public Integer getNt() {
        return nt;
    }

    public void setNt(Integer nt) {
        this.nt = nt;
    }

    public long getNm() {
        return nm;
    }

    public void setNm(long nm) {
        this.nm = nm;
    }

    public boolean isNk() {
        return nk;
    }

    public void setNk(boolean nk) {
        this.nk = nk;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Notification notification = (Notification) obj;
        assert notification != null;
        return nd.matches(notification.getNd());
    }
}
