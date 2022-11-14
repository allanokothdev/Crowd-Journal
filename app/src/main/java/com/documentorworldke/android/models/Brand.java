package com.documentorworldke.android.models;

import java.io.Serializable;

public class Brand implements Serializable {

    private String bd;      //brand ID
    private String title;   //brand name
    private String pic;     //brand picture
    private String publisher;

    public Brand() {
    }

    public Brand(String bd, String title, String pic, String publisher) {
        this.bd = bd;
        this.title = title;
        this.pic = pic;
        this.publisher = publisher;
    }

    public String getBd() {
        return bd;
    }

    public void setBd(String bd) {
        this.bd = bd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Brand brand = (Brand) obj;
        return bd.matches(brand.getBd());
    }
}
