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

    public enum collectionState { FAILURE, BEGIN, LECTURE_LIST, LECTURE_ACTIVITY, LECTURE_CONTEXT };

    private JSONObject head = null;
    private JSONObject context = null;

    private final Context ctx;

    collectionState state_head = collectionState.FAILURE;

    public ContentCollector(Context ctx) {
        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        this.ctx = ctx;

        if (!loadData()) {
            Log.d("CCPP", "launch failed: refresh");
            refresh();
        }
        else {
            Log.d("CCPP", "load succeed: " + head.toString().length() + ", " + context.toString().length());
        }
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

    public String getContext(String path) {
        try {
            return context.getString(path);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean saveData() {
        try {
            FileOutputStream fos = ctx.openFileOutput("_data",
                    Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeUTF(state_head.toString());
            dos.writeUTF(head.toString());

            if (state_head.equals(collectionState.LECTURE_CONTEXT))
                dos.writeUTF(context.toString());

            dos.flush();
            dos.close();

            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean loadData() {
        try {
            FileInputStream fis = ctx.openFileInput("_data");
            DataInputStream dis = new DataInputStream(fis);

            state_head = collectionState.valueOf(dis.readUTF());
            head = new JSONObject(dis.readUTF());

            if (state_head.equals(collectionState.LECTURE_CONTEXT))
                context = new JSONObject(dis.readUTF());

            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private class collectionTask extends Thread {
        private final JSONObject newHead = new JSONObject();
        private final JSONObject newContext = new JSONObject();

        collectionState state_current;

        private int num_request;

        public collectionTask() {
            state_current = collectionState.BEGIN;
        }

        public void updateHead() {
            if (state_head.compareTo(state_current) < 0) {
                head = newHead;
                state_head = state_current;
                saveData();
            }
        }

        public void updateContext() {
            if (state_head.compareTo(state_current) < 0) {
                context = newContext;
                state_head = state_current;
                saveData();
            }
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
                if (state_current == collectionState.LECTURE_LIST)
                    break;
            }

            try {
                requestActivity();
            } catch (JSONException ignored) {}

            while (state_current != collectionState.FAILURE) {
                if (num_request == 0)
                    break;
            }

            state_current = collectionState.LECTURE_ACTIVITY;
            updateHead();

            try {
                requestContext();
            } catch (JSONException e) { e.printStackTrace(); }

            while (state_current != collectionState.FAILURE) {
                if (num_request == 0)
                    break;
            }

            state_current = collectionState.LECTURE_CONTEXT;
            updateContext();
        }

        private void requestActivity() throws JSONException {
            num_request = 0;

            for (Iterator<String> it = newHead.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONObject lecture = new JSONObject(newHead.getString(key));

                num_request++;
                api.getUri(lecture.getString("link"))
                        .enqueue(new Callback<ResponseBody>() {
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

                                lecture.put("activity", data.toString());
                                synchronized(newHead) {
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

        private void requestAnnouncement(String key, String link) throws JSONException {
            num_request++;

            JSONObject lecture = new JSONObject(newHead.getString(key));
            JSONObject json = new JSONObject();

            MainActivity.api.getUri(link + "&ls=100").enqueue(new Callback<ResponseBody>() {
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                            ArrayList<ListForm> announcementLink = parser.getCourseAnnouncementList();

                            for (ListForm l : announcementLink) {
                                JSONObject child = new JSONObject();
                                child.put("link", l.link);
                                child.put("index", l.payload);

                                json.put(l.title, child);
                            }

                            lecture.put("announcement", json.toString());
                            synchronized (newHead) {
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

        private void requestContext() throws JSONException {
            num_request = 0;
            Log.d("CCPP", "head length: " + newHead.toString().length());
            for (Iterator<String> it = newHead.keys(); it.hasNext(); ) {
                String key = it.next();
                Log.d("CCPP", "key1: " + key);

                JSONObject lec = new JSONObject(newHead.getString(key));
                JSONObject activity = new JSONObject(lec.getString("activity"));
                JSONObject announcement = new JSONObject(lec.getString("announcement"));            Log.d("CCPP", "head length: " + newHead.toString().length());

                Log.d("CCPP", "act length: " + activity.toString().length() + ", "
                        + "ann length: " + announcement.toString().length());

                for (Iterator<String> iter = activity.keys(); iter.hasNext(); ) {
                    String child_key = iter.next();
                    Log.d("CCPP", "key2: " + child_key);
                    if (child_key.contentEquals("current"))
                        continue;

                    JSONObject child = new JSONObject(activity.getString(child_key));

                    for (Iterator<String> itera = child.keys(); itera.hasNext(); ) {
                        String child_key2 = itera.next();
                        JSONObject child2 = new JSONObject(child.getString(child_key2));

                        if (!child2.has("link"))
                            continue;

                        requestAssContext(key + "\\/*activity\\/*" + child_key
                                        + "\\/*" + child_key2,
                                child2.getString("link"));
                    }
                }

                for (Iterator<String> iter = announcement.keys(); iter.hasNext(); ) {
                    String child_key = iter.next();
                    JSONObject child = new JSONObject(announcement.getString(child_key));

                    for (Iterator<String> itera = child.keys(); itera.hasNext(); ) {
                        String child_key2 = iter.next();
                        JSONObject child2 = new JSONObject(child.getString(child_key));

                        if (!child2.has("link"))
                            continue;

                        requestAnnContext(key + "\\/*announcement\\/*" + child_key
                                + "\\/*" + child_key2,
                                child2.getString("link"));
                    }
                }
            }

            Log.d("CCPP", "request ends.");
        }

        private void requestAnnContext(String path, String link) {
            Log.d("CCPP", "Requested: " + path);
            num_request++;
            api.getUri(link).enqueue(new Callback<ResponseBody>() {
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                            ContentForm data = parser.getAnnouncementContent();
                            JSONObject json = new JSONObject();
                            // json.put("title", data.title); // already has same values on head
                            json.put("date", data.date);
                            json.put("writer", data.writer);
                            json.put("content", data.content);
                            json.put("hit", data.payload);

                            synchronized (newContext) {
                                newContext.put(path, json);
                            }
                        } catch (Exception ignored) {
                        } finally { num_request--; }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    num_request--;
                }
            });
        }

        private void requestAssContext(String path, String link) {
            Log.d("CCPP", "Requested: " + path);
            num_request++;
            api.getUri(link).enqueue(new Callback<ResponseBody>() {
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                            ContentForm data = parser.getAssignmentContent();
                            JSONObject json = new JSONObject();

                            json.put("date", data.date);
                            if (!data.content.contentEquals("")) {
                                json.put("content", data.content);
                                json.put("link", data.payload);
                            }

                            synchronized (newContext) {
                                newContext.put(path, json);
                            }
                        } catch (Exception ignored) {
                        } finally { num_request--; }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    num_request--;
                }
            });
        }
    }
}
