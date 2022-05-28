package com.gachon.ccpp;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LectureActivity extends AppCompatActivity {
    private RetrofitClient retrofitClient;
    private RetrofitAPI api;

    private GridView notify_list;
    private GridView current_list;
    private GridView weekly_list;

    private JSONObject data = null;

    private static final String baseUrl = "https://cyber.gachon.ac.kr/course/view.php";

    private final String[] week_patterns = {
            "(\\d+)Week \\[(\\d+) (\\p{Alpha}+) - (\\d+) (\\p{Alpha}+)\\]",
            "(\\d+)주차 \\[(\\d+)월(\\d+)일 - (\\d+)월(\\d+)일\\]"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        notify_list = findViewById(R.id.element_list_notify);
        current_list = findViewById(R.id.element_list_current);
        weekly_list = findViewById(R.id.element_list_weekly);

        Intent it = getIntent();
        if (it != null) {
            if (it.getStringExtra("title") != null)
                getSupportActionBar().setTitle(it.getStringExtra("title"));

            if (it.getStringExtra("link") != null)
                requestCourse(it.getStringExtra("link"));
        }
        else
            finish();
    }

    private String parseContent(Element parent) throws JSONException {
        JSONObject head = new JSONObject();

        Elements section = parent.select(".section.img-text");

        if (section.first() == null)
            return head.toString();

        int i = 0;
        Elements contents = section.first().select(".activity");
        for (Element content : contents) {
            String link = content.select(".activityinstance a").first().attr("href");

            Element instance_name = content.select(".instancename").first();

            if (instance_name == null)
                continue;

            String name = instance_name.text();
            if (instance_name.select(".accesshide").first() != null) {
                String hide = instance_name.select(".accesshide").first().text();
                name = name.substring(0, name.length() - hide.length());
            }

            JSONObject elem = new JSONObject();
            elem.put("name", name);
            elem.put("class", content.attr("class").substring(9));
            elem.put("link", link);
            head.put("item" + ++i, elem.toString());
        }
        return head.toString();
    }

    private void requestCourse(String url) {
        Call<ResponseBody> connect = api.course(url.substring(baseUrl.length() + 4));
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        Element article = html.select(".section.main.clearfix.current").first();

                        data = new JSONObject();

                        String title = article.select(".sectionname").text();
                        data.put("current",
                                new JSONObject().put(title, parseContent(article)).toString());

                        Elements weeks = html
                                .select(".total_sections").first()
                                .select(".section.main.clearfix");

                        for (Element e : weeks) {
                            title = e.attr("aria-label");
                            data.put(title, parseContent(e));
                        }

                        constructView();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        data = null;
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                finish();
            }
        });
    }

    public void constructView() throws JSONException {
        if (data == null)
            return;

        NotiElem assign = new NotiElem(getString(R.string.LectureActivity_assignment),
                "assignment",
                0,
                R.drawable.ic_announcement,
                Color.argb(255, 255, 94, 0));

        ArrayList<String> li = new ArrayList<String>();
        for (Iterator<String> it = data.keys(); it.hasNext();) {
            String t = it.next();

            if (t.contentEquals("current")) {
                JSONObject current = new JSONObject(data.getString("current"));
                if (current.keys().hasNext())
                    current_list.setAdapter(new WeeklyCourseAdapter(current.keys().next()));
                continue;
            }

            li.add(t);
            JSONObject json = new JSONObject(data.getString(t));
            for (Iterator<String> iter = json.keys(); iter.hasNext();) {
                JSONObject activity = new JSONObject(json.getString(iter.next()));
                if (activity.has("class")) {
                    String type = activity.getString("class").split(" ")[0];
                    if (type.contentEquals("assign"))
                        assign.num++;
                }
            }
        }

        if (li.size() != 0)
            weekly_list.setAdapter(new WeeklyCourseAdapter(li));

        ArrayList<NotiElem> n_li = new ArrayList<NotiElem>();

        if (assign.num != 0)
            n_li.add(assign);

        if (n_li.size() != 0)
            notify_list.setAdapter(new NotiAdapter(n_li));
    }

    private class WeeklyCourseAdapter extends BaseAdapter {
        private final List<String> list;

        public WeeklyCourseAdapter(List<String> list) {
            this.list = list;
        }

        public WeeklyCourseAdapter(String title) {
            this.list = new ArrayList<String>();
            list.add(title);
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
            final String title = (String) getItem(i);

            if (view == null) {
                LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inf.inflate(R.layout.lecture_elem_item, viewGroup, false);
            }

            TextView bigN = view.findViewById(R.id.big_n_week);
            TextView week = view.findViewById(R.id.ctx_n_week);
            TextView period = view.findViewById(R.id.ctx_n_per);
            TextView num_noti = view.findViewById(R.id.num_notify);

            Matcher mat = parseTitle(title);
            if (mat != null) {
                bigN.setText(mat.group(1));
                week.setText(getString(R.string.LectureActivity_elem_week, mat.group(1)));
                period.setText(getString(R.string.LectureActivity_elem_period,
                        mat.group(2), mat.group(3), mat.group(4), mat.group(5)));
            } else {
                bigN.setText("N");
                week.setText(getString(R.string.LectureActivity_elem_week, "NA"));
                period.setText(getString(R.string.LectureActivity_elem_period,
                        "NA", "NA", "NA", "NA"));
            }
            num_noti.setVisibility(View.INVISIBLE);

            try {
                JSONObject child = new JSONObject(data.getString(title));
                if (!child.keys().hasNext()) {
                    view.setBackgroundColor(Color.argb(255, 245, 245, 245));
                    if (view.hasOnClickListeners())
                        view.setOnClickListener(null);
                }
                else {
                    view.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    view.setOnClickListener(view_ -> {
                        Log.d("CCPP", "Clicked: " + child);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

    private Matcher parseTitle(String t) {
        for (String p : week_patterns) {
            Matcher mat = Pattern.compile(p).matcher(t);
            if (mat.matches())
                return mat;
        }

        return null;
    }

    public static class NotiElem {
        public final String name;
        public final String link;
        public int num;
        public final int icon;
        public final int color;

        public NotiElem(String name, String link, int num, int icon, int color) {
            this.name = name;
            this.link = link;
            this.num = num;
            this.icon = icon;
            this.color = color;
        }
    }

    private class NotiAdapter extends BaseAdapter {
        private final List<NotiElem> list;

        public NotiAdapter(List<NotiElem> list) {
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
            final NotiElem elem = (NotiElem) getItem(i);

            if (view == null) {
                LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inf.inflate(R.layout.lecture_elem_noti, viewGroup, false);
            }

            TextView bigN = view.findViewById(R.id.big_n_week);
            TextView week = view.findViewById(R.id.ctx_n_week);
            TextView num_noti = view.findViewById(R.id.num_notify);
;
            num_noti.setVisibility(View.VISIBLE);

            bigN.setText("");
            if (elem.icon != 0)
                bigN.setBackgroundResource(elem.icon);
            if (elem.color != 0) {
                bigN.setBackgroundTintList(ColorStateList.valueOf(elem.color));

                int r = 255 - Color.red(elem.color);
                int g = 255 - Color.green(elem.color);
                int b = 255 - Color.blue(elem.color);
                num_noti.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255, r, g, b)));
                num_noti.setClipToOutline(true);
            }

            week.setText(elem.name);
            num_noti.setText("" + elem.num);

            view.setOnClickListener(view_ -> {
                NotiShortCut(elem.link);
            });
            return view;
        }
    }

    public void NotiShortCut(String link) {

    }
}
