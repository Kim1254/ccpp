package com.gachon.ccpp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gachon.ccpp.util.DataHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView2);
        Intent intent = getIntent();

        String text = intent.getStringExtra("html");

        String out = "";

        JSONObject data = null;
        try {
            data = DataHandler.parse(text);
            for (Iterator<String> iter = data.keys(); iter.hasNext();) {
                out += (String)data.get(iter.next()) + "\n";
            }
        } catch (JSONException e) {
            Log.d("CCPP", "Failed parsing json on onCreate(html)");
        }
        textView.setText(out);
    }
}