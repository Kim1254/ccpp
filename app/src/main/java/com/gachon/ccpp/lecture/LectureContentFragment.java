package com.gachon.ccpp.lecture;

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

import com.gachon.ccpp.MainActivity;
import com.gachon.ccpp.R;
import com.gachon.ccpp.lecture.announcement.AnnouncementFragment;
import com.gachon.ccpp.lecture.assignment.AssignmentListFragment;
import com.gachon.ccpp.lecture.grade.GradeFragment;
import com.gachon.ccpp.lecture.syllabus.SyllabusFragment;
import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;
import com.gachon.ccpp.listener.onBackPressedListener;
import com.gachon.ccpp.parser.ContentForm;
import com.gachon.ccpp.parser.HtmlParser;
import com.gachon.ccpp.parser.ListForm;
import com.gachon.ccpp.parser.TableForm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LectureContentFragment extends Fragment implements onBackPressedListener {
    private RetrofitClient retrofitClient;
    private RetrofitAPI api;

    private String id;
    private ListForm lecture;

    private ImageButton announcement_button;
    private ImageButton syllabus_button;
    private ImageButton grade_button;
    private ImageButton assignment_button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        Bundle bundle = getArguments();
        lecture = (ListForm)bundle.getSerializable("lecture");
        id=lecture.link.split("=")[1];

        ActionBar ac = ((MainActivity)getActivity()).getSupportActionBar();
        ac.setTitle(lecture.title);
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

        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("link",lecture.link);

        announcement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnnouncementFragment announcement = new AnnouncementFragment();
                announcement.setArguments(bundle);
                deployFragment(announcement);
            }
        });

        syllabus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyllabusFragment syllabusFragment = new SyllabusFragment();
                syllabusFragment.setArguments(bundle);
                deployFragment(syllabusFragment);
            }
        });

        grade_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GradeFragment gradeFragment = new GradeFragment();
                gradeFragment.setArguments(bundle);
                deployFragment(gradeFragment);
            }
        });

        assignment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignmentListFragment assignmentListFragment = new AssignmentListFragment();
                assignmentListFragment.setArguments(bundle);
                deployFragment(assignmentListFragment);
            }
        });

        return view;
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
