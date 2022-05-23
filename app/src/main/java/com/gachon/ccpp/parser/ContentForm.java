package com.gachon.ccpp.parser;

public class ContentForm {
    public String title;
    public String date;
    public String writer;
    public String content;
    public String payload;

    ContentForm(){
    }

    ContentForm(String title, String date, String writer, String content, String payload){
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.content = content;
        this. payload = payload;
    }
}
