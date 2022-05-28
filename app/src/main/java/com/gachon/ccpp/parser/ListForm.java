package com.gachon.ccpp.parser;

import java.io.Serializable;

public class ListForm implements Serializable {
    public String title;
    public String date;
    public String writer;
    public String link;
    public String payload;
    public String image;

    ListForm(){
    }

    ListForm(String title,String date,String writer,String link, String payload, String image) {
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.link = link;
        this.payload = payload;
        this.image = image;
    }
}
