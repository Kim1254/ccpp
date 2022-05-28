package com.gachon.ccpp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gachon.ccpp.parser.ListForm;

import java.util.ArrayList;
import java.util.List;

public class LectureFragment extends Fragment {
    View thisView;

    ArrayList<ListForm> courseList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_lecture, container, false);

        Bundle bundle = getArguments();
        courseList = (ArrayList<ListForm>) bundle.getSerializable("courseList");

        GridView grid = thisView.findViewById(R.id.lecture_list);
        grid.setAdapter(new LectureAdapter(courseList));

        return thisView;
    }

    public void startLecture(String title, String url) {
        Intent it = new Intent(getActivity(), LectureActivity.class);
        it.putExtra("title", title);
        it.putExtra("link", url);
        startActivity(it);
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

            if (form.image != null) {
                ImageView iv = view.findViewById(R.id.lec_img);
                Glide.with(view).load(form.image).into(iv);
                iv.setClipToOutline(true);
            }

            tv.setText(form.title);
            cid.setText(form.writer);

            view.setOnClickListener(view_ -> {
                startLecture(form.title, form.link);
            });

            return view;
        }
    }
}