package com.gachon.ccpp.parser;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gachon.ccpp.MainActivity;
import com.gachon.ccpp.R;
import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentCollector {
    RetrofitClient retrofitClient;
    RetrofitAPI api;

    public enum collectionState { FAILURE, BEGIN, LECTURE_LIST, LECTURE_ACTIVITY };

    public JSONObject head = null;
    private collectionState state_head = collectionState.FAILURE;

    private final Context ctx;

    public ContentCollector(Context ctx) {
        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        this.ctx = ctx;
        refresh();
    }

    public void refresh() {
        new collectionTask().start();
    }

    public collectionState getProgress() {
        return state_head;
    }

    public JSONObject getObject(String path) {
        String[] args = path.split("\\/*");

        JSONObject prev = head;
        for (String arg : args) {
            try {
                prev = new JSONObject(prev.getString(arg));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return prev;
    }

    private void saveData() {
        try {
            FileOutputStream fos = ctx.openFileOutput("_data",
                    Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeUTF(state_head.toString());
            dos.write(head.toString().getBytes());

            dos.flush();
            dos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean loadData() {
        try {
            FileInputStream fis = ctx.openFileInput("_data");
            DataInputStream dis = new DataInputStream(fis);

            state_head = collectionState.valueOf(dis.readUTF());
            head = new JSONObject(dis.readUTF());

            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private class collectionTask extends Thread {
        private final JSONObject newHead = new JSONObject();

        collectionState state_current;

        private int num_request;

        public collectionTask() {
            state_current = collectionState.BEGIN;
        }

        public synchronized void updateHead() {
            if (state_current.compareTo(state_head) < 0)
                return;

            head = newHead;
            state_head = state_current;
            saveData();
        }

        public void run() {
            // requestLecture
            api.getUri("").enqueue(new Callback<ResponseBody>() {
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        state_current = collectionState.FAILURE;
                    } else {
                        try {
                            Document html = Jsoup.parse(response.body().string());
                            HtmlParser parser = new HtmlParser(html);

                            ArrayList<ListForm> list = parser.getCourseList();
                            for (ListForm e : list) {
                                JSONObject json = new JSONObject();
                                json.put("prof", e.writer);
                                json.put("image", e.payload);
                                json.put("link", e.link);
                                newHead.put(e.title, json.toString());
                            }

                            state_current = collectionState.LECTURE_LIST;
                            updateHead();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            state_current = collectionState.FAILURE;
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    state_current = collectionState.FAILURE;
                }
            });

            while (state_current != collectionState.FAILURE) {
                try { synchronized (this) { wait(100); } } catch (Exception ignored) {}
                if (state_current == collectionState.LECTURE_LIST)
                    break;
            }

            try {
                requestActivity();
            } catch (JSONException ignored) {}

            while (state_current != collectionState.FAILURE) {
                try { synchronized (this) { wait(100); } } catch (Exception ignored) {}
                if (num_request == 0)
                    break;
            }

            state_current = collectionState.LECTURE_ACTIVITY;
            updateHead();
        }

        private void requestActivity() throws JSONException {
            num_request = 0;

            for (Iterator<String> it = newHead.keys(); it.hasNext(); ) {
                String key = it.next();
                String link = new JSONObject(newHead.getString(key)).getString("link");

                num_request++;
                api.getUri(link).enqueue(new Callback<ResponseBody>() {
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                Document html = Jsoup.parse(response.body().string());

                                HtmlParser parser = new HtmlParser(html);
                                String link = parser.getClassAnnouncementLink();
                                requestAnnouncement(key, link);

                                Element article = html.select(".section.main.clearfix.current").first();

                                JSONObject data = new JSONObject();

                                String title = article.select(".sectionname").text();
                                data.put("current", title);

                                Elements weeks = html
                                        .select(".total_sections").first()
                                        .select(".section.main.clearfix");

                                for (Element e : weeks) {
                                    title = e.attr("aria-label");
                                    data.put(title, parseContent(e));
                                }

                                synchronized(newHead) {
                                    JSONObject lecture = new JSONObject(newHead.getString(key));
                                    lecture.put("activity", data.toString());
                                    newHead.put(key, lecture.toString());
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                state_current = collectionState.FAILURE;
                            } finally { num_request--; }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        state_current = collectionState.FAILURE;
                        num_request--;
                    }
                });
            }
        }

        private String parseContent(Element parent) throws JSONException {
            JSONObject head = new JSONObject();

            Elements section = parent.select(".section.img-text");

            if (section.first() == null)
                return head.toString();

            int i = 0;
            Elements contents = section.first().select(".activity");
            for (Element content : contents) {
                if (content.select(".activityinstance a").first() == null) { // label
                    Elements elem = contents.select("p");
                    if (elem.first() == null)
                        continue;

                    String name = elem.first().text();

                    if (name.trim().length() == 0) // blank
                        continue;

                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("class", content.attr("class").substring(9));
                    head.put("item" + ++i, json.toString());
                } else {
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
            }
            return head.toString();
        }

        private void requestAnnouncement(String key, String link) {
            num_request++;

            MainActivity.api.getUri(link + "&ls=100").enqueue(new Callback<ResponseBody>() {
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject();
                            HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                            ArrayList<ListForm> announcementLink = parser.getCourseAnnouncementList();

                            for (ListForm l : announcementLink) {
                                JSONObject child = new JSONObject();
                                child.put("link", l.link);
                                child.put("index", l.payload);

                                json.put(l.title, child);
                            }

                            synchronized (newHead) {
                                JSONObject lecture = new JSONObject(newHead.getString(key));
                                lecture.put("announcement", json.toString());
                                newHead.put(key, lecture.toString());
                            }
                        } catch (Exception ignored) {
                            state_current = collectionState.FAILURE;
                        } finally { num_request--; }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    state_current = collectionState.FAILURE;
                    num_request--;
                }
            });
        }
    }
}
