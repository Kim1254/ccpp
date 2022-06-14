package com.gachon.ccpp.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gachon.ccpp.dialog.LoginDialog;
import com.gachon.ccpp.MainActivity;
import com.gachon.ccpp.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private LoginDialog privateDialog;
    private JSONObject info_send = null;

    private Button sendBtn;
    private TextView contentText;
    private GridView grid;

    private String title = null;
    private String link = null;
    private String img = null;
    private String type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        privateDialog = new LoginDialog(this);

        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            link = intent.getStringExtra("link");
            img = intent.getStringExtra("image");
            type = intent.getStringExtra("type");
            getSupportActionBar().setTitle(title);
        }

        sendBtn = findViewById(R.id.btn_send);
        contentText = findViewById(R.id.edit_text);
        grid = findViewById(R.id.grid);
        request();

        sendBtn.setEnabled(false);
        contentText.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        privateDialog.dismiss();
    }

    private void request() {
        privateDialog.show("9844-loading-40-paperplane.json", getString(R.string.LoadingDialog_TextLoading));

        if (type.contentEquals("individual"))
            MainActivity.api.getUri(String.format("local/ubmessage/message.php?page=1&id=%s&total=1",
                    link)).enqueue(request_callback);
        else
            MainActivity.api.getUri(String.format("local/ubmessage/gmessage.php?mc=&keyfield=&keyword=&page=1&id=%s",
                    link)).enqueue(request_callback);
    }

    private final Callback<ResponseBody> request_callback = new Callback<ResponseBody>() {
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                try {
                    Document html = Jsoup.parse(response.body().string());

                    Elements message_list = html.select(".messages.well.clearfix");
                    ArrayList<JSONObject> json_list = new ArrayList<>();

                    for (Element e : message_list.get(0).children()) {
                        if (e.attr("class").contentEquals("heading clearfix"))
                            continue;

                        JSONObject json = new JSONObject();
                        json.put("class", e.attr("class").trim());
                        json.put("content", e.select(".content").text().replace("<br>", "\n"));
                        json.put("time", e.select(".time").attr("title"));
                        json_list.add(json);
                    }

                    Log.d("CCPP", "tesT: " + message_list.text());

                    grid.setAdapter(new ChatRoomAdapter(json_list));

                    if (type.contentEquals("group")) {
                        Elements send_field = html.select(".message_send_form.well fieldset");

                        if (info_send == null) {
                            info_send = new JSONObject();
                            info_send.put("type",
                                    send_field.get(0).child(0).attr("value"));
                            info_send.put("sesskey",
                                    send_field.get(0).child(1).attr("value"));
                            info_send.put("to",
                                    send_field.get(0).child(2).attr("value"));
                            info_send.put("returnurl",
                                    send_field.get(0).child(3).attr("value"));

                            sendBtn.setOnClickListener(view -> {
                                privateDialog.show("9844-loading-40-paperplane.json", getString(R.string.LoadingDialog_TextLoading));

                                String text = contentText.getText().toString();
                                contentText.setText("");

                                try {
                                    MainActivity.api.send_message(
                                            info_send.getString("type"),
                                            info_send.getString("sesskey"),
                                            info_send.getString("to"),
                                            info_send.getString("returnurl"),
                                            text).enqueue(send_callback);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                            sendBtn.setEnabled(true);
                            contentText.setEnabled(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    privateDialog.hide();
                }
            } else
                privateDialog.hide();
        }
        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            privateDialog.hide();
        }
    };

    private final Callback<ResponseBody> send_callback = new Callback<ResponseBody>() {
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if (response.isSuccessful())
                request();

            privateDialog.hide();
        }
        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            privateDialog.hide();
        }
    };

    private class ChatRoomAdapter extends BaseAdapter {
        private final List<JSONObject> list;

        private ChatRoomAdapter(List<JSONObject> list) {
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
        public View getView(int position, View view, ViewGroup parent) {
            Context ctx = parent.getContext();

            if (view == null) {
                LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inf.inflate(R.layout.chat_activity_item, parent, false);
            } else {
                View newView = new View(ctx);
                newView = (View) view;
            }

            final JSONObject json = (JSONObject) getItem(position);

            ImageView image_to = view.findViewById(R.id.chat_item_image);
            TextView title_to = view.findViewById(R.id.chat_item_nickname);
            TextView content_to = view.findViewById(R.id.chat_item_context);

            ImageView image_from = view.findViewById(R.id.chat_item_image_me);
            TextView title_from = view.findViewById(R.id.chat_item_nickname_me);
            TextView content_from = view.findViewById(R.id.chat_item_context_me);

            try {
                ImageView iv;
                TextView tit, text;

                if (json.getString("class").contentEquals("to")) {
                    title_from.setVisibility(View.INVISIBLE);
                    content_from.setVisibility(View.INVISIBLE);
                    image_from.setVisibility(View.INVISIBLE);

                    iv = image_to;
                    tit = title_to;
                    text = content_to;
                } else {
                    title_to.setVisibility(View.INVISIBLE);
                    content_to.setVisibility(View.INVISIBLE);
                    image_to.setVisibility(View.INVISIBLE);

                    iv = image_from;
                    tit = title_from;
                    text = content_from;
                }

                tit.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                iv.setVisibility(View.VISIBLE);

                if (img != null) {
                    Glide.with(view).load(img).into(iv);
                    iv.setPadding(0, 0, 0, 0);
                } else {
                    iv.setImageResource(R.drawable.baseline_account_circle_24);

                    DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
                    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
                    iv.setPadding(px, px, px, px);
                }

                iv.setClipToOutline(true);

                tit.setText(title);
                text.setText(json.getString("content"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }
}