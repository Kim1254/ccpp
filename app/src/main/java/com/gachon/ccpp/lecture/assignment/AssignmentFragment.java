package com.gachon.ccpp.lecture.assignment;

import static com.gachon.ccpp.MainActivity.api;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gachon.ccpp.R;
import com.gachon.ccpp.dialog.LoadingDialog;
import com.gachon.ccpp.listener.onBackPressedListener;
import com.gachon.ccpp.parser.HtmlParser;
import com.gachon.ccpp.parser.TableForm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignmentFragment extends Fragment implements onBackPressedListener {
    TextView assignment_title;
    TextView assignment_content;
    TextView assignment_team;
    TextView assignment_attempt;
    TextView assignment_submission;
    TextView assignment_grade;
    TextView assignment_due;
    TextView assignment_time;
    TextView assignment_modified;
    TextView feedback_grade;
    TextView feedback_grade_on;
    TextView feedback_grade_by;
    TextView feedback_comments;
    ConstraintLayout feedback_layout;
    View temp;

    TableForm assignment;
    LoadingDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment, container, false);
        temp = view;

        Bundle bundle = getArguments();
        assignment = (TableForm) bundle.getSerializable("assignment");

        assignment_title = view.findViewById(R.id.assignment_fragment_title);
        assignment_content = view.findViewById(R.id.assignment_fragment_content);
        assignment_team = view.findViewById(R.id.assignment_team);
        assignment_attempt = view.findViewById(R.id.assignment_attempt);
        assignment_submission = view.findViewById(R.id.assignment_submission_status);
        assignment_grade = view.findViewById(R.id.assignment_grade_status);
        assignment_due = view.findViewById(R.id.assignment_due_date);
        assignment_time = view.findViewById(R.id.assignment_time_remain);
        assignment_modified = view.findViewById(R.id.assignment_modified);
        feedback_grade = view.findViewById(R.id.feedback_grade);
        feedback_grade_on = view.findViewById(R.id.feedback_graded_on);
        feedback_grade_by = view.findViewById(R.id.feedback_graded_by);
        feedback_comments = view.findViewById(R.id.feedback_comments);
        feedback_layout = view.findViewById(R.id.feedback_layout);

        requestContent(getArguments().getString("link"));
        dialog = new LoadingDialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        return view;
    }

    private void requestContent(String url) {
        Call<ResponseBody> connect = api.getUri(url);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        assignment = parser.getAssignment();
                        showData();
                        dialog.dismiss();
                    } catch (IOException e) {
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    private void showData(){

        int i=1;
        assignment_title.setText(assignment.table.get(0).get(0));
        assignment_content.setText(Html.fromHtml(assignment.table.get(0).get(1)));
        if(assignment.table.get(i).get(1).length()==1){
            assignment_team.setText(assignment.table.get(i++).get(1));
            assignment_team.setVisibility(View.VISIBLE);
            temp.findViewById(R.id.index_team).setVisibility(View.VISIBLE);
        }
        assignment_attempt.setText(assignment.table.get(i++).get(1));
        assignment_submission.setText(assignment.table.get(i++).get(1));
        assignment_grade.setText(assignment.table.get(i++).get(1));
        assignment_due.setText(assignment.table.get(i++).get(1));
        assignment_time.setText(assignment.table.get(i++).get(1));
        if(assignment.table.size()>i)assignment_modified.setText(assignment.table.get(i++).get(1));

        if(assignment.table.get(0).get(2).compareTo("1")==0){
            feedback_layout.setVisibility(View.VISIBLE);
            feedback_grade.setText(assignment.table.get(i++).get(1));
            feedback_grade_on.setText(assignment.table.get(i++).get(1));
            feedback_grade_by.setText(assignment.table.get(i++).get(1));
            if(assignment.table.size()>i){
                feedback_comments.setText(Html.fromHtml(assignment.table.get(i++).get(1)));
                feedback_comments.setVisibility(View.VISIBLE);
                temp.findViewById(R.id.index_comment).setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
}