package com.wasge.stockalyser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiManager {

    // Heute 15min ==>
    // Woche 2h
    // Monat 4h - jeden 2  löschen
    // Jahr 1d - jeden 3  löschen

    // 1min, 5min, 15min, 30min, 45min, 1h, 2h, 4h, 1day, 1week, 1month
    private Context context;
    private String apikey;
    private String entypoint = "https://api.twelvedata.com/";

    public ApiManager(Context context) {
        SharedPreferences PreferenceKey = PreferenceManager.getDefaultSharedPreferences(context);
        this.apikey = PreferenceKey.getString("apikey", null);
        this.context = context;
    }


    //  time_series?symbol=AAPL&interval=1min&apikey=your_api_key --- Time Series
    //  stock?symbol=AAPL&interval=1min&apikey=your_api_key --- Interval Data
    //  trend?symbol=AAPL&interval=1min&apikey=your_api_key --- Trendgraph
    public String buildUrl(String kind, String symbol, String interval){
        return entypoint + kind + "?symbol=" + symbol + "&interval=" + interval + "&apikey=" + apikey;
    }


    //  quote?symbol=AAPL&apikey=your_api_key --- Stock Info
    //  price?symbol=AAPL&apikey=your_api_key --- Stock Price
    public String buildUrl(String kind, String symbol){
        return entypoint + kind + "?symbol=" + symbol  + "&apikey=" + apikey;
    }


    //symbol_search?symbol=AA --- Search Stock
    public String search(String stock){
        return entypoint + "symbol_search?symbol=" + stock;
    }

    //  api_usage?apikey=your_api_key --- Search Stock
    public String usage(){
        return entypoint + "api_usage?apikey=" + apikey;
    }

    public String getUrlInformation(String rowUrl)  {
        URL url = null;
        try {
            url = new URL(rowUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            for (String line; (line = reader.readLine()) != null;) {
                s.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

    public void parseJsonFromInterval(String input){
        try {
            JSONObject meta = (JSONObject) new JSONObject(input).get("meta");
            JSONArray values = new JSONObject(input).getJSONArray("values");

            System.out.println(meta.get("symbol"));
            System.out.println(meta.get("symbol"));
            System.out.println(meta.get("symbol"));
            System.out.println(meta.get("symbol"));

            for (int i = 0; i < values.length(); i++){

                System.out.print(values.getJSONObject(i).get("volume"));
                System.out.print(values.getJSONObject(i).get("datetime"));
                System.out.print(values.getJSONObject(i).get("high"));
                System.out.print(values.getJSONObject(i).get("low"));
                System.out.print(values.getJSONObject(i).get("close"));
                System.out.println(values.getJSONObject(i).get("open"));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
// 15 min intervall
// https://api.twelvedata.com/time_series?symbol=AAPL&interval=15min&apikey=2d9d8679a270405da1f46095b5b1ae27
/*
{.......},"values":[{"datetime":"2021-01-20 10:15:00","open":"130.90010","high":"131.20000","low":"130.77000","close":"130.81160","volume":"4603463"},{"datetime":"2021-01-20 10:00:00","open":"130.00000","high":"130.95000","low":"129.80000","close":"130.89000","volume":"5311046"},{"datetime":"2021-01-20 09:45:00","open":"130.24500","high":"130.42261","low":"129.88000","close":"129.98500","volume":"5825876"},{"datetime":"2021-01-20 09:30:00","open":"128.56000","high":"130.36000","low":"128.55000","close":"130.25000","volume":"10223683"},{"datetime":"2021-01-19 15:45:00","open":"127.94500","high":"128.00000","low":"127.64000","close":"127.85000","volume":"5033616"},{"datetime":"2021-01-19 15:30:00","open":"127.88000","high":"127.98000","low":"127.82020","close":"127.94930","volume":"1877565"},{"datetime":"2021-01-19 15:15:00","open":"127.97000","high":"127.98000","low":"127.77000","close":"127.87500","volume":"1622234"},{"datetime":"2021-01-19 15:00:00","open":"127.90000","high":"128.13000","low":"127.89400","close":"127.98000","volume":"1772761"},{"datetime":"2021-01-19 14:45:00","open":"127.73000","high":"127.97000","low":"127.68500","close":"127.89000","volume":"1408682"},{"datetime":"2021-01-19 14:30:00","open":"128.05499","high":"128.12500","low":"127.68000","close":"127.73000","volume":"1880055"},{"datetime":"2021-01-19 14:15:00","open":"127.93130","high":"128.19000","low":"127.93000","close":"128.05051","volume":"1632908"},{"datetime":"2021-01-19 14:00:00","open":"127.94500","high":"127.99980","low":"127.83000","close":"127.93010","volume":"1432267"},{"datetime":"2021-01-19 13:45:00","open":"127.98400","high":"128.12000","low":"127.89000","close":"127.95000","volume":"1495979"},{"datetime":"2021-01-19 13:30:00","open":"128.12000","high":"128.17999","low":"127.92000","close":"127.98000","volume":"1575351"},{"datetime":"2021-01-19 13:15:00","open":"128.19000","high":"128.25999","low":"128.02000","close":"128.11000","volume":"1690531"},{"datetime":"2021-01-19 13:00:00","open":"128.03931","high":"128.20000","low":"127.94000","close":"128.17999","volume":"1918403"},{"datetime":"2021-01-19 12:45:00","open":"127.59000","high":"128.08080","low":"127.55000","close":"128.03011","volume":"2721613"},{"datetime":"2021-01-19 12:30:00","open":"127.38390","high":"127.61000","low":"127.29000","close":"127.58000","volume":"1918212"},{"datetime":"2021-01-19 12:15:00","open":"127.59820","high":"127.68000","low":"127.36500","close":"127.37000","volume":"1660691"},{"datetime":"2021-01-19 12:00:00","open":"127.70080","high":"127.78000","low":"127.36000","close":"127.59930","volume":"2127074"},{"datetime":"2021-01-19 11:45:00","open":"127.32000","high":"127.79000","low":"127.32000","close":"127.71000","volume":"2122583"},{"datetime":"2021-01-19 11:30:00","open":"127.18000","high":"127.37000","low":"127.12000","close":"127.32000","volume":"1625194"},{"datetime":"2021-01-19 11:15:00","open":"127.17500","high":"127.29000","low":"127.05000","close":"127.18000","volume":"2547326"},{"datetime":"2021-01-19 11:00:00","open":"127.10000","high":"127.56000","low":"127.08000","close":"127.18000","volume":"3347428"},{"datetime":"2021-01-19 10:45:00","open":"127.27500","high":"127.42000","low":"126.99000","close":"127.10500","volume":"2728249"},{"datetime":"2021-01-19 10:30:00","open":"127.18000","high":"127.28000","low":"126.95000","close":"127.27500","volume":"2844590"},{"datetime":"2021-01-19 10:15:00","open":"127.40990","high":"127.50000","low":"126.95000","close":"127.16930","volume":"3678344"},{"datetime":"2021-01-19 10:00:00","open":"127.46500","high":"127.48000","low":"126.93800","close":"127.40940","volume":"5034120"},{"datetime":"2021-01-19 09:45:00","open":"127.95000","high":"128.21899","low":"127.32000","close":"127.46000","volume":"11753675"},{"datetime":"2021-01-19 09:30:00","open":"127.75000","high":"128.71001","low":"127.72000","close":"127.95000","volume":"6373188"}],"status":"ok"}
 */

//watchlist
/*

-------- Api-json -------
{
"symbol":"AAPL", ====== ID
"name":"Apple Inc", ===========
"exchange":"NASDAQ", ===========
"currency":"USD", ===========
"datetime":"2020-11-17", ========
"open":"119.54900",
"high":"120.30000",
"low":"118.96000",
"close":"119.36000",
"volume":"13012825",
"previous_close":"120.30000",
"change":"-0.94000",
"percent_change":"-0.78138",
"average_volume":"106265760",
"fifty_two_week":
    {
    "low":"53.15250", ==========
    "high":"137.39000", ===========
    "low_change":"66.20750", =========
    "high_change":"-18.03000", =========
    "low_change_percent":"124.56140", ==========
    "high_change_percent":"-13.12323",==========
    "range":"53.152500 - 137.389999"===========
    }
}
 */