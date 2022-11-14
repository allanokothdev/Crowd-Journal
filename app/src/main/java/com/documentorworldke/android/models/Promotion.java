package com.documentorworldke.android.models;

import java.io.Serializable;

public class Promotion implements Serializable {

    private String pd;          //ID
    private String pic;         //PIC
    private String title;       //TITLE
    private String summary;     //SUMMARY
    private String cta;         //CALL TO ACTION
    private String link;        //WEB LINK
    private String publisher;   //PUBLISHER
    private Brand brand;        //BRAND

    public Promotion() {
    }

    public Promotion(String pd, String pic, String title, String summary, String cta, String link, String publisher, Brand brand) {
        this.pd = pd;
        this.pic = pic;
        this.title = title;
        this.summary = summary;
        this.cta = cta;
        this.link = link;
        this.publisher = publisher;
        this.brand = brand;
    }

    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCta() {
        return cta;
    }

    public void setCta(String cta) {
        this.cta = cta;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Promotion promotion = (Promotion) obj;
        return pd.matches(promotion.getPd());
    }
}
