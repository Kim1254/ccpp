package com.gachon.ccpp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.gachon.ccpp.api.UserManager;
import com.gachon.ccpp.parser.HtmlParser;
import com.gachon.ccpp.parser.ListForm;

public class MainActivity extends AppCompatActivity {
    RetrofitClient retrofitClient;
    RetrofitAPI api;
    HtmlParser parser;

    private static final String baseUrl = "https://cyber.gachon.ac.kr";
    private static final String keyword = "/user/edit.php";

    LoadingDialog privateDialog;
    LectureFragment lecture;

    private UserManager userManager;

    private FragmentTransaction transaction;

    private String sourceId;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        sourceId = intent.getStringExtra("id");

        bundle = new Bundle();
        bundle.putSerializable("courseList", intent.getSerializableExtra("courseList"));

        getSupportActionBar().hide();

        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();
        parser = new HtmlParser();

        privateDialog = new LoadingDialog(this);

        infoRequest();

        lecture = new LectureFragment();
        AlarmFragment alarm = new AlarmFragment();
        ChatFragment chat = new ChatFragment();
        ScheduleFragment schedule = new ScheduleFragment();
        SettingFragment setting = new SettingFragment();

        lecture.setArguments(bundle);

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragLayout, lecture).commit();

        Button lectureButton = findViewById(R.id.footer_lecture);
        lectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragLayout, lecture).commit();
            }
        });

        Button scheduleButton = findViewById(R.id.footer_schedule);
        scheduleButton.setOnClickListener(view -> {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragLayout, schedule).commit();
        });

        Button alarmButton = findViewById(R.id.footer_alarm);
        alarmButton.setOnClickListener(view -> {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragLayout, alarm).commit();
        });

        Button chatButton = findViewById(R.id.footer_chat);
        chatButton.setOnClickListener(view -> {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragLayout, chat).commit();
        });

        Button settingButton = findViewById(R.id.footer_setting);
        settingButton.setOnClickListener( view -> {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragLayout, setting).commit();
        });
    }

    public void infoRequest() {
        privateDialog.show("9844-loading-40-paperplane.json", getString(R.string.LoadingDialog_TextLoading));

        Call<ResponseBody> connect = api.getUri("user/user_edit.php");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        parser.setHtml(html);
                        int studentId = Integer.parseInt(parser.getStudentInfo().get(0));
                        makeConnection(studentId);
                    } catch (Exception ignored) { }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
        privateDialog.hide();
    }

    public void makeConnection(int id) {
        userManager = new UserManager(sourceId, String.valueOf(id));
    }

    public void courseListRequest() {
        Call<ResponseBody> connect = api.getUri("");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ArrayList<ListForm> courseList = parser.getCourseList();
                        bundle.putSerializable("courseList", courseList);
                    } catch (Exception ignored) { }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_LONG).show();
            }
        });
    }
}