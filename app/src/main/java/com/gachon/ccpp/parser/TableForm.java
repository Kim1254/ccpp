package com.gachon.ccpp.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableForm implements Serializable {
    public ArrayList<Map<Integer,String>> table;

    public TableForm(){
        table = new ArrayList<>();
    }
    TableForm(ArrayList<Map<Integer,String>> table){
        this.table = table;
    }

    public void addRow(Map row){
        this.table.add(row);
    }

}
