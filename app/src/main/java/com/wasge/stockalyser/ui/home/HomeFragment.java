package com.wasge.stockalyser.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.wasge.stockalyser.R;

import com.yabu.livechart.model.*;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;
import com.yabu.livechart.view.LiveChartStyle;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        LiveChart liveChart = root.findViewById(R.id.live_chart);
        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
        dataPoints.add(new DataPoint(0f,6f));
        dataPoints.add(new DataPoint(1f,3f));
        dataPoints.add(new DataPoint(2f,3f));
        dataPoints.add(new DataPoint(3f,4f));
        dataPoints.add(new DataPoint(4f,5f));

        ArrayList<DataPoint> dataPoints1 = new ArrayList<DataPoint>();
        dataPoints.add(new DataPoint(0f,4f));
        dataPoints.add(new DataPoint(1f,7f));
        dataPoints.add(new DataPoint(2f,9f));
        dataPoints.add(new DataPoint(3f,2f));
        dataPoints.add(new DataPoint(4f,0f));

        Dataset dataset = new Dataset(dataPoints);
        Dataset dataset1 = new Dataset(dataPoints1);

        liveChart.setDataset(dataset)
                .setSecondDataset(dataset1)
                .drawYBounds()
                .drawLastPointLabel()
                .drawBaselineConditionalColor()
                .drawVerticalGuidelines(4)
                .drawHorizontalGuidelines(4)
                .drawBaseline()
                .drawSmoothPath()
                .setBaselineManually(0.5f)
                .drawFill(true)
                .drawFill(true)
                .drawDataset();

        return root;
    }
}