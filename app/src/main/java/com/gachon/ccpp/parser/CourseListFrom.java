package com.gachon.ccpp.parser;

import java.util.ArrayList;

public class CourseListFrom {
    public String announcement;
    public String week;
    public ArrayList<ListForm> list;

    CourseListFrom(){
    }

    CourseListFrom(String announcement,String week, ArrayList<ListForm> list){
        this.announcement = announcement;
        this.week = week;
        this.list = list;
    }
}
