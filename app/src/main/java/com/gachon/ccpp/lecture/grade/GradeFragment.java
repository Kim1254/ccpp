package com.gachon.ccpp.lecture.grade;

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

import com.gachon.ccpp.R;
import com.gachon.ccpp.dialog.LoadingDialog;
import com.gachon.ccpp.listener.onBackPressedListener;
import com.gachon.ccpp.parser.HtmlParser;
import com.gachon.ccpp.parser.TableForm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GradeFragment extends Fragment implements onBackPressedListener {
    RecyclerView recyclerView;
    GradeAdapter adapter;
    TableForm list;
    Map<Integer,TableForm> assignment;
    LoadingDialog dialog;
    int sizeOfData;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignment = new HashMap<>();
        list = new TableForm();
        dialog = new LoadingDialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        sizeOfData = -1;

        requestGrade(getArguments().getString("id"));
        dialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        adapter = new GradeAdapter(list,getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void requestGrade(String id) {
        Call<ResponseBody> connect = api.grade(id);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        list = parser.getGrade();
                        dialog.dismiss();
                        adapter.changeItem(list);
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
