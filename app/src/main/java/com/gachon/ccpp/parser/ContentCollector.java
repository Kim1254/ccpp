package com.gachon.ccpp.parser;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;
import com.gachon.ccpp.util.AES256;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentCollector {
    private static final RetrofitClient retrofitClient = RetrofitClient.getInstance();
    private static final RetrofitAPI api = RetrofitClient.getRetrofitInterface();

    private static JSONObject head = null;

    public static void beginThread(collectionListener listener) {
        new collectionTask(listener).start();
    }

    public static JSONObject getObject(String path) {
        String[] args = path.split("\\\\");

        JSONObject prev = head;
        for (String arg : args) {
            try { prev = new JSONObject(prev.getString(arg)); }
            catch (Exception e) { return null; }
        }

        return prev;
    }

    public static void saveData(Context ctx) {
        try {
            FileOutputStream fos = ctx.openFileOutput("_data",
                    Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);

            String cipher = head.toString();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                cipher = new AES256().encrypt(cipher);

            dos.write(cipher.getBytes());

            dos.flush();
            dos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static boolean loadData(Context ctx) {
        try {
            FileInputStream fis = ctx.openFileInput("_data");
            DataInputStream dis = new DataInputStream(fis);

            String plain = dis.readUTF();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                plain = new AES256().decrypt(plain);

            head = new JSONObject(plain);

            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public abstract static class collectionListener {
        public void onCompleteLectureList(boolean success) {};
        public void onCompleteActivity(boolean success) {};
        public void onCompleteChatList(boolean success) {};
    }

    private static class collectionTask extends Thread {
        private final JSONObject newHead = new JSONObject();
        private final collectionListener listener;

        public collectionTask(collectionListener listener) {
            this.listener = listener;
        }

        public synchronized void updateHead() {
            head = newHead;
        }

        public void run() {
            new Thread(this::requestChatList).start();

            requestLecture();
            requestActivity();
            updateHead();
        }

        private void requestLecture() {
            final boolean[] breaker = {false};

            api.getUri("").enqueue(new Callback<ResponseBody>() {
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            Document html = Jsoup.parse(response.body().string());
                            HtmlParser parser = new HtmlParser(html);

                            JSONObject lecture = new JSONObject();

                            ArrayList<ListForm> list = parser.getCourseList();
                            for (ListForm e : list) {
                                JSONObject json = new JSONObject();
                                json.put("prof", e.writer);
                                json.put("image", e.payload);
                                json.put("link", e.link);
                                lecture.put(e.title, json.toString());
                            }

                            synchronized (newHead) {
                                newHead.put("lecture", lecture.toString());
                            }

                            updateHead();
                            if (listener != null)
                                listener.onCompleteLectureList(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (listener != null)
                                listener.onCompleteLectureList(false);
                        } finally {
                            breaker[0] = true;
                        }
                    } else {
                        breaker[0] = true;
                        if (listener != null)
                            listener.onCompleteLectureList(true);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    breaker[0] = true;
                    if (listener != null)
                        listener.onCompleteLectureList(false);
                }
            });

            // wait for response
            while (!breaker[0]) {
                try { synchronized (this) { wait(100); } } catch (Exception ignored) {}
            }
        }

        private void requestActivity() {
            final int[] counter = {0};

            JSONObject lecture = null;

            try {
                lecture = new JSONObject(newHead.getString("lecture"));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            for (Iterator<String> it = lecture.keys(); it.hasNext(); ) {
                String key = it.next();
                String link = null;
                try {
                    link = new JSONObject(lecture.getString(key)).getString("link");
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }

                counter[0]++;
                final JSONObject finalLecture = lecture;
                api.getUri(link).enqueue(new Callback<ResponseBody>() {
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            counter[0]--;
                            return;
                        }

                        try {
                            Document html = Jsoup.parse(response.body().string());

                            HtmlParser parser = new HtmlParser(html);
                            String link = parser.getClassAnnouncementLink();
                            requestAnnouncement(key, link);

                            Element article = html.select(".section.main.clearfix.current").first();

                            JSONObject data = new JSONObject();

                            String title = article.select(".sectionname").text();
                            data.put("current", title);

                            Elements weeks = html.select(
                                    ".total_sections .section.main.clearfix");

                            for (Element e : weeks) {
                                title = e.attr("aria-label");
                                data.put(title, parseContent(e));
                            }

                            synchronized(newHead) {
                                finalLecture.put("activity", data.toString());
                                newHead.put("lecture", finalLecture.toString());
                            }
                            updateHead();
                            if (listener != null)
                                listener.onCompleteActivity(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (listener != null)
                                listener.onCompleteActivity(false);
                        } finally {
                            counter[0]--;
                            if (listener != null)
                                listener.onCompleteActivity(false);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        counter[0]--;
                        if (listener != null)
                            listener.onCompleteActivity(false);
                    }
                });
            }

            // wait for response
            while (counter[0] != 0) {
                try { synchronized (this) { wait(100); } } catch (Exception ignored) {}
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
            final int[] counter = {0};
            counter[0]++;

            api.getUri(link + "&ls=100").enqueue(new Callback<ResponseBody>() {
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
                                JSONObject lecture = new JSONObject(newHead.getString("lecture"));
                                JSONObject lec_elem = new JSONObject(lecture.getString(key));
                                lec_elem.put("announcement", json.toString());
                                lecture.put(key, lec_elem.toString());
                                newHead.put("lecture", lecture.toString());
                            }
                        } catch (Exception ignored) {
                        } finally { counter[0]--; }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    counter[0]--;
                }
            });
        }

        private void requestChatList() {
            api.getUri("/local/ubmessage").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject chat;
                            if (newHead.has("chat"))
                                chat = new JSONObject(newHead.getString("chat"));
                            else
                                chat = new JSONObject();

                            Document html = Jsoup.parse(response.body().string());

                            Elements list = html.select("li.media");
                            for (Element e : list) {
                                JSONObject json = new JSONObject();
                                json.put("image",
                                        e.select(".media-left img").attr("src"));
                                json.put("title",
                                        e.select(".media-heading").text());
                                json.put("time",
                                        e.select(".time").text());
                                json.put("hint",
                                        e.select(".msg").text());
                                json.put("delete",
                                        e.select("div.tools a").attr("href"));

                                String link = e.select(".media-body a").attr("href");
                                if (link.contains("gmessage.php"))
                                    json.put("type", "group");
                                else
                                    json.put("type", "individual");
                                chat.put(link.split("&id=")[1], json.toString());
                            }
                            synchronized (newHead) {
                                newHead.put("chat", chat);
                            }
                            updateHead();

                            if (listener != null)
                                listener.onCompleteChatList(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (listener != null)
                                listener.onCompleteChatList(false);
                        }
                    } else {
                        if (listener != null)
                            listener.onCompleteChatList(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    if (listener != null)
                        listener.onCompleteChatList(false);
                }
            });

        }
    }
}
