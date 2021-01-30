package com.wasge.stockalyser.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.wasge.stockalyser.util.ApiManager;
import com.wasge.stockalyser.util.DatabaseManager;
import com.wasge.stockalyser.util.ProcessData;
import com.wasge.stockalyser.util.ToastyAsyncTask;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;
import org.jetbrains.annotations.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChartProcess {

    private String inter = "15min";
    ApiManager mng;
    Context context;
    private ArrayList<DataPoint> data = null;
    LiveChart liveChart;
    String symbol;
    TabLayout tabLayout;
    private float NaN;

    public ChartProcess(Context context) {
        this.context = context;
        this.mng  = new ApiManager(context);
    }

    public void setData(final LiveChart liveChart, DatabaseManager dbm, final String symbol, final TextView currentPrice, final TextView percentPrice){
        new TrendlineTask(context).execute(symbol, inter);
        this.liveChart = liveChart;
        this.symbol = symbol;
        ArrayList<DataPoint> dataPoints = getDataPoints(getData(0,symbol, dbm));
        this.data = dataPoints;
        setLiveChart( dataPoints);
        setIndicator(dataPoints, liveChart, currentPrice, percentPrice);
    }

    public void setTab(TabLayout tabLayout, final LiveChart liveChart, final DatabaseManager dbm, final TextView interval, final String symbol, final TextView currentPrice, final TextView percentPrice){
        this.liveChart = liveChart;
        this.symbol = symbol;
        this.tabLayout = tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ArrayList<DataPoint> dataPoints = getDataPoints(getData(tab.getPosition(),symbol, dbm));
                setLiveChart(dataPoints);
                interval.setText(tab.getText());
                setInterval(tab.getPosition());
                setIndicator(dataPoints, liveChart,currentPrice, percentPrice);
                new TrendlineTask(context).execute(symbol, inter);
                data = dataPoints;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void setValues(TextView max, TextView min, TextView average){
        float data_min = data.get(0).getY();
        float data_max = data.get(0).getY();
        float data_avg = 0;
        for (int i = 0; i < data.size(); i++) {
            if(data_max < data.get(i).getY())
                data_max = data.get(i).getY();
            if(data_min > data.get(i).getY())
                data_min = data.get(i).getY();
            data_avg += data.get(i).getY();
        }
        data_avg = data_avg / data.size();
        max.setText(setCurrent(data_max));
        min.setText(setCurrent(data_min));
        average.setText(setCurrent(data_avg));


    }

    public float[] getData(int style, String symbol, DatabaseManager dbm) {
        if (symbol == null)
            return getAllData(dbm, style);
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

    private float[] getAllData(DatabaseManager dbm, int style){
        String[] stocks = dbm.getWatchlistStockIDs();
        ProcessData processData = new ProcessData();
        ArrayList<float[]> data = new ArrayList<>();
        for (String stock : stocks) {
            data.add(getData(style, stock, dbm));
            for (float e : getData(style, stock, dbm)) {
                Log.d("Test", String.valueOf(e));
            }


        }
        float[] output = processData.compactData(data);
        return output;
    }

    public ArrayList<DataPoint> getDataPoints(float[] data) {
        if (data == null)
            return null;
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        int i = 0;
        for (float d : data) {
            if (d != NaN)
                dataPoints.add(new DataPoint(i, d));
            i++;
        }
        return dataPoints;
    }

    public void setLiveChart( ArrayList<DataPoint> dataPoints) {
        if (dataPoints == null)
            return;
        Dataset dataset = new Dataset(dataPoints);
        liveChart.setDataset(dataset)
                .drawBaselineFromFirstPoint()
                .drawDataset();
    }

    private void setTrend(float[] trenddata) {
        if (trenddata == null)
            return;
        int len = this.data.size() - trenddata.length;
        float[] trend = new float[this.data.size()];
        for (int i = len; i < this.data.size(); i++) {
            trend[i] = trenddata[i-len];
        }

        Dataset dataset = new Dataset(getDataPoints(trend));
        liveChart.setSecondDataset(dataset)
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
        float percent;
        if (start != 0) {
            Log.wtf("Data", end + " - " + start);
            percent = ((end - start) / start) * 100;
        }
        else
            percent = end * 100;
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

    public void setInterval(int intervalInt){
        switch (intervalInt){
            case 1:
                inter = "2h";
                break;
            case 2:
                inter = "4h";
                break;
            case 3:
                inter = "1day";
                break;
            case 4:
                inter = "1week";
                break;
            default:
                inter = "15min";
                break;
        }
    }

    private class TrendlineTask extends ToastyAsyncTask<Object,Integer,Integer> {
        float[] output;
        boolean errorOccured = false;

        public TrendlineTask(Context context) {
            super(context);
            message = "Error! Couldn't load Trendgraph Data!";
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            if (output != null)
                setTrend(output);
            else
                Log.d("Data", "data empty");
        }

        /**
         * @param objects Url, kind if kind == 0 -> Url will be ignored
         **/
        @Override
        protected Integer doInBackground(Object... objects){
            try {
                SharedPreferences PreferenceKey = PreferenceManager.getDefaultSharedPreferences(context);
                String style = PreferenceKey.getString("trend", null);
                if (objects != null && objects.length == 2 && objects[0] instanceof String && objects[1] instanceof String) {
                    Log.d("Chartprocess", "BackgroundTask startet!");
                    output = mng.parseJSONData(mng.buildUrl(style, (String) objects[0], (String) objects[1]), style);
                }
            } catch (Exception e) {
                errorOccured = true;
                Log.e("Chartprocess", "BackgroundTask failed: " + e.getMessage());
            }
            return 1;
        }
    }
}
