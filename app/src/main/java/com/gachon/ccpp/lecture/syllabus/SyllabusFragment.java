package com.gachon.ccpp.lecture.syllabus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gachon.ccpp.R;
import com.gachon.ccpp.listener.onBackPressedListener;

public class SyllabusFragment extends Fragment implements onBackPressedListener {
    WebView syllabus;
    String content;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_syllabus, container, false);
        Bundle bundle = getArguments();
        content =  bundle.getString("syllabus");
        syllabus = view.findViewById(R.id.webView);
        syllabus.getSettings().setUseWideViewPort(true);
        syllabus.getSettings().setLoadWithOverviewMode(true);
        syllabus.getSettings().setBuiltInZoomControls(true);
        syllabus.getSettings().setSupportZoom(true);
        syllabus.loadData(content,"text/html","UTF-8");

        return view;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
}
