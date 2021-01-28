package com.wasge.stockalyser.ui.home;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.wasge.stockalyser.MainActivity;
import com.wasge.stockalyser.R;
import com.wasge.stockalyser.ui.ChartProcess;
import com.wasge.stockalyser.ui.StockFragment;
import com.wasge.stockalyser.util.DatabaseManager;
import com.wasge.stockalyser.util.ProcessData;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment {

    private MainActivity mainActivity;
    private DatabaseManager dbManager;
    private final ChartProcess c = new ChartProcess();

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
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        LiveChart liveChart = root.findViewById(R.id.pre_chart);
        TabLayout tabLayout = root.findViewById(R.id.tabs);
        TextView percent = root.findViewById(R.id.home_percent);
        TextView interval = root.findViewById(R.id.home_intervall);
        c.setData(liveChart, dbManager, interval,null, null, percent);
        c.setTab(tabLayout, liveChart, dbManager, interval, null, null, percent);

        return root;
    }
}