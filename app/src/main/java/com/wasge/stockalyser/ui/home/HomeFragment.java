package com.wasge.stockalyser.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.wasge.stockalyser.R;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;
import com.yabu.livechart.view.LiveChartStyle;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        LiveChartStyle style = new LiveChartStyle();

        LiveChart liveChart = root.findViewById(R.id.live_chart);
        final TextView currentPrice = root.findViewById(R.id.current_price);
        final TextView percentPrice = root.findViewById(R.id.percent_price);

        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
        float temp = 5;
        for (int i = 0; i < 96; i++) {
            Random r = new Random();
            float rand = -1 + r.nextFloat() * 2;
            temp = temp + rand;
            dataPoints.add(new DataPoint(i, temp));
        }

        final float start = dataPoints.get(0).getY();
        final float end = dataPoints.get(dataPoints.size()-1).getY();

        percentPrice.setText(setPercent(start, end));
        currentPrice.setText(setCurrent(end));

        Dataset dataset = new Dataset(dataPoints);

        liveChart.setDataset(dataset)
                .setOnTouchCallbackListener(new LiveChart.OnTouchCallback() {

                    @Override
                    public void onTouchCallback(@NotNull DataPoint dataPoint) {
                        Log.d("data", "x: " + dataPoint.getX() + "  y: " + dataPoint.getY());
                        currentPrice.setText(setCurrent(dataPoint.getY()));
                        percentPrice.setText(setPercent(start, dataPoint.getY()));
                    }

                    @Override
                    public void onTouchFinished() {
                        currentPrice.setText(setCurrent(end));
                        percentPrice.setText(setPercent(start, end));
                    }
                })
                .setLiveChartStyle(style)
                .drawBaselineFromFirstPoint()
                .drawDataset();

        return root;
    }

    private String setPercent(float start, float end){
        final DecimalFormat df = new DecimalFormat("#.##");
        String percentString = "";
        float percent = (100/start * end) - 100;
        if(percent >= 0 )
            percentString = "+" + df.format(percent);
        else
            percentString = df.format(percent);
        return percentString;

    }

    private String setCurrent(float end){
        final DecimalFormat df = new DecimalFormat("#.##");
        return df.format(end);

    }
}