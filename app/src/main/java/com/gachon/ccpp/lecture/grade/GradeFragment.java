package com.gachon.ccpp.lecture.grade;

import static com.gachon.ccpp.MainActivity.api;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        list = (TableForm) bundle.getSerializable("list");

        assignment = new HashMap<>();
        requestAssignment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        adapter = new GradeAdapter(list,assignment,getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void requestAssignment(){
        int i =0;
        for(Map<Integer,String> row : list.table){
            requestContent(row.get(1),i);
            i = i+1;
        }
    }

    private void requestContent(String url,int position) {
        Call<ResponseBody> connect = api.getUri(url);
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        HtmlParser parser = new HtmlParser(html);
                        TableForm tableForm = parser.getAssignment();
                        assignment.put(position,tableForm);
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
