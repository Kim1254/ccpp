package com.gachon.ccpp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;

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
import com.gachon.ccpp.LectureFragment.Lecture;

public class MainActivity extends AppCompatActivity {
    RetrofitClient retrofitClient;
    RetrofitAPI api;

    private static final String baseUrl = "https://cyber.gachon.ac.kr/user/edit.php";
    private static final String defaultImage = "https://cyber.gachon.ac.kr/theme/image.php/coursemosv2/core/1637637992/u/f1";

    LoadingDialog privateDialog;

    private UserManager userManager;

    private FragmentManager fragManager;
    private FragmentTransaction transaction;

    private LectureFragment lectureFrag;

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

        lectureFrag = new LectureFragment();

        infoRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        privateDialog.hide(); // ??
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
                            if (link.contains(baseUrl)) {
                                connRequest(link.substring((baseUrl).length() + 4));
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
                        Elements elements = html.select(".course_box");

                        lectureFrag.lecture_list.clear();
                        for (Element e : elements) {
                            String link = e.select("a").first().attr("href");
                            String name = e.select(".course-title h3").first().text();
                            String prof = e.select(".course-title p").first().text();
                            String image = e.select(".course-image img").first().attr("src");

                            if (name.substring(name.length() - 3).contentEquals("NEW"))
                                name = name.substring(0, name.length() - 3);

                            String id = name.substring(name.length() - 11);
                            name = name.substring(0, name.length() - 12);

                            if (image.contentEquals(defaultImage))
                                lectureFrag.lecture_list.add(new Lecture(name, id, prof, link));
                            else
                                lectureFrag.lecture_list.add(new Lecture(name, id, prof, link, image));
                        }

                        deployLecture();
                    } catch (IOException e) {
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
        transaction.replace(R.id.fragLayout, lectureFrag).commitAllowingStateLoss();
        privateDialog.hide();
    }
}