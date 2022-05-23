package com.gachon.ccpp.parser;

public class ListForm {
    public String title;
    public String date;
    public String writer;
    public String link;
    public String payload;

    ListForm(){
    }

    ListForm(String title,String date,String writer,String link, String payload){
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.link = link;
        this.payload = payload;
    }
}
