package com.gachon.ccpp.parser;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ListForm implements Serializable {
    public String title;
    public String date;
    public String writer;
    public String link;
    public String payload;

    ListForm(String title,String date,String writer,String link, String payload){
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.link = link;
        this.payload = payload;
    }

    @NonNull
    @Override
    public String toString() {
        return "title: " + this.title
                + "\ndate: " + this.date
                + "\nwriter: " + this.writer
                + "\nlink: " + this.link
                + "\npayload: " + this.payload;
    }
}
