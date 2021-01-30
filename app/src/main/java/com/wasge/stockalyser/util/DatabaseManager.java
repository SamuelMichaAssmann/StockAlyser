package com.wasge.stockalyser.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
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

        dbR.close();
        dbW.close();
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

        dbR.close();
        dbW.close();
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
            values.put(columnMap[i],data[i]);
        }

        int id = (int) db.insert(table, null, values);
        if (id == -1) {
            db.update(table, values, columnMap[0]+"=?", new String[] {data[0]});
        }
        db.close();


    }

    /**
     * Handles data returned by the Api and stores it accordingly
     * @param request_type type of request to be handled
     * @param object JSONObject containing the data to be handled
     * **/
    public void handleData(@NotNull REQUEST_TYPE request_type,@NotNull JSONObject object) {
        if(object == null) {
            Log.e(TAG,"couldnt parse JSON data, object was null!");
            return;
        }

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
                // (...) "values":[{"datetime":"2021-01-20 10:15:00","open":"130.90010","high":"131.20000",
                //                  "low":"130.77000","close":"130.81160","volume":"4603463"},  (...) ]
                try {
                    JSONObject meta = object.getJSONObject("meta");
                    String symbol = meta.getString("symbol");
                    JSONArray values = object.getJSONArray("values");
                    int elements = values.length();
                    for(int i = 0; i < elements; i++){
                        String[] data = JSONObjectToIntervalArray(values.getJSONObject(i), symbol);
                        insertToTable(targetTable,data);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                Log.d(TAG,"handling data: request type: " + request_type + " JSON:" + object);


                break;
            case CURRENT_STATUS:

                try {
                    String[] data = JSONObjectToStockArray(object);
                    insertToTable(StockDataContract.Stocks.TABLE_NAME,data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
        }


    }

    /**
     * @param object json privided by Api within "values" array
     * @param symbol stock id
     * @return String Array containing all values for interval table in order
     * **/
    private String[] JSONObjectToIntervalArray(JSONObject object, String symbol) throws JSONException{
        // (...) "values":[{"datetime":"2021-01-20 10:15:00","open":"130.90010","high":"131.20000",
        //                  "low":"130.77000","close":"130.81160","volume":"4603463"},  (...) ]

        // stock = 0; datetime = 1; open = 2; high = 3; low = 4; close = 5; volume 6
        String[] data = new String[7];
        data[0] = symbol;
        data[1] = object.getString("datetime");
        data[2] = object.getString("open");
        data[3] = object.getString("high");
        data[4] = object.getString("low");
        data[5] = object.getString("close");
        data[6] = object.getString("volume");
        return data;
    }

    /**
     * @param object json object provided by api request
     * @return String array containing all values for stock table in order
     * **/
    private String[] JSONObjectToStockArray(JSONObject object) throws JSONException{
        //JSON:
        //"symbol","name","exchange","currency","datetime","open","high","low","close","volume","previous_close","change","percent_change","average_volume","fifty_two_week"
        //"low","high","low_change","high_change","low_change_percent","high_change_percent","range"

        //
        //symbol = 0, name = 1, exchange = 2, currency = 3, date = 4, open = 5,
        // high = 6, low = 7, close = 8, volume = 9, avgvolume = 10, preclose = 11,
        // range = 12, perchange = 13, yearlow = 14, yearhigh = 15, yearlowchange = 16,
        // yearhighchange = 17, yearlowchangeper = 18, yearhighchangeper = 19
        JSONObject weekObj = object.getJSONObject("fifty_two_week");
        String[] data = new String[20];
        data[0] = object.getString("symbol");
        data[1] = object.getString("name");
        data[2] = object.getString("exchange");
        data[3] = object.getString("currency");
        data[4] = object.getString("datetime");
        data[5] = object.getString("open");
        data[6] = object.getString("high");
        data[7] = object.getString("low");
        data[8] = object.getString("close");
        data[9] = object.getString("colume");
        data[10] = object.getString("average_volume");
        data[11] = object.getString("previous_close");
        data[12] = weekObj.getString("range");
        data[13] = object.getString("percent_change");
        data[14] = weekObj.getString("low");
        data[15] = weekObj.getString("high");
        data[16] = weekObj.getString("low_change");
        data[17] = weekObj.getString("high_change");
        data[18] = weekObj.getString("low_change_percent");
        data[19] = weekObj.getString("high_change_percent");
        return data;
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
        db.close();
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
        Cursor out = dbR.query(table,StockDataContract.IntervalEntry.getValueColums(),
                StockDataContract.IntervalEntry.FOREIGN_ID + " LIKE ?" ,
                new String[]{stock},
                null,null,
                StockDataContract.IntervalEntry.COLUMN_NAME_DATETIME + " DESC",
                n + "");
        return out;
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

    /**
     * returns null if none found in database
     * **/
    public String[] getDisplayData(@NotNull String stockName) {

        String[] columns = StockDataContract.Stocks.getColumnMap();
        SQLiteDatabase dbR = getReadableDatabase();
        Cursor c = dbR.query(StockDataContract.Stocks.TABLE_NAME,null,
                StockDataContract.Stocks.ID + " LIKE ?" ,
                new String[]{stockName},
                null,null,
                StockDataContract.Stocks.COLUMN_NAME_DATETIME + " DESC",
                1 + "");
        if(c.getCount() < 1){
            return null;
        }
        c.moveToFirst();
        String[] output = new String[columns.length];
        for(int i = 0; i < columns.length; i++){
            output[i] = c.getString(c.getColumnIndex(columns[i]));
        }
        c.close();
        dbR.close();

        return output;
    }

    //---> [   ["AAPL", "Apple Inc", "NASDAQ", "USD", "avg()", "date"] , [...] , ...  ]
    public ArrayList<String[]> getWatchlistStock() {
        ArrayList<String[]> watchlist = new ArrayList<>();

        SQLiteDatabase dbR = getReadableDatabase();
        Cursor c = dbR.query(StockDataContract.Watchlist.TABLE_NAME,null,
                null,
                null,
                null,null,
                StockDataContract.Watchlist.ID + " DESC");
        if(c.getCount() < 1) return watchlist;
        c.moveToFirst();
        String[] columns = c.getColumnNames();
        while(!c.isAfterLast()){
            String[] stock = new String[columns.length];
            for(int i = 0; i< columns.length;i++){
                stock[i] = c.getString(i);
            }
            watchlist.add(stock);
            c.moveToNext();
        }
        c.close();
//        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
//        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
//        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
//
//        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
//        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});
//        watchlist.add(new String[]{"AAPL", "Apple Inc", "NASDAQ", "USD", "123.546", "20-12-2021"});

        return watchlist;
    }

    /**
     * @param symbol i.e. "AAPL"
     **/
    public boolean removeFromWatchlist(String symbol) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(StockDataContract.Watchlist.TABLE_NAME,
                StockDataContract.Watchlist.ID + " = \"" + symbol + "\"",
                null) > 0;
    }

    /**
     * @param stock i.e. ["AAPL", "Apple Inc", "NASDAQ", "USD", "avg()", "date"]
     **/
    public boolean addToWatchlist(String[] stock) {
        insertToTable(StockDataContract.Watchlist.TABLE_NAME,stock);
        return true;
    }

    public boolean hasStockInfo(String symbol){
        return getDisplayData(symbol) != null;
    }

    public String[] getWatchlistStockIDs() {


        SQLiteDatabase dbR = getReadableDatabase();
        Cursor c = dbR.query(StockDataContract.Watchlist.TABLE_NAME,null,
                null,
                null,
                null,null,
                StockDataContract.Watchlist.ID + " DESC");
        String[] out = new String[c.getCount()];
        if(c.getCount() < 1) return out;
        c.moveToFirst();
        for(int i = 0; i < out.length; i++){
            out[i] = c.getString(c.getColumnIndex(StockDataContract.Watchlist.ID));
            c.moveToNext();
        }
        c.close();

        return out;
    }
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