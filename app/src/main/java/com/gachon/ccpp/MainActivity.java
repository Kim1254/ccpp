package com.gachon.ccpp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;

import org.jsoup.Jsoup;

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

    LoadingDialog privateDialog;

    Bundle bundle;

    private UserManager userManager;

    private FragmentManager fragManager;
    private FragmentTransaction transaction;

    private LectureFragment lecture;
    private ScheduleFragment schedule;
    private AlarmFragment alarm;
    private ChatFragment chat;
    private SettingFragment setting;

    public ArrayList<ListForm> scheduleList;

    private String sourceId;

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

        privateDialog = new LoadingDialog(this);

        fragManager = getSupportFragmentManager();

        lecture = new LectureFragment();
        schedule = new ScheduleFragment();
        alarm = new AlarmFragment();
        chat = new ChatFragment();
        setting = new SettingFragment();

        lecture.setArguments(bundle);

        infoRequest();

        scheduleList = new ArrayList<>();
        requestSchedule();

        findViewById(R.id.footer_lecture).setOnClickListener(view ->
                deployFragment(R.string.MainFragment_Lecture_Title, lecture));
        findViewById(R.id.footer_schedule).setOnClickListener(view ->
                deployFragment(R.string.MainFragment_Schedule_Title, schedule));
        findViewById(R.id.footer_alarm).setOnClickListener(view ->
                deployFragment(R.string.MainFragment_Alarm_Title, alarm));
        findViewById(R.id.footer_chat).setOnClickListener(view ->
                deployFragment(R.string.MainFragment_Chat_Title, chat));
        findViewById(R.id.footer_setting).setOnClickListener(view ->
                deployFragment(R.string.MainFragment_Setting_Title, setting));
    }

    @Override
    protected void onResume() {
        super.onResume();
        privateDialog.hide();
    }

    // 일정이 있는날만 뽑아와서 requestDaySchedule 호출
    public void requestSchedule() {
        Call<ResponseBody> connect = api.getUri("calendar/view.php?view=month");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ArrayList<ListForm> monthList = parser.getMonthList();
                        for (ListForm l : monthList)
                            requestDaySchedule(l.date, l.link);
                    } catch (Exception ignored) { }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    //일정이 있는날 과제만 파싱해서 가져옴
    public void requestDaySchedule(String day, String link) {
        Call<ResponseBody> connect = api.getUri(link);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ArrayList<ListForm> dayList = parser.getDayList();
                        for(ListForm l : dayList){
                            l.date = day;
                            scheduleList.add(l);
                        }
                    } catch (Exception ignored) { }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    public void infoRequest() {
        privateDialog.show("9844-loading-40-paperplane.json", getString(R.string.LoadingDialog_TextLoading));

        Call<ResponseBody> connect = api.getUri("");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        parser.setHtml(Jsoup.parse(response.body().string()));
                        makeConnection(parser.getStudentInfo().get(0));
                    } catch (Exception ignored) {
                    } finally {
                        privateDialog.hide();
                        deployFragment(R.string.MainFragment_Lecture_Title, lecture);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                privateDialog.hide();
                deployFragment(R.string.MainFragment_Lecture_Title, lecture);
            }
        });
    }

    public void makeConnection(String id) {
        userManager = new UserManager(sourceId, id);
    }

    public void deployFragment(int title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().show();

        transaction = fragManager.beginTransaction();
        transaction.replace(R.id.fragLayout, fragment).commit();
    }
}