package com.gachon.ccpp;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.gachon.ccpp.parser.ListForm;

import java.util.ArrayList;

public class DeadlinePopupActivity extends Activity {
    RecyclerView recyclerView;
    PopUpAdapter adapter;
    private ArrayList<ListForm> schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_deadline_popup);
        schedule = (ArrayList<ListForm>) getIntent().getSerializableExtra("schedule");

        recyclerView = findViewById(R.id.daily_assignment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        adapter = new PopUpAdapter(schedule);
        recyclerView.setAdapter(adapter);

        Button btn = findViewById(R.id.close_btn);
        btn.setOnClickListener(view -> {
            finish();
        });

    }
}
