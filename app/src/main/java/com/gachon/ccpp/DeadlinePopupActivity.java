package com.gachon.ccpp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeadlinePopupActivity extends Activity {
    TextView empty;
    GridView grid;

    ///
    /// * Format
    /// {
    ///     "icon": int // R.drawable.ic_announcement
    ///     "color": int // #FFFF0000
    ///     "title": String // Final Presentation
    ///     "context": String // YYYY-MM-DD · Mobile Programming
    ///     "link": String // "\"Mobile Programming(DDDDD_DDD)/"12Week (DD-MM - DD-MM)/Final Presentation"\""
    /// }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_deadline_popup);

        Button btn = findViewById(R.id.close_btn);
        btn.setOnClickListener(view -> {
            finish();
        });

        TextView title = findViewById(R.id.title_bar);

        empty = findViewById(R.id.empty_content);
        grid = findViewById(R.id.content);

        Object parser = null;
        ArrayList<String> list = new ArrayList<>();

        if (list.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            grid.setVisibility(View.INVISIBLE);
        } else {
            grid.setAdapter(new scheduleAdapter(list));
            empty.setVisibility(View.INVISIBLE);
            grid.setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");

        Matcher result = Pattern.compile("(\\d+)-(\\d+)-(\\d+)").matcher(date);

        if (result.matches()) {
            date = result.group(1) + "-"
                    + (Integer.parseInt(result.group(2)) + 1) + "-"
                    + result.group(3);
            title.setText(date);
        }
        title.setText(date);
    }

    public class scheduleAdapter extends BaseAdapter {
        private final ArrayList<String> list;

        public scheduleAdapter(ArrayList<String> value) {
            list = value;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            Context ctx = parent.getContext();

            try {
                final JSONObject json = new JSONObject((String) getItem(position));

                if (convertView == null) {
                    LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inf.inflate(R.layout.vertical_item, parent, false);
                } else {
                    View newView = new View(ctx);
                    newView = (View) convertView;
                }

                if (json == null)
                    return convertView;

                ImageView icon = convertView.findViewById(R.id.vert_item_icon);
                TextView tv = convertView.findViewById(R.id.vert_item_title);
                TextView context = convertView.findViewById(R.id.vert_item_ctx);

                icon.setBackgroundResource(R.drawable.style_oval);
                icon.setClipToOutline(true);

                if (json.has("icon"))
                    icon.setImageResource(json.getInt("icon"));
                else
                    icon.setImageResource(R.drawable.ic_bell);

                if (json.has("color"))
                    icon.setImageTintList(ColorStateList.valueOf(json.getInt("color")));
                else
                    icon.setImageTintList(ColorStateList.valueOf(Color.BLACK));

                if (json.has("title"))
                    tv.setText(json.getString("title"));
                else
                    tv.setText("N/A");

                if (json.has("context"))
                    context.setText(json.getString("context"));
                else
                    context.setText("N/A");

                convertView.setOnClickListener(view -> {
                    try {
                        linkTo(json.getString("link"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    public void linkTo(String path){
        Log.d("CCPP", "Deadline popup activity: " + path);
        finish();
    }
}