package com.jueun.termproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeeklyCourseAdapter {

    Context context;
    LayoutInflater inflater;
    String[] arrWeekWord;
    int[] arrWeekImage;

    public WeeklyCourseAdapter(Context context, String[] arrWeekWord, int[] arrWeekImage) {
        this.context = context;
        this.arrWeekImage = arrWeekImage;
        this.arrWeekWord = arrWeekWord;
    }

    public int getCount() {
        return arrWeekWord.length;
    }

    public Object getItem(int position) {
        return arrWeekWord[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {

        if(inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(view == null) {
            view = inflater.inflate(R.layout.weeklycourse_layout, null);
        }

        ImageView weeklyCourseImage = view.findViewById(R.id.weeklyCourseImage);
        TextView weeklyCourseWord = view.findViewById(R.id.weeklyCourseText);

        weeklyCourseImage.setImageResource(arrWeekImage[position]);
        weeklyCourseWord.setText(arrWeekWord[position]);

        return view;
    }
}
