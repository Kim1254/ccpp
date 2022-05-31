package com.gachon.ccpp;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gachon.ccpp.parser.TableForm;

public class AssignmentFragment extends Fragment implements onBackPressedListener{
    TableForm assignment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment, container, false);

        Bundle bundle = getArguments();
        assignment = (TableForm) bundle.getSerializable("assignment");

        TextView assignment_title = view.findViewById(R.id.assignment_fragment_title);
        TextView assignment_content = view.findViewById(R.id.assignment_fragment_content);
        TextView assignment_team = view.findViewById(R.id.assignment_team);
        TextView assignment_attempt = view.findViewById(R.id.assignment_attempt);
        TextView assignment_submission = view.findViewById(R.id.assignment_submission_status);
        TextView assignment_grade = view.findViewById(R.id.assignment_grade_status);
        TextView assignment_due = view.findViewById(R.id.assignment_due_date);
        TextView assignment_time = view.findViewById(R.id.assignment_time_remain);
        TextView assignment_modified = view.findViewById(R.id.assignment_modified);
        TextView feedback_grade = view.findViewById(R.id.feedback_grade);
        TextView feedback_grade_on = view.findViewById(R.id.feedback_graded_on);
        TextView feedback_grade_by = view.findViewById(R.id.feedback_graded_by);
        TextView feedback_comments = view.findViewById(R.id.feedback_comments);
        ConstraintLayout feedback_layout = view.findViewById(R.id.feedback_layout);

        int i=1;
        assignment_title.setText(assignment.table.get(0).get(0));
        assignment_content.setText(Html.fromHtml(assignment.table.get(0).get(1)));
        if(assignment.table.get(i).get(1).length()==1){
            assignment_team.setText(assignment.table.get(i++).get(1));
            assignment_team.setVisibility(View.VISIBLE);
            view.findViewById(R.id.index_team).setVisibility(View.VISIBLE);
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
                view.findViewById(R.id.index_comment).setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
}
