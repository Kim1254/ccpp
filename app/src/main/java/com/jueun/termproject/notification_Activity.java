package com.jueun.termproject;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jueun.termproject.databinding.ActivityNotificationBinding;

public class notification_Activity extends AppCompatActivity {

    GridView gridView;

    String[] weeklyCourseWord = {"Week 1\n03.02 - 03.08", "Week 2\n03.09 - 03.15", "Week 3\n03.16 - 03.22", "Week 4\n03.23 - 03.29",
            "Week 5\n03.30 - 04.05", "Week 6\n04.06 - 04.12", "Week 7\n04.13 - 04.19", "Week 8\n04.20 - 04.26", "Week 9\n04.27 - 05.03",
            "Week 10\n05.04 - 05.10", "Week 11\n05.11 - 05.18"};
    int[] weeklyCourseImage = {R.drawable.bell_48, R.drawable.home_48, R.drawable.calendar_48,
            R.drawable.menu_48, R.drawable.comments_48, R.drawable.bell_48};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_layout);

        gridView = findViewById(R.id.weeklyCourseGridView);

        WeeklyCourseAdapter weeklyCourseAdapter = new WeeklyCourseAdapter(notification_Activity.this, weeklyCourseWord, weeklyCourseImage);
        gridView.setAdapter((ListAdapter) weeklyCourseAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Toast.makeText(getApplicationContext(), weeklyCourseWord[position] + "을 클릭했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}