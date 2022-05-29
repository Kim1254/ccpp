package com.gachon.ccpp.parser;

import java.io.Serializable;

public class ContentForm implements Serializable {
    public String title;
    public String date;
    public String writer;
    public String content;
    public String payload;

    ContentForm(){
    }

    public ContentForm(String title, String date, String writer, String content, String payload){
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.content = content;
        this. payload = payload;
    }
}
