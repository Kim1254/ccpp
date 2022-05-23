package com.gachon.ccpp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.gachon.ccpp.api.UserManager;

public class MainActivity extends AppCompatActivity {
    RetrofitClient retrofitClient;
    RetrofitAPI api;

    private static final String baseUrl = "https://cyber.gachon.ac.kr";
    private static final String keyword = "/user/edit.php";

    LoadingDialog privateDialog;

    private UserManager userManager;

    private FragmentManager fragManager;
    private FragmentTransaction transaction;

    private LectureFragment Lecture;

    private String sourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        sourceId = intent.getStringExtra("id");

        getSupportActionBar().hide();

        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        privateDialog = new LoadingDialog(this);

        Lecture = new LectureFragment();

        infoRequest();
    }

    public void infoRequest() {
        privateDialog.show("9844-loading-40-paperplane.json",
                getString(R.string.LoadingDialog_TextLoading));

        Call<ResponseBody> connect = api.getUri("");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        Elements article = html.select(".user-info-submenu.clearfix");
                        if (article.size() != 0) {
                            article = article.select("li.items");

                            String link = article.first().select("a").attr("abs:href");
                            if (link.contains(baseUrl + keyword)) {
                                connRequest(link.substring((baseUrl + keyword).length() + 4));
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void connRequest(String id) {
        Call<ResponseBody> connect = api.info(id);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        Elements article = html.select(".felement");
                        if (article.size() != 0) {
                            for (Element e : article) {
                                try {
                                    int num = Integer.parseInt(e.text());
                                    makeConnection(num);
                                    break;
                                } catch (NumberFormatException exception) {
                                    continue;
                                }
                            }
                        }
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });

        homeRequest();
    }

    public void makeConnection(int id) {
        userManager = new UserManager(sourceId, String.valueOf(id));
    }

    public void homeRequest() {
        Call<ResponseBody> connect = api.getUri("");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        Elements elements = html.select(".course-title h3");

                        JSONObject data = new JSONObject();
                        for (Element e : elements)
                            data.put(e.text(), "");

                        Lecture.appendList(data.toString());
                        deployLecture();
                    } catch (IOException | JSONException e) {
                        privateDialog.hide();
                        String text = getString(R.string.LoginActivity_LoginParseError, e.getMessage());
                        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                    }
                } else {
                    privateDialog.hide();
                    Toast.makeText(MainActivity.this,
                            R.string.LoginActivity_LoginNoResponse, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                privateDialog.hide();
                String text = getString(R.string.LoginActivity_LoginOnFailure, t.getMessage());
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deployLecture() {
        getSupportActionBar().setTitle(R.string.MainFragment_Lecture_Title);
        getSupportActionBar().show();

        fragManager = getSupportFragmentManager();

        transaction = fragManager.beginTransaction();
        transaction.replace(R.id.fragLayout, Lecture).commitAllowingStateLoss();
        privateDialog.hide();
    }
}