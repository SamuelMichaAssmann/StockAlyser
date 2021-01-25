package com.wasge.stockalyser.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.wasge.stockalyser.R;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class StockFragment extends Fragment {

    String symbol;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_stock, container, false);
        TabLayout tabLayout = root.findViewById(R.id.tablayout);
        final LiveChart liveChart = root.findViewById(R.id.live_chart);

        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        getDataPoints(getData(0, symbol), dataPoints);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ArrayList<DataPoint> dataPoints = new ArrayList<>();
                getDataPoints(getData(tab.getPosition(), symbol), dataPoints);
                setLiveChart(root, dataPoints, liveChart);
                setIndicator(root, dataPoints, liveChart);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setLiveChart(root, dataPoints, liveChart);
        setIndicator(root, dataPoints, liveChart);
        // TODO
        //setData(root, DatabaseManager.getDisplayData(stock));

        return root;
    }

    private float[] getData(int style, String symbol){
        /*if (style == 1)
            return DatabaseManager.getWeekData(symbol);
        else if (style == 2)
            return DatabaseManager.getMonthData(symbol);
        else if (style == 3)
            return DatabaseManager.getYearData(symbol);
        else if (style == 4)
            return DatabaseManager.getMaxData(symbol);
        else
            return DatabaseManager.getDayData(symbol);
        */
        // TODO -------Test---------
        float[] da = new float[90];
        float temp = 5;
        for (int i = 0; i < 90; i++) {
            Random r = new Random();
            float rand = -1 + r.nextFloat() * 2;
            temp = temp + rand;
            da[i] = temp;
        }
        return da;
    }

    private void getDataPoints(float[] data, ArrayList<DataPoint> dataPoints){
        dataPoints.clear();
        int i = 0;
        for (float d:data) {
            dataPoints.add(new DataPoint(i, d));
            i++;
        }

    }

    private void setLiveChart(View root, ArrayList<DataPoint> dataPoints,  LiveChart liveChart){
        Dataset dataset = new Dataset(dataPoints);
        liveChart.setDataset(dataset)
                .drawBaselineFromFirstPoint()
                .drawDataset();
    }

    private void setIndicator(View root, ArrayList<DataPoint> dataPoints, LiveChart liveChart){
        final TextView currentPrice = root.findViewById(R.id.current_price);
        final TextView percentPrice = root.findViewById(R.id.percent_price);
        final float start = dataPoints.get(0).getY();
        final float end = dataPoints.get(dataPoints.size()-1).getY();

        percentPrice.setText(setPercent(start, end));
        currentPrice.setText(setCurrent(end));

        liveChart.setOnTouchCallbackListener(new LiveChart.OnTouchCallback() {

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
        });
    }

    private String setPercent(float start, float end){
        final DecimalFormat df = new DecimalFormat("#.##");
        String percentString = "";
        float percent = ((end-start) / start) * 100;
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

    private void setData(View root, String[] data){
        TextView name = root.findViewById(R.id.name_data);
        TextView exchange = root.findViewById(R.id.exchenge_data);
        TextView currency = root.findViewById(R.id.currency_data);
        TextView date = root.findViewById(R.id.date_data);
        TextView insert = root.findViewById(R.id.insert_data);
        TextView open = root.findViewById(R.id.open_data);
        TextView high = root.findViewById(R.id.high_data);
        TextView close = root.findViewById(R.id.close_data);
        TextView volumen = root.findViewById(R.id.volumen_data);
        TextView avgvolumen = root.findViewById(R.id.avgvolume_data);
        TextView preclose = root.findViewById(R.id.preclose_data);
        TextView range = root.findViewById(R.id.range_data);
        TextView perchange = root.findViewById(R.id.perchange_data);
        TextView yearlow = root.findViewById(R.id.yearlow_data);
        TextView yearhigh = root.findViewById(R.id.yearhigh_data);
        TextView yearlowchange = root.findViewById(R.id.yearlowchange_data);
        TextView yearhighchange = root.findViewById(R.id.yearhighchange_data);
        TextView perlowchange = root.findViewById(R.id.yearperlowchange_data);
        TextView perhighchange = root.findViewById(R.id.yearperhighchange_data);

        name.setText(data[0]);
        exchange.setText(data[1]);
        currency.setText(data[2]);
        date.setText(data[3]);
        insert.setText(data[4]);
        open.setText(data[5]);
        high.setText(data[6]);
        close.setText(data[7]);
        volumen.setText(data[8]);
        avgvolumen.setText(data[9]);
        preclose.setText(data[10]);
        range.setText(data[11]);
        perchange.setText(data[12]);
        yearlow.setText(data[13]);
        yearhigh.setText(data[14]);
        yearlowchange.setText(data[15]);
        yearhighchange.setText(data[16]);
        perlowchange.setText(data[17]);
        perhighchange.setText(data[18]);
    }
}