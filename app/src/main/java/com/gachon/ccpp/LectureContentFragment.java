package com.gachon.ccpp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;
import com.gachon.ccpp.parser.ContentForm;
import com.gachon.ccpp.parser.HtmlParser;
import com.gachon.ccpp.parser.ListForm;
import com.gachon.ccpp.parser.TableForm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LectureContentFragment extends Fragment implements onBackPressedListener {
    private RetrofitClient retrofitClient;
    private RetrofitAPI api;

    private String id;

    private ArrayList<ContentForm> announcement_content;
    private String syllabus;
    private TableForm assignment;
    private TableForm grade;

    private ImageButton announcement_button;
    private ImageButton syllabus_button;
    private ImageButton grade_button;
    private ImageButton assignment_button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        announcement_content = new ArrayList<>();

        Bundle bundle = getArguments();
        ListForm lecture = (ListForm)bundle.getSerializable("lecture");
        id=lecture.link.split("=")[1];

        ActionBar ac = ((MainActivity)getActivity()).getSupportActionBar();
        ac.setTitle(lecture.title);

        requestContent(lecture.link);
        requestContents(id);;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture_content, container, false);

        announcement_button = view.findViewById(R.id.announcement_button);
        syllabus_button = view.findViewById(R.id.syllabus_button);
        grade_button = view.findViewById(R.id.grade_button);
        assignment_button = view.findViewById(R.id.assignment_button);

        announcement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnnouncementFragment announcement = new AnnouncementFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list",announcement_content);
                announcement.setArguments(bundle);
                deployFragment(announcement);
            }
        });

        syllabus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyllabusFragment syllabusFragment = new SyllabusFragment();
                Bundle bundle = new Bundle();
                bundle.putString("syllabus",syllabus);
                syllabusFragment.setArguments(bundle);
                deployFragment(syllabusFragment);
            }
        });

        grade_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GradeFragment gradeFragment = new GradeFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list",grade);
                gradeFragment.setArguments(bundle);
                deployFragment(gradeFragment);
            }
        });

        assignment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignmentListFragment assignmentListFragment = new AssignmentListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", assignment);
                assignmentListFragment.setArguments(bundle);
                deployFragment(assignmentListFragment);
            }
        });

        return view;
    }

    private void requestContents(String id){
        requestSyllabus(id);
        requestGrade(id);
        requestAssignment(id);
    }

    private void requestSyllabus(String id) {
        Call<ResponseBody> connect = api.syllabus(id);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        syllabus = parser.getSyllabus();
                    } catch (IOException e) {
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    private void requestGrade(String id) {
        Call<ResponseBody> connect = api.grade(id);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        grade = parser.getGrade();
                    } catch (IOException e) {
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    private void requestAssignment(String id) {
        Call<ResponseBody> connect = api.assignment(id);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        assignment = parser.getAllAssignment();
                    } catch (IOException e) {
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    private void requestContent(String url) {
        Call<ResponseBody> connect = api.getUri(url);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        String link = parser.getClassAnnouncementLink();
                        requestAnnouncementsLink(link);
                    } catch (IOException e) {
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    public void requestAnnouncementsLink(String link) {
        Call<ResponseBody> connect = MainActivity.api.getUri(link+"&ls=100");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ArrayList<ListForm> announcementLink = parser.getCourseAnnouncementList();
                        for(ListForm l :announcementLink){
                            requestAnnouncements(l.link,new ContentForm("","","","",l.payload));
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

    public void requestAnnouncements(String link,ContentForm content) {
        Call<ResponseBody> connect = MainActivity.api.getUri(link);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ContentForm data = parser.getAnnouncementContent();
                        data.payload = content.payload;
                        announcement_content.add(data);
                    } catch (Exception e) {
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void deployFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragLayout, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
}
