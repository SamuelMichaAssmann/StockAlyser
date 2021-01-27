package com.wasge.stockalyser.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.wasge.stockalyser.util.ApiManager;
import com.wasge.stockalyser.util.DatabaseManager;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockFragment extends Fragment {

    String symbol;
    ArrayList<DataPoint> dataPoints;
    LiveChart liveChart;
    View root;
    String interval = "15min";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = null;
        if (getActivity() instanceof MainActivity)
            mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.subscribeToMain(R.id.navigation_stock, this);

    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_stock, container, false);
        TabLayout tabLayout = root.findViewById(R.id.tablayout);
        final TextView intervall = root.findViewById(R.id.intervall);
        liveChart = root.findViewById(R.id.live_chart);
        dataPoints = new ArrayList<>();
        getDataPoints(getData(0, symbol), dataPoints);
        setLiveChart();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ArrayList<DataPoint> dataPoints = new ArrayList<>();
                getDataPoints(getData(tab.getPosition(), symbol), dataPoints);
                setInterval(tab.getPosition());
                setLiveChart();
                setIndicator();
                intervall.setText(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setLiveChart();
        setIndicator();
        setData(DatabaseManager.getDisplayData(symbol));

        return root;
    }

    private float[] getData(int style, String symbol) {
        if (style == 1)
            return DatabaseManager.getWeekData(symbol);
        else if (style == 2)
            return DatabaseManager.getMonthData(symbol);
        else if (style == 3)
            return DatabaseManager.getYearData(symbol);
        else if (style == 4)
            return DatabaseManager.getMaxData(symbol);
        else
            return DatabaseManager.getDayData(symbol);
    }

    private void getDataPoints(float[] data, ArrayList<DataPoint> dataPoints) {
        if (data == null)
            return;
        dataPoints.clear();
        int i = 0;
        for (float d : data) {
            dataPoints.add(new DataPoint(i, d));
            i++;
        }
    }

    private void setGraph(float[] output) {
        ArrayList<DataPoint> dataPoints2 = new ArrayList<>();
        getDataPoints(output,dataPoints2);
        Dataset dataset = new Dataset(dataPoints2);
        liveChart.setSecondDataset(dataset).drawDataset();
        Log.d("Trend", "Trend printed");
    }

    private void setLiveChart() {
        SharedPreferences PreferenceKey = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        String style = PreferenceKey.getString("trend", null);

        Log.d("Trend", style);
        if (style != null || !style.equals("none"))
            new TrendlineTask().execute(style);

        Dataset dataset = new Dataset(dataPoints);
        liveChart.setDataset(dataset)
                .drawBaselineFromFirstPoint()
                .drawDataset();
    }

    private void setIndicator() {
        final TextView currentPrice = root.findViewById(R.id.current_price);
        final TextView percentPrice = root.findViewById(R.id.percent_price);
        final float start = dataPoints.get(0).getY();
        final float end = dataPoints.get(dataPoints.size() - 1).getY();

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

    private void setData(String[] data) {
        TextView symbol = root.findViewById(R.id.symbol);
        TextView name = root.findViewById(R.id.name_data);
        TextView exchange = root.findViewById(R.id.exchenge_data);
        TextView currency = root.findViewById(R.id.currency_data);
        TextView date = root.findViewById(R.id.date_data);
        TextView insert = root.findViewById(R.id.insert_data);
        TextView open = root.findViewById(R.id.open_data);
        TextView high = root.findViewById(R.id.high_data);
        TextView low = root.findViewById(R.id.low_data);
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

        try {
            symbol.setText(data[1]);
            name.setText(data[1]);
            exchange.setText(data[2]);
            currency.setText(data[3]);
            date.setText(data[4]);
            //insert.setText(?);
            open.setText(data[5]);
            high.setText(data[6]);
            low.setText(data[7]);
            close.setText(data[8]);
            volumen.setText(data[9]);
            avgvolumen.setText(data[10]);
            preclose.setText(data[11]);
            range.setText(data[12]);
            perchange.setText(data[13]);
            yearlow.setText(data[14]);
            yearhigh.setText(data[15]);
            yearlowchange.setText(data[16]);
            yearhighchange.setText(data[17]);
            perlowchange.setText(data[18]);
            perhighchange.setText(data[19]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInterval(int intervalInt){
        switch (intervalInt){
            case 1:
                interval = "2h";
                break;
            case 2:
                interval = "4h";
                break;
            case 3:
                interval = "1d";
                break;
            case 4:
                interval = "1week";
                break;
            default:
                interval = "15min";
                break;
        }

    }

    public void recieveData(Object[] data) {
        if (data[0] instanceof String) {
            Log.d("Data", "Data received");
            this.symbol = (String) data[0];
        }
    }

    private class TrendlineTask extends AsyncTask<Object,Integer,Integer> {
        float[] output;

        @Override
        protected void onPostExecute(Integer strings) {
            setGraph(output);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * @param objects Url, kind if kind == 0 -> Url will be ignored
         * **/
        @Override
        protected Integer doInBackground(Object... objects) {
            try {
                ApiManager mng = new ApiManager(getContext());
                if(objects != null && objects.length == 1 && objects[0] instanceof String) {
                    Log.d("Trend", (String) objects[0] + " -- " + symbol + " -- " + interval);
                    Log.d("url", mng.buildUrl((String) objects[0], symbol, interval));
                    output = mng.parseJSONData(mng.buildUrl((String) objects[0], symbol, interval), (String) objects[0]);
                }
            }catch (Exception e){
                Log.e("searchFragment","BackgroundTask failed: " + e.getMessage());
            }
            return 1;
        }
    }
}