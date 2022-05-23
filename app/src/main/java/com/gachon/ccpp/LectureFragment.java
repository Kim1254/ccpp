package com.gachon.ccpp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;
import com.gachon.ccpp.util.DataHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class LectureFragment extends Fragment {
    View thisView;

    Set<String> lecture_list = Collections.synchronizedSet(new HashSet<String>());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_lecture, container, false);
        LinearLayout layout = thisView.findViewById(R.id.lecture_list);

        for (String n : lecture_list) {
            Button btn = new Button(getActivity());
            btn.setText(n);
            layout.addView(btn);
        }
        return thisView;
    }

    public void appendList(String data) {
        try {
            JSONObject value = new JSONObject(data);
            for (Iterator<String> s = value.keys(); s.hasNext();) {
                lecture_list.add(s.next());
            }
        } catch (JSONException e) {
        }
    }
}