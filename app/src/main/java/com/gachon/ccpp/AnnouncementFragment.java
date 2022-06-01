package com.gachon.ccpp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ccpp.parser.ContentForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AnnouncementFragment extends Fragment implements onBackPressedListener{
    RecyclerView recyclerView;
    AnnouncementAdapter adapter;
    ArrayList<ContentForm> announcementList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        Bundle bundle = getArguments();
        announcementList = (ArrayList<ContentForm>) bundle.getSerializable("list");
        Collections.sort(announcementList, (a, b) -> Integer.valueOf(b.payload)- Integer.valueOf(a.payload));

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
}
