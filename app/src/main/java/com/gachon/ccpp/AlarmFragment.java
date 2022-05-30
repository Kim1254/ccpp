package com.gachon.ccpp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AlarmFragment extends Fragment {
    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        ArrayList<ItemFormat> list = new ArrayList<ItemFormat>();

        // Write alarm parsing here
        // requestAlarm(list) ... parseAlarm(data, list)

        list.add(new ItemFormat("Test alarm 1", "This is announcement icon",
                R.drawable.ic_announcement, R.color.gcc_liteblue,
                "test_alarm1"));
        list.add(new ItemFormat("Test alarm 2", "This is assignment icon",
                R.drawable.ic_assignment, R.color.gcc_orange,
                "test_alarm2"));

        GridView grid = view.findViewById(R.id.alarm_elem_list);
        grid.setAdapter(new ItemAdapter(list));

        return view;
    }

    public static class ItemFormat {
        public final String title;
        public final String context;
        public final int icon;
        public final int color;
        public final String link;

        public ItemFormat(String title, String context, int icon, int color, String link) {
            this.title = title;
            this.context = context;
            this.icon = icon;
            this.color = color;
            this.link = link;
        }
    }

    private static class ItemAdapter extends BaseAdapter {
        private final List<ItemFormat> list;

        public ItemAdapter(List<ItemFormat> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            Context ctx = parent.getContext();
            final ItemFormat item = (ItemFormat) getItem(i);

            if (view == null) {
                LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inf.inflate(R.layout.vertical_item, parent, false);
            } else {
                View newView = new View(ctx);
                newView = (View) view;
            }

            ImageView img = view.findViewById(R.id.vert_item_icon);
            TextView tv = view.findViewById(R.id.vert_item_title);
            TextView context = view.findViewById(R.id.vert_item_ctx);

            if (item.icon != 0)
                img.setImageResource(item.icon);
            if (item.color != 0)
                img.setImageTintList(ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                                view.getResources(), item.color, null)));

            tv.setText(item.title);
            context.setText(item.context);

            view.setOnClickListener(view_ -> {
                onClick(item);
            });

            return view;
        }

        public void onClick(ItemFormat item) {
            Log.d("CCPP", "Alarm: " + item.link);
        }
    }
}