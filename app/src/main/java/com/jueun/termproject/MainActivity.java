package com.jueun.termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    GridView gridView;

    String[] lectureWord = {"One", "Two", "Three", "Four", "Five", "Six"};
    int[] lectureImage = {R.drawable.bell_48, R.drawable.home_48, R.drawable.calendar_48,
            R.drawable.menu_48, R.drawable.comments_48, R.drawable.bell_48};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
//                    case R.id.first_tab:
//                        Intent homeIntent = new Intent(this, MainActivity.class);
//                        startActivity(homeIntent);
//                        return true;
//                    case R.id.second_tab:
//                        return true;
//                    case R.id.third_tab:
//                        Intent notiIntent = new Intent(this, notification_Activity.class);
                }
                return false;
            }
        });

        gridView = findViewById(R.id.gridView);

        LectureAdapter lectureAdapter = new LectureAdapter(MainActivity.this, lectureWord, lectureImage);
        gridView.setAdapter(lectureAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Toast.makeText(getApplicationContext(), lectureWord[position] + "을 클릭했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}