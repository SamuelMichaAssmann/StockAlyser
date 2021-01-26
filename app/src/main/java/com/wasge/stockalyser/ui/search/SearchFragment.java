package com.wasge.stockalyser.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wasge.stockalyser.R;
import com.wasge.stockalyser.util.ApiManager;
import com.wasge.stockalyser.util.FragmentReciever;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements FragmentReciever {

    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    View root;

    @Override
    public void onStart() {
        super.onStart();
        listView = root.findViewById(R.id.searchlist);
        arrayAdapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_1, list);
        ApiManager apiManager = new ApiManager(root.getContext());

        ArrayList<String[]> stocklist = apiManager.parseJSONData(apiManager.search(), 0);
        for (String[] s : stocklist) {
            list.add(s[1]);
        }
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);

        return root;
    }

    @Override
    public void recieveData(Object[] data) {
        if (data[0] instanceof String) {
            ApiManager apiManager = new ApiManager(root.getContext());
            ArrayList<String[]> stocklist = apiManager.parseJSONData(apiManager.search((String) data[0]), 1);
            list.clear();
            for (String[] s : stocklist) {
                list.add(s[1]);
                Log.d("test", s[1]);
            }
            listView = root.findViewById(R.id.searchlist);
            arrayAdapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_1, list);
            listView.setAdapter(arrayAdapter);
        }
    }
}