package com.gachon.ccpp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LectureFragment extends Fragment {
    public final List<Lecture> lecture_list = Collections.synchronizedList(new ArrayList<Lecture>());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);

        GridView grid = view.findViewById(R.id.lecture_list);
        grid.setAdapter(new LectureAdapter(lecture_list));

        return view;
    }

    public static class Lecture {
        public final String name;
        public final String id;
        public final String prof;
        public final String link;
        public String img_url = null;

        public Lecture(String name, String id, String prof, String link) {
            this.name = name;
            this.id = id;
            this.prof = prof;
            this.link = link;
        }

        public Lecture(String name, String id, String prof, String link, String img_url) {
            this.name = name;
            this.id = id;
            this.prof = prof;
            this.link = link;
            this.img_url = img_url;
        }
    }

    public void startLecture(String title, String url) {
        Intent it = new Intent(getActivity(), LectureActivity.class);
        it.putExtra("title", title);
        it.putExtra("link", url);
        startActivity(it);
    }

    private class LectureAdapter extends BaseAdapter {
        private final List<Lecture> list;

        public LectureAdapter(List<Lecture> list) {
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
            final Lecture lec = (Lecture) getItem(i);

            if (view == null) {
                LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inf.inflate(R.layout.lecture_item, viewGroup, false);
            } else {
                View newView = new View(ctx);
                newView = (View) view;
            }

            TextView tv = view.findViewById(R.id.lec_name);
            TextView cid = view.findViewById(R.id.lec_id);

            if (lec.img_url != null) {
                ImageView iv = view.findViewById(R.id.lec_img);
                Glide.with(view).load(lec.img_url).into(iv);
                iv.setClipToOutline(true);
            }

            tv.setText(lec.name);
            cid.setText(lec.id);

            view.setOnClickListener(view_ -> {
                startLecture(lec.name, lec.link);
            });

            return view;
        }
    }
}