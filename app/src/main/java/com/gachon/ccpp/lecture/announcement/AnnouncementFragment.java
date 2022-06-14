package com.gachon.ccpp.lecture.announcement;

import static com.gachon.ccpp.MainActivity.api;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ccpp.MainActivity;
import com.gachon.ccpp.R;
import com.gachon.ccpp.dialog.LoadingDialog;
import com.gachon.ccpp.listener.onBackPressedListener;
import com.gachon.ccpp.parser.ContentForm;
import com.gachon.ccpp.parser.HtmlParser;
import com.gachon.ccpp.parser.ListForm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementFragment extends Fragment implements onBackPressedListener {
    RecyclerView recyclerView;
    AnnouncementAdapter adapter;
    ArrayList<ContentForm> announcementList;
    LoadingDialog dialog;
    int sizeOfData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announcementList = new ArrayList<>();
        dialog = new LoadingDialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        sizeOfData = -1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        Bundle bundle = getArguments();

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setAdapter(adapter);

        requestContent(bundle.getString("link"));
        dialog.show();

        return view;
    }

    private void requestContent(String url) {
        Call<ResponseBody> connect = api.getUri(url);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        String link = parser.getClassAnnouncementLink();
                        requestAnnouncementsLink(link);
                    } catch (IOException e) {
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    public void requestAnnouncementsLink(String link) {
        Call<ResponseBody> connect = api.getUri(link+"&ls=100");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ArrayList<ListForm> announcementLink = parser.getCourseAnnouncementList();
                        sizeOfData = announcementLink.size();
                        for(ListForm l :announcementLink){
                            requestAnnouncements(l.link,new ContentForm("","","","",l.payload));
                        }
                    } catch (Exception e) {
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void requestAnnouncements(String link,ContentForm content) {
        Call<ResponseBody> connect = api.getUri(link);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        HtmlParser parser = new HtmlParser(Jsoup.parse(response.body().string()));
                        ContentForm data = parser.getAnnouncementContent();
                        data.payload = content.payload;
                        adapter.addItem(data);
                        if(sizeOfData == adapter.getItemCount())
                            dialog.dismiss();
                    } catch (Exception e) {
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
