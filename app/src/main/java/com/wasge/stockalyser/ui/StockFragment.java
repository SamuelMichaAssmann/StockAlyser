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
import com.wasge.stockalyser.util.REQUEST_TYPE;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockFragment extends Fragment {

    private MainActivity mainActivity;
    private DatabaseManager dbManager;
    private final ChartProcess c = new ChartProcess();


    LiveChart liveChart;
    View root;
    String interval = "15min";

    //Stock Data:
    // columns: symbol, name, exchange, currency, average;
    private String symbol, name, exchange, currency, average, date, TAG ="StockFragment";

    //for testing
    boolean watched = false;

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"entered onResume()");
        if(isInWatchlist())
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark);
        else
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark_border);
        mainActivity.setBookmarkVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"entered onPause()");
        mainActivity.setBookmarkVisibility(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate() entered");
        mainActivity = null;
        if (getActivity() instanceof MainActivity)
            mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            dbManager = mainActivity.getDatabaseManager();
            mainActivity.subscribeToMain(R.id.navigation_stock, this);
        }
        this.symbol = mainActivity.getSymbol_for_stock_fragment();


    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView() entered");
        root = inflater.inflate(R.layout.fragment_stock, container, false);
        final TextView currentPrice = root.findViewById(R.id.current_price);
        final TextView percentPrice = root.findViewById(R.id.percent_price);
        final TextView inter = root.findViewById(R.id.intervall);
        liveChart = root.findViewById(R.id.live_chart);
        TabLayout tabLayout = root.findViewById(R.id.tablayout);

        c.setData(liveChart, dbManager, inter,null, currentPrice, percentPrice);
        c.setTab(tabLayout, liveChart, dbManager, inter, null, currentPrice, percentPrice);

        setData(dbManager.getDisplayData(symbol));
        if(symbol != null) {
            if(dbManager.hasStockInfo(symbol))
                setData(dbManager.getDisplayData(symbol));
            else
                new StockDataTask().execute(symbol);
        }
        return root;
    }

    private void setData(String[] data) {
        TextView symbol = root.findViewById(R.id.symbol);
        if(data == null) {
            symbol.setText(this.symbol);
            Log.e(TAG,"error setting data, null recieved");
            return;
        }



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

            for(int i = 0; i < 5; i++){
                if(i == data.length)
                    throw new Exception("Error setting data for Stock Fragment," +
                            " data might be incorrect or corrupted!");
                else if(data[i] == null) {
                    throw new Exception("Error setting data for Stock Fragment," +
                            " data might be incorrect or corrupted!");
                }
            }

            //symbol = 0, name = 1, exchange = 2, currency = 3, date = 4, open = 5,
            // high = 6, low = 7, close = 8, volume = 9, avgvolume = 10, preclose = 11,
            // range = 12, perchange = 13, yearlow = 14, yearhigh = 15, yearlowchange = 16,
            // yearhighchange = 17, yearlowchangeper = 18, yearhighchangeper = 19

            this.name = data[1];
            this.exchange = data[2];
            this.currency = data[3];
            this.date = data[4];
            this.average = data[5];


            symbol.setText(this.symbol);
            name.setText(this.name);
            exchange.setText(this.exchange);
            currency.setText(this.currency);
            date.setText(this.date);
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

    private void setGraph(float[] output) {
        if (output == null)
            return;
        Dataset dataset2 = new Dataset(c.getDataPoints(output));
        liveChart.setSecondDataset(dataset2)
                .drawBaselineFromFirstPoint()
                .drawDataset();
    }

    public void toggleCurrentToWatchlist(){
        //for testing
        watched = !watched;

        if(isInWatchlist())
            removeFromWatchlist();
        else
            addToWatchlist();
        updateBookmark();
    }

    private void removeFromWatchlist(){
        if(dbManager.removeFromWatchlist(symbol))
            Log.d(TAG, "successfully removed stock: " + symbol + " from watchlist!");
        else
            Log.d(TAG,"failed to remove stock: " + symbol + " from watchlist!");
    }

    private void addToWatchlist(){
        if(dbManager.addToWatchlist(new String[]{symbol, name, exchange, currency, average, date}))
            Log.d(TAG, "successfully added stock: " + symbol + " to watchlist!");
        else
            Log.d(TAG,"failed to add stock: " + symbol + " to watchlist!");

    }

    private void updateBookmark(){
        if(isInWatchlist())
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark);
        else
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark_border);
    }

    private boolean isInWatchlist(){

        String[] ids = dbManager.getWatchlistStockIDs();
        if(ids == null || ids.length < 1)
            return false;
        for (String id: ids){
            if(id == null)
                continue;
            if(symbol.contentEquals(id))
                return true;
        }
        return false;
    }


    private class TrendlineTask extends AsyncTask<Object,Integer,Integer> {
        float[] output;

        @Override
        protected void onPostExecute(Integer strings) {
            if (output != null)
                setGraph(output);
            else
                Log.d("Data", "data empty");
        }

        /**
         * @param objects Url, kind if kind == 0 -> Url will be ignored
         **/
        @Override
        protected Integer doInBackground(Object... objects) {
            try {
                ApiManager mng = new ApiManager(getContext());
                if (objects != null && objects.length == 1 && objects[0] instanceof String) {
                    Log.d("Trend", (String) objects[0] + " -- " + symbol + " -- " + interval);
                    Log.d("url", mng.buildUrl((String) objects[0], symbol, interval));
                    output = mng.parseJSONData(mng.buildUrl((String) objects[0], symbol, interval), (String) objects[0]);
                }
            } catch (Exception e) {
                Log.e("searchFragment", "BackgroundTask failed: " + e.getMessage());
            }
            return 1;
        }
    }

    private class StockDataTask extends AsyncTask<Object,Integer,Integer>{
        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            setData(dbManager.getDisplayData(symbol));
        }

        @Override
        protected Integer doInBackground(Object... objects) {
            //TODO: request data from api (and insert to database)
            //dbManager.handleData(REQUEST_TYPE.CURRENT_STATUS,  *insert JSON Data here*  );
            return 0;
        }
    }
}