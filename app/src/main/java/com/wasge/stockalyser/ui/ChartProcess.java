package com.wasge.stockalyser.ui;

import android.util.Log;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.wasge.stockalyser.util.DatabaseManager;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;
import org.jetbrains.annotations.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChartProcess {

    public void setData( final LiveChart liveChart, DatabaseManager dbm, TextView intervall, final String symbol, final TextView currentPrice, final TextView percentPrice){
        ArrayList<DataPoint> dataPoints = getDataPoints(getData(0,symbol, dbm));
        setLiveChart(liveChart, dataPoints);
        setIndicator(dataPoints, liveChart, currentPrice, percentPrice);
    }

    public void setTab(TabLayout tabLayout, final LiveChart liveChart, final DatabaseManager dbm, final TextView interval, final String symbol, final TextView currentPrice, final TextView percentPrice){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ArrayList<DataPoint> dataPoints = getDataPoints(getData(tab.getPosition(),symbol, dbm));
                setLiveChart(liveChart, dataPoints);
                interval.setText(tab.getText());
                setIndicator(dataPoints, liveChart,currentPrice, percentPrice);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public float[] getData(int style, String symbol, DatabaseManager dbm) {
        if (style == 1)
            return dbm.getWeekData(symbol);
        else if (style == 2)
            return dbm.getMonthData(symbol);
        else if (style == 3)
            return dbm.getYearData(symbol);
        else if (style == 4)
            return dbm.getMaxData(symbol);
        else
            return dbm.getDayData(symbol);
    }

    public ArrayList<DataPoint> getDataPoints(float[] data) {
        if (data == null)
            return null;
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        int i = 0;
        for (float d : data) {
            dataPoints.add(new DataPoint(i, d));
            i++;
        }
        return dataPoints;
    }

    private void setLiveChart(LiveChart liveChart, ArrayList<DataPoint> dataPoints) {
        if (dataPoints == null)
            return;
        Dataset dataset = new Dataset(dataPoints);
        liveChart.setDataset(dataset)
                .drawBaselineFromFirstPoint()
                .drawDataset();
    }

    private void setIndicator(ArrayList<DataPoint> dataPoints, LiveChart liveChart, final TextView currentPrice, final TextView percentPrice) {
        if (dataPoints == null)
            return;

        final float start = dataPoints.get(0).getY();
        final float end = dataPoints.get(dataPoints.size() - 1).getY();
        percentPrice.setText(setPercent(start, end));
        if (currentPrice != null)
            currentPrice.setText(setCurrent(end));

        liveChart.setOnTouchCallbackListener(new LiveChart.OnTouchCallback() {

            @Override
            public void onTouchCallback(@NotNull DataPoint dataPoint) {
                Log.d("LiveChart", "x: " + dataPoint.getX() + "  y: " + dataPoint.getY());
                if (currentPrice != null)
                    currentPrice.setText(setCurrent(dataPoint.getY()));
                percentPrice.setText(setPercent(start, dataPoint.getY()));
            }

            @Override
            public void onTouchFinished() {
                if (currentPrice != null)
                    currentPrice.setText(setCurrent(end));
                percentPrice.setText(setPercent(start, end));
            }
        });
    }

    private String setPercent(float start, float end) {
        final DecimalFormat df = new DecimalFormat("#.##");
        String percentString;
        float percent = ((end - start) / start) * 100;
        if (percent >= 0)
            percentString = "+" + df.format(percent);
        else
            percentString = df.format(percent);
        return percentString;
    }

    private String setCurrent(float end) {
        final DecimalFormat df = new DecimalFormat("#.##");
        return df.format(end);
    }

    public String setInterval(int intervalInt){
        String interval;
        switch (intervalInt){
            case 1:
                interval = "2h";
                break;
            case 2:
                interval = "4h";
                break;
            case 3:
                interval = "1day";
                break;
            case 4:
                interval = "1week";
                break;
            default:
                interval = "15min";
                break;
        }
        return interval;
    }
}
