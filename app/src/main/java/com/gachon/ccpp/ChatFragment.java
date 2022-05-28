package com.gachon.ccpp;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        ArrayList<ItemFormat> list = new ArrayList<ItemFormat>();

        // requestChat(list)

        list.add(new ItemFormat("Test chat 1", "Test text",
                null, "test_chat1"));
        list.add(new ItemFormat("Test chat 2", "Test text2",
                "https://cyber.gachon.ac.kr/pluginfile.php/306392/user/icon/coursemosv2/f1?rev=4221197",
                "test_chat2"));

        GridView grid = view.findViewById(R.id.alarm_elem_list);
        grid.setAdapter(new ItemAdapter(list));

        return view;
    }

    public static class ItemFormat {
        public final String title;
        public final String context;
        public final String img_url;
        public final String link;

        public ItemFormat(String title, String context, String img_url, String link) {
            this.title = title;
            this.context = context;
            this.img_url = img_url;
            this.link = link;
        }
    }

    private class ItemAdapter extends BaseAdapter {
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
                view = inf.inflate(R.layout.alarm_item, parent, false);
            } else {
                View newView = new View(ctx);
                newView = (View) view;
            }

            ImageView img = view.findViewById(R.id.elem_alarm_img);
            TextView tv = view.findViewById(R.id.elem_alarm_title);
            TextView context = view.findViewById(R.id.elem_alarm_ctx);

            img.setBackgroundResource(R.drawable.style_oval);

            if (item.img_url != null)
                Glide.with(view).load(item.img_url).into(img);
            else
                img.setImageResource(R.drawable.baseline_account_circle_24);

            img.setClipToOutline(true);

            tv.setText(item.title);
            context.setText(item.context);

            view.setOnClickListener(view_ -> {
                startChatActivity(item);
            });

            return view;
        }
    }

    public final void startChatActivity(ItemFormat item) {
        Intent it = new Intent(getActivity(), ChatActivity.class);
        it.putExtra("title", item.title);
        it.putExtra("link", item.link);
        startActivity(it);
    }
}