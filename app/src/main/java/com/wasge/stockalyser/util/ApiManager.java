package com.wasge.stockalyser.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
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
    //  trend?symbol=AAPL&interval=1min&apikey=your_api_key
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

    public String getUrlInformation(String rowUrl) throws MalformedURLException {
        URL url = new URL(rowUrl);
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
