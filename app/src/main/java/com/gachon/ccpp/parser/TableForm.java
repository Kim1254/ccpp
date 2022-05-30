package com.gachon.ccpp.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableForm {
    //Map<String,String> row = new HashMap<String,String>();
    ArrayList<Map> table;

    TableForm(){
        table = new ArrayList<>();
    }
    TableForm(ArrayList<Map> table){
        this.table = table;
    }

    public void addRow(Map row){
        this.table.add(row);
    }

}
