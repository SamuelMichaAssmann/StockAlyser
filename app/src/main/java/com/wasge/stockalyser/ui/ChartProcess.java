package com.wasge.stockalyser.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
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

    private String TAG = "ChartProcess";
    private String inter = "15min";
    private ApiManager mng;
    private Context context;
    private ArrayList<DataPoint> data = null;
    private LiveChart liveChart;
    public String symbol;
    public TabLayout tabLayout;
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

    /**
     * Sets the Tablisener for changing date
     **/
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

    /**
     * Gets data of the database
     * @param style is the kind of data you like to recive (like day, week, month, etc.)
     * @param symbol what stock
     * @param dbm need the DatabaseManager for watchlistdata
     **/
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

    /**
     * Data manipulation in ProcessData
     * @param dbm need the DatabaseManager for watchlistdata
     * @param style is the kind of data you like to recive (like day, week, month, etc.)
     **/
    private float[] getAllData(DatabaseManager dbm, int style){
        String[] stocks = dbm.getWatchlistStockIDs();
        ProcessData processData = new ProcessData();
        ArrayList<float[]> data = new ArrayList<>();
        for (String stock : stocks) {
            data.add(getData(style, stock, dbm));
            for (float e : getData(style, stock, dbm)) {
                Log.d(TAG, String.valueOf(e));
            }
        }
        float[] output = processData.compactData(data);
        return output;
    }

    /**
     * @param data get row data an change it to datapoints of livechart
     **/
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

    /**
     * Can set a second graph like a trendline
     **/
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

    /**
     * Changes values while interaction with livechart
     **/
    private void setIndicator(ArrayList<DataPoint> dataPoints, LiveChart liveChart, final TextView currentPrice, final TextView percentPrice) {
        if (dataPoints == null || dataPoints.size() < 1){
            Log.e(TAG,"datapoints don't exist");
            return;
        }
        final float start = dataPoints.get(0).getY();
        final float end = dataPoints.get(dataPoints.size() - 1).getY();
        percentPrice.setText(setPercent(start, end));
        if (currentPrice != null)
            currentPrice.setText(setCurrent(end));

        liveChart.setOnTouchCallbackListener(new LiveChart.OnTouchCallback() {

            @Override
            public void onTouchCallback(@NotNull DataPoint dataPoint) {
                Log.d(TAG, "x: " + dataPoint.getX() + "  y: " + dataPoint.getY());
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

    /**
     * calculates the percentvalue and prettyprint
     **/
    private String setPercent(float start, float end) {
        String percentString;
        float percent;
        if (start != 0) {
            Log.d(TAG, end + " - " + start);
            percent = ((end - start) / start) * 100;
        }
        else
            percent = end * 100;
        if (percent >= 0)
            percentString = "+" + setCurrent(percent);
        else
            percentString = setCurrent(percent);
        return percentString;
    }

    private String setCurrent(float end) {
        final DecimalFormat df = new DecimalFormat("#.##");
        return df.format(end);
    }

    /**
     * Change a int into a interval for a url
     * @param intervalInt get a int of the tablayout
     **/
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

    private class TrendlineTask extends ToastyAsyncTask<String,Integer,Integer> {
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
                Log.d(TAG, "data empty");
        }

        /**
         * @param strings Url, kind if kind == 0 -> Url will be ignored
         **/
        @Override
        protected Integer doInBackground(String... strings){
            try {
                SharedPreferences PreferenceKey = PreferenceManager.getDefaultSharedPreferences(context);
                String style = PreferenceKey.getString("trend", null);
                if (strings != null && strings.length == 2) {
                    Log.d(TAG, "BackgroundTask startet!");
                    output = mng.parseJSONData(mng.buildUrl(style, strings[0], strings[1]), style);
                }
            } catch (Exception e) {
                errorOccured = true;
                Log.e(TAG, "BackgroundTask failed: " + e.getMessage());
            }
            return 1;
        }
    }
}
