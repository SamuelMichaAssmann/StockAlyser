package com.wasge.stockalyser.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public void createTable (String TableName, String[] TableData){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder createQuery = new StringBuilder();
        createQuery.append("CREATE TABLE ")
                .append(TableName)
                .append(" (ID INTEGER PRIMARY KEY");
        for (String datum : TableData) {
            createQuery.append(", ")
                    .append(datum)
                    .append(" TEXT NOT NULL");
        }
        createQuery.append(")");
        sqLiteDatabase.execSQL(createQuery.toString());
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

    private final String deletion_template = "DELETE FROM %s WHERE %s <= date('now','%s')";
    private void truncateData(SQLiteDatabase db){

        //delete Data, older than the specified longest date
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

    public static float[] getDayData(String stockName){
        return null;
    }

    public static float[] getWeekData(String stockName){
        return null;
    }

    public static float[] getMonthData(String stockName){
        return null;
    }

    public static float[] getYearData(String stockName){
        return null;
    }

    public static String[] getDisplayData(String stockName){
        return null;
    }

    //---> [   ["AAPL", "Apple Inc", "NASDAQ", "USD", "avg()"] , [...] , ...  ]
    public static ArrayList<String[]> getWatchlistStockIDs(){
        return null;
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