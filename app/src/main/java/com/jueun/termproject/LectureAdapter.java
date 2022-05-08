package com.jueun.termproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LectureAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String[] arrLectureWord;
    int[] arrLectureImage;

    public LectureAdapter(Context context, String[] arrLectureWord, int[] arrLectureImage) {
        this.context = context;
        this.arrLectureImage = arrLectureImage;
        this.arrLectureWord = arrLectureWord;
    }

    @Override
    public int getCount() {
        return arrLectureWord.length;
    }

    @Override
    public Object getItem(int position) {
        return arrLectureWord[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if(inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(view == null) {
            view = inflater.inflate(R.layout.lecture_layout, null);
        }

        ImageView lectureImage = view.findViewById(R.id.lectureImage);
        TextView lectureWord = view.findViewById(R.id.lectureText);

        lectureImage.setImageResource(arrLectureImage[position]);
        lectureWord.setText(arrLectureWord[position]);

        return view;
    }
}
