package com.wasge.stockalyser.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseManager extends SQLiteOpenHelper {

    // https://developer.android.com/topic/libraries/architecture/room#java

    private static final String DB_NAME = "STOCK_DATA";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "STOCK_DATA";

    public DatabaseManager(final Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StockDataContract.initializeTables();

        //crate tables
        sqLiteDatabase.execSQL(StockDataContract.DailyEntry.createTable());
        sqLiteDatabase.execSQL(StockDataContract.WeeklyEntry.createTable());
        sqLiteDatabase.execSQL(StockDataContract.MonthlyEntry.createTable());
        sqLiteDatabase.execSQL(StockDataContract.YearlyEntry.createTable());

        sqLiteDatabase.execSQL(StockDataContract.Stocks.createTable());
        sqLiteDatabase.execSQL(StockDataContract.Watchlist.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(dropTable);
        onCreate(sqLiteDatabase);*/
    }

    public void insertTable (String TableName, String[] TableData){ // datentyp einfügen
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder createQuery = new StringBuilder();
        createQuery.append("INSERT INTO ")
                .append(TableName)
                .append("VALUES ('")
                .append(TableData[0]);
        for (int i = 1; i < TableData.length; i++) {
            createQuery.append("', '")
                    .append(TableData[i]);
        }
        createQuery.append("')");
        sqLiteDatabase.execSQL(createQuery.toString());
    }

    public void updateTable (String TableName, String[] TableData){

    }

    public List<String> readTable (){
        return null;
    }

    public void deleteTable (String TableName){

    }

    public void handleData(REQUEST_TYPE request_type, JSONObject object){


    }


    private void truncateData(SQLiteDatabase db){

        //delete Data, older than the specified longest date
        String deletion_template = "DELETE FROM %s WHERE %s <= date('now','%s')";
        String sql = String.format(deletion_template,
                StockDataContract.DailyEntry.TABLE_NAME,
                StockDataContract.DailyEntry.COLUMN_NAME_DATETIME,
                StockDataContract.DailyEntry.EXPIRATION_TIME);
        db.execSQL(sql);
        sql = String.format(deletion_template,
                StockDataContract.WeeklyEntry.TABLE_NAME,
                StockDataContract.WeeklyEntry.COLUMN_NAME_DATETIME,
                StockDataContract.WeeklyEntry.EXPIRATION_TIME);
        db.execSQL(sql);
        sql = String.format(deletion_template,
                StockDataContract.MonthlyEntry.TABLE_NAME,
                StockDataContract.MonthlyEntry.COLUMN_NAME_DATETIME,
                StockDataContract.MonthlyEntry.EXPIRATION_TIME);
        db.execSQL(sql);
        sql = String.format(deletion_template,
                StockDataContract.DailyEntry.TABLE_NAME,
                StockDataContract.DailyEntry.COLUMN_NAME_DATETIME,
                StockDataContract.DailyEntry.EXPIRATION_TIME);
        db.execSQL(sql);
    }

    // last is newest value

    //TODO remove generate
    private static float[] gernerateData(int many){
        float[] da = new float[many];
        float temp = 5;
        for (int i = 0; i < many; i++) {
            Random r = new Random();
            float rand = -1 + r.nextFloat() * 2;
            temp = temp + rand;
            da[i] = temp;
        }
        return da;
    }

    // SELECT * FROM Tabelle1 ORDER BY key DESC LIMIT 10
    public static float[] getTenDayData(String stockName){
        return gernerateData(10);
    }

    public static float[] getDayData(String stockName){
        return gernerateData(90);
    }

    public static float[] getWeekData(String stockName){
        return gernerateData(90);
    }

    public static float[] getMonthData(String stockName){
        return gernerateData(90);
    }

    public static float[] getYearData(String stockName){
        return gernerateData(90);
    }

    public static float[] getMaxData(String stockName){
        return gernerateData(90);
    }

    public static String[] getDisplayData(String stockName){
        String[] data = new String[]{
                "AAPL", "Apple Inc",
                "NASDAQ", "USD", "2020-11-17",

                "119.54900", "120.30000", "118.96000", "119.36000",
                "13012825", "120.30000", "-0.94000", "-0.78138",
                "106265760",

                "53.15250", "137.39000",
                "66.20750", "-18.03000",
                "124.56140", "-13.12323"
        };

        return data;
    }

    //---> [   ["AAPL", "Apple Inc", "NASDAQ", "USD", "avg()", "date"] , [...] , ...  ]
    public static ArrayList<String[]> getWatchlistStock(){

        // TODO insert real data
        ArrayList<String[]> watchlist = new ArrayList<>();
        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});

        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});

        return watchlist;
    }

    /**
     * @param Symbol i.e. "AAPL"
     * **/
    public static boolean removeFromWatchlist(String Symbol){
        return true;
    }

    /**
     * @param Stock i.e. ["AAPL", "Apple Inc", "NASDAQ", "USD", "avg()", "date"]
     * **/
    public static boolean addToWatchlist(String[] Stock){
        return true;
    }

    public static String[] getWatchlistStockIDs(){
        return new String[]{"AAPL","AAPL","AAPL",  "AAPL", "AAPL","AAPL"};
    }
}



enum REQUEST_TYPE {
    CURRENT_STATUS,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}


/*
-------- Api-json -------
{
"symbol":"AAPL", ========= ID
"name":"Apple Inc", ==========
"exchange":"NASDAQ", ==========
"currency":"USD", ==========
"datetime":"2020-11-17", ==========
"open":"119.54900",   -> \
"high":"120.30000",   ->  |
"low":"118.96000",    ->  | -> avg()
"close":"119.36000",  -> /
"volume":"13012825",
"previous_close":"120.30000",
"change":"-0.94000",
"percent_change":"-0.78138",
"average_volume":"106265760",   =========
"fifty_two_week":
    {                                          <--- getDisplayData() only
    "low":"53.15250",
    "high":"137.39000",
    "low_change":"66.20750",
    "high_change":"-18.03000",
    "low_change_percent":"124.56140",
    "high_change_percent":"-13.12323",
    "range":"53.152500 - 137.389999"
    }
}
 */