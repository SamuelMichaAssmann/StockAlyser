package com.wasge.stockalyser.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.wasge.stockalyser.MainActivity;
import com.wasge.stockalyser.R;
import com.wasge.stockalyser.ui.ChartProcess;
import com.wasge.stockalyser.util.DatabaseManager;
import com.yabu.livechart.view.LiveChart;

public class HomeFragment extends Fragment {

    private MainActivity mainActivity;
    private DatabaseManager dbManager;
    private ChartProcess c;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = null;
        if (getActivity() instanceof MainActivity)
            mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            dbManager = mainActivity.getDatabaseManager();
            mainActivity.subscribeToMain(R.id.navigation_stock, this);
        }
        c = new ChartProcess(getContext());
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        LiveChart liveChart = root.findViewById(R.id.pre_chart);
        TabLayout tabLayout = root.findViewById(R.id.tabs);
        TextView percent = root.findViewById(R.id.home_percent);
        TextView interval = root.findViewById(R.id.home_intervall);

        TextView max = root.findViewById(R.id.home_max_data);
        TextView min = root.findViewById(R.id.home_min_data);
        TextView avg = root.findViewById(R.id.home_avg_data);

        c.setData(liveChart, dbManager,null, null, percent);
        c.setTab(tabLayout, liveChart, dbManager, interval, null, null, percent);
        c.setValues(max, min, avg);

        return root;
    }
}