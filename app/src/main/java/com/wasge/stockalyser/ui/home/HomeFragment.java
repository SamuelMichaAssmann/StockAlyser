package com.wasge.stockalyser.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.wasge.stockalyser.R;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;
import com.yabu.livechart.view.LiveChartStyle;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        LiveChart liveChart = root.findViewById(R.id.live_chart);

        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
        for (int i = 0; i < 50; i++) {
            Random r = new Random();
            float rand = -10 + r.nextFloat() * (20);
            dataPoints.add(new DataPoint(i,rand));
        }

        ArrayList<DataPoint> dataPoints1 = new ArrayList<DataPoint>();
        dataPoints1.add(new DataPoint(0f,4f));
        dataPoints1.add(new DataPoint(1f,7f));
        dataPoints1.add(new DataPoint(2f,9f));
        dataPoints1.add(new DataPoint(3f,2f));
        dataPoints1.add(new DataPoint(4f,0f));

        Dataset dataset = new Dataset(dataPoints);
        Dataset dataset1 = new Dataset(dataPoints1);

        LiveChartStyle style = new LiveChartStyle();
        style.setBaselineStrokeWidth(5f);
        style.setMainColor(Color.GREEN);
        style.setSecondColor(Color.RED);
        style.setPathStrokeWidth(8f);
        style.setSecondPathStrokeWidth(8f);
        style.setOverlayLineColor(Color.BLUE);
        style.setOverlayCircleDiameter(32f);
        style.setOverlayCircleColor(Color.RED);
        style.setBaselineColor(Color.GRAY);
        style.setBaselineDashLineGap(100f);



        liveChart.setDataset(dataset)
                .setOnTouchCallbackListener(new LiveChart.OnTouchCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTouchCallback(@NotNull DataPoint dataPoint) {
                        Log.d("data", "x: " + dataPoint.getX() + "  y: " + dataPoint.getY());
                    }

                    @Override
                    public void onTouchFinished() {

                    }
                })
                .setSecondDataset(dataset1)
                .setLiveChartStyle(style)
                .drawBaselineFromFirstPoint()
                .drawFill(true)
                .drawDataset();

        return root;
    }
}