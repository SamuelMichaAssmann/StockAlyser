package com.wasge.stockalyser.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wasge.stockalyser.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    ArrayAdapter<String> arrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        ListView listView = root.findViewById(R.id.searchlist);
        List<String> list = new ArrayList<>();
        list.add("Apple Inc");
        list.add("Airbnb");

        arrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, list);

        listView.setAdapter(arrayAdapter);


        return root;
    }
}