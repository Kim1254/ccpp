package com.gachon.ccpp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gachon.ccpp.parser.ContentCollector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_alarm, container, false);

        while (ContentCollector.getObject("chat") == null) {}
        JSONObject json = ContentCollector.getObject("chat");

        GridView grid = thisView.findViewById(R.id.alarm_elem_list);
        grid.setAdapter(new ItemAdapter(json));

        return thisView;
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

        public ItemAdapter(JSONObject json) {
            list = Collections.synchronizedList(new ArrayList<ItemFormat>());

            for (Iterator<String> it = json.keys(); it.hasNext();) {
                String link = it.next();
                String title = "Untitled";
                String content = "";
                String img = null;

                try {
                    JSONObject child = new JSONObject(json.getString(link));

                    if (child.has("title"))
                        title = child.getString("title");
                    if (child.has("hint"))
                        content = child.getString("hint");
                    if (child.has("image"))
                        img = child.getString("image");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                list.add(new ItemFormat(title, content, img, link));
            }
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
        public long getItemId(int i) {
            return i;
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

            img.setBackgroundResource(R.drawable.style_oval);

            try {
                if (item.img_url != null) {
                    Glide.with(view).load(item.img_url).into(img);
                    img.setPadding(0, 0, 0, 0);
                } else {
                    img.setImageResource(R.drawable.baseline_account_circle_24);

                    DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
                    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
                    img.setPadding(px, px, px, px);
                }

                img.setClipToOutline(true);

                tv.setText(item.title);
                context.setText(item.context);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        it.putExtra("image", item.img_url);

        startActivity(it);
    }
}