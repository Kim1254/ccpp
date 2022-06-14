package com.gachon.ccpp.lecture;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gachon.ccpp.R;
import com.gachon.ccpp.listener.onBackPressedListener;
import com.gachon.ccpp.parser.ListForm;

import java.util.ArrayList;
import java.util.List;

public class LectureFragment extends Fragment implements onBackPressedListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);

        Bundle bundle = getArguments();
        ArrayList<ListForm> courseList = (ArrayList<ListForm>) bundle.getSerializable("courseList");

        GridView grid = view.findViewById(R.id.lecture_list);
        grid.setAdapter(new LectureAdapter(courseList));

        return view;
    }

    public void startLecture(ListForm lecture) {
        LectureContentFragment lectureContentFragment = new LectureContentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("lecture",lecture);
        lectureContentFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragLayout, lectureContentFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }

    private class LectureAdapter extends BaseAdapter {
        private final List<ListForm> list;

        public LectureAdapter(List<ListForm> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Context ctx = viewGroup.getContext();
            final ListForm form = (ListForm) getItem(i);

            if (view == null) {
                LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inf.inflate(R.layout.lecture_item, viewGroup, false);
            } else {
                View newView = new View(ctx);
                newView = (View) view;
            }

            TextView tv = view.findViewById(R.id.lec_name);
            TextView cid = view.findViewById(R.id.lec_id);

            if (!form.payload.contentEquals("")) {
                ImageView iv = view.findViewById(R.id.lec_img);
                Glide.with(view).load(form.payload).into(iv);
                iv.setClipToOutline(true);
            }

            tv.setText(form.title);
            cid.setText(form.writer);

            view.setOnClickListener(view_ -> {
                startLecture(form);
            });

            return view;
        }
    }
}