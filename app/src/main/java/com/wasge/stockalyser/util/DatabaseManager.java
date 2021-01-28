package com.wasge.stockalyser.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseManager extends SQLiteOpenHelper {

    // https://developer.android.com/topic/libraries/architecture/room#java

    private static final String TAG = "DBManager";
    private static final String DB_NAME = "STOCK_DATA";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "STOCK_DATA";

    public DatabaseManager(final Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * Executes all the DELETE-statements found in the StockDataContract Class
     * **/
    public void deleteDB() {
        SQLiteDatabase dbR = getReadableDatabase();
        SQLiteDatabase dbW = getWritableDatabase();

        //delete tables
        dbW.execSQL(StockDataContract.DailyEntry.deleteTable());
        dbW.execSQL(StockDataContract.WeeklyEntry.deleteTable());
        dbW.execSQL(StockDataContract.MonthlyEntry.deleteTable());
        dbW.execSQL(StockDataContract.YearlyEntry.deleteTable());

        dbW.execSQL(StockDataContract.Stocks.deleteTable());
        dbW.execSQL(StockDataContract.Watchlist.deleteTable());
    }

    /**
     * Executes all the CREATE-statements found in the StockDataContract Class
     * **/
    public void initializeDB() {
        SQLiteDatabase dbR = getReadableDatabase();
        SQLiteDatabase dbW = getWritableDatabase();


        //crate tables
        dbW.execSQL(StockDataContract.DailyEntry.createTable());
        dbW.execSQL(StockDataContract.WeeklyEntry.createTable());
        dbW.execSQL(StockDataContract.MonthlyEntry.createTable());
        dbW.execSQL(StockDataContract.YearlyEntry.createTable());

        dbW.execSQL(StockDataContract.Stocks.createTable());
        dbW.execSQL(StockDataContract.Watchlist.createTable());


    }

    /**
     * Inserts sample data into the Database for debugging purposes
     * **/
    public void insertTestData() {
        insertToTable(StockDataContract.Watchlist.TABLE_NAME,new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
        insertToTable(StockDataContract.Stocks.TABLE_NAME,new String[]{
                "AAPL", "Apple Inc",
                "NASDAQ", "USD", "2020-11-17",

                "119.54900", "120.30000", "118.96000", "119.36000",
                "13012825", "120.30000", "-0.94000", "-0.78138",
                "106265760",

                "53.15250", "137.39000",
                "66.20750", "-18.03000",
                "124.56140", "-13.12323"
        });
    }

    /**
     * Required Method by inheritance
     * **/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //initializeDB();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(dropTable);
        onCreate(sqLiteDatabase);*/
    }

    /**
     * Inserts the given Data into the specified table. Note, that the Table must have an available
     * columnMap in the StockDataContract class.
     * @param table String of the tables name.
     * @param data String[] containing all the data to be inserted.
     * **/
    public void insertToTable(@NotNull String table, @NotNull String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columnMap = StockDataContract.getAppropriateColumnMap(table);

        ContentValues values = new ContentValues();
        for(int i = 0; i < columnMap.length && i < data.length; i++){
            if(data[i] == null) continue;
            values.put(columnMap[i],data[i]);
        }

        int id = (int) db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(table, values, columnMap[0]+"=?", new String[] {data[0]});
        }


    }

    /**
     * Handles data returned by the Api and stores it accordingly
     * @param request_type type of request to be handled
     * @param object JSONObject containing the data to be handled
     * **/
    public void handleData(@NotNull REQUEST_TYPE request_type,@NotNull JSONObject object) {
        // (...) "values":[{"datetime":"2021-01-20 10:15:00","open":"130.90010","high":"131.20000",
        //                  "low":"130.77000","close":"130.81160","volume":"4603463"},  (...) ]
        String targetTable = "";
        switch (request_type){
            case DAILY:
            case WEEKLY:
            case MONTHLY:
            case YEARLY:

                switch (request_type){
                    case DAILY:
                        targetTable = StockDataContract.DailyEntry.TABLE_NAME;
                        break;
                    case WEEKLY:
                        targetTable = StockDataContract.WeeklyEntry.TABLE_NAME;
                        break;
                    case MONTHLY:
                        targetTable = StockDataContract.MonthlyEntry.TABLE_NAME;
                        break;
                    case YEARLY:
                        targetTable = StockDataContract.YearlyEntry.TABLE_NAME;
                        break;
                }
                //TODO: extract data from JSON ARRAY and insert into table
                Log.d(TAG,"handling data: request type: " + request_type + " JSON:" + object);



                break;
            case CURRENT_STATUS:
                //TODO: extract data from JSON Object and save to table
                break;
        }


    }


    /**
     * Deletes Data from each table specified in StockDataContract, according to each tables maximum
     * data-lifespan.
     * **/
    private void truncateData() {

        SQLiteDatabase db = this.getWritableDatabase();
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
    private static float[] gernerateData(int many) {
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

    /**
     * Helper funcion returning a Query response from the Database containing the last N Elements
     * of a selected stock from a selected table
     * @param table String of Table to be selected
     * @param stock String of Stock symbol
     * @param n int specifying N
     * **/
    private Cursor getLastNElements(@NotNull String table, @NotNull String stock, int n) {
        SQLiteDatabase dbR = getReadableDatabase();
        return dbR.query(table,StockDataContract.IntervalEntry.getValueColums(),
                StockDataContract.IntervalEntry.FOREIGN_ID + " LIKE ?" ,
                new String[]{stock},
                null,null,
                StockDataContract.IntervalEntry.COLUMN_NAME_DATETIME + "DESC",
                n + "");
    }

    /**
     * Takes a Cursor to a Table of Floats and averages each row into a float array.
     * Afterwards the Cursor is closed.
     * @param cursor Cursor to a Table entirely made up of floats.
     * **/
    private float[] averageToFloatArr(@NotNull Cursor cursor){
        int elements = cursor.getCount();
        float[] output = new float[elements];
        String[] columns = cursor.getColumnNames();
        cursor.moveToLast();
        for(int i = 0; i < elements; i++){
            output[i] = 0;
            for(int j = 0; j < columns.length; j++){
                output[i] += cursor.getFloat(j);
            }
            output[i] /= columns.length;
            cursor.moveToPrevious();
        }

        cursor.close();
        return output;
    }

    /**
     * Either reduces or stretches a float array approximately to a desired size (+- 10).
     * stretching is done by adding the first value as padding to the front
     * **/
    private float[] reduceOrStretchToSize(@NotNull float[] original, int desiredSize){
        int originalSize = original.length;
        if(originalSize == 0) return gernerateData(90);
        int difference = desiredSize - originalSize;
        if(Math.abs(difference) < 10)  return original;
        if(difference > 0){
            float[] newArr = new float[desiredSize];
            //needs to fill the preceding empty spaces with the
            for(int i = 0; i < difference; i++){
                newArr[i] = original[0];
            }
            for(int i = 0; i < originalSize; i++){
                newArr[difference + i] = original[i];
            }
            return newArr;
        } else if(difference < 0){
            //TODO: omit data from array in a fairly distributed manner
            return original;
        }

        return  null;
    }

    // SELECT * FROM Tabelle1 ORDER BY key DESC LIMIT 10
    /**
     * Get last 10 Datapoints stored in the Daily Table of a given stock
     * @param stockName String of stock symbol
     * **/
    public float[] getTenDayData(@NotNull String stockName) {

        Cursor cursor = getLastNElements(StockDataContract.DailyEntry.TABLE_NAME,stockName,10);
        return gernerateData(10);
    }

    public float[] getDayData(@NotNull String stockName) {
        Cursor cursor = getLastNElements(StockDataContract.DailyEntry.TABLE_NAME,stockName,96);
        return reduceOrStretchToSize( averageToFloatArr(cursor) ,90);
    }

    public float[] getWeekData(@NotNull String stockName) {
        Cursor cursor = getLastNElements(StockDataContract.WeeklyEntry.TABLE_NAME,stockName,84);
        return reduceOrStretchToSize( averageToFloatArr(cursor) ,90);
    }

    public float[] getMonthData(@NotNull String stockName) {
        Cursor cursor = getLastNElements(StockDataContract.MonthlyEntry.TABLE_NAME,stockName,186);
        return reduceOrStretchToSize( averageToFloatArr(cursor) ,90);
    }

    public float[] getYearData(@NotNull String stockName) {
        Cursor cursor = getLastNElements(StockDataContract.DailyEntry.TABLE_NAME,stockName,356);
        return reduceOrStretchToSize( averageToFloatArr(cursor) ,90);
    }

    public float[] getMaxData(@NotNull String stockName) {
        return gernerateData(90);
    }

    public String[] getDisplayData(@NotNull String stockName) {

        String[] columns = StockDataContract.Stocks.getColumnMap();
        SQLiteDatabase dbR = getReadableDatabase();
        Cursor c = dbR.query(StockDataContract.Stocks.TABLE_NAME,null,
                StockDataContract.Stocks.ID + " LIKE ?" ,
                new String[]{stockName},
                null,null,
                StockDataContract.Stocks.COLUMN_NAME_DATETIME + "DESC",
                1 + "");
        c.moveToFirst();
        String[] output = new String[columns.length];
        for(int i = 0; i < columns.length; i++){
            output[i] = c.getString(c.getColumnIndex(columns[i]));
        }

        return output;
    }

    //---> [   ["AAPL", "Apple Inc", "NASDAQ", "USD", "avg()", "date"] , [...] , ...  ]
    public ArrayList<String[]> getWatchlistStock() {


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
     **/
    public boolean removeFromWatchlist(String Symbol) {
        return true;
    }

    /**
     * @param Stock i.e. ["AAPL", "Apple Inc", "NASDAQ", "USD", "avg()", "date"]
     **/
    public boolean addToWatchlist(String[] Stock) {

        return true;
    }

    public String[] getWatchlistStockIDs() {
        String template = "SELECT %s FROM %s ORDER BY ASC";
        String query = String.format(
                template,
                StockDataContract.Watchlist.ID,
                StockDataContract.Watchlist.TABLE_NAME);

        return new String[]{"AAPL", "AAPL", "AAPL", "AAPL", "AAPL", "AAPL"};
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