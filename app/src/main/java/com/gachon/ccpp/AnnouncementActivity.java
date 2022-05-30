package com.gachon.ccpp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ccpp.parser.ContentForm;
import com.gachon.ccpp.parser.ListForm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AnnouncementActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AnnouncementAdapter adapter;
    ArrayList<ContentForm> announcementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Announcement");
        setContentView(R.layout.activity_announcement);
        announcementList = (ArrayList<ContentForm>) getIntent().getSerializableExtra("list");
        Collections.sort(announcementList, (a, b) -> Integer.valueOf(b.payload)- Integer.valueOf(a.payload));

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ;

        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
