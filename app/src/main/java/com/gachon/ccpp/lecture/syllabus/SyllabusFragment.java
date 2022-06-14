package com.gachon.ccpp.lecture.syllabus;

import static com.gachon.ccpp.MainActivity.api;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gachon.ccpp.R;
import com.gachon.ccpp.dialog.LoadingDialog;
import com.gachon.ccpp.listener.onBackPressedListener;
import com.gachon.ccpp.parser.HtmlParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyllabusFragment extends Fragment implements onBackPressedListener {
    WebView syllabus;
    String content;
    LoadingDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_syllabus, container, false);

        syllabus = view.findViewById(R.id.webView);
        syllabus.getSettings().setUseWideViewPort(true);
        syllabus.getSettings().setLoadWithOverviewMode(true);
        syllabus.getSettings().setBuiltInZoomControls(true);
        syllabus.getSettings().setSupportZoom(true);

        requestSyllabus(getArguments().getString("id"));
        dialog = new LoadingDialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        return view;
    }

    private void requestSyllabus(String id) {
        Call<ResponseBody> connect = api.syllabus(id);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        content = parser.getSyllabus();
                        syllabus.loadData(content,"text/html","UTF-8");
                        dialog.dismiss();
                    } catch (IOException e) {
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
}
