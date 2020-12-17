package com.wasge.stockalyser.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
       /* String createQuery = "CREATE TABLE "
                + TABLE_NAME
                + " (ID INTEGER PRIMARY KEY, "
                + "SYMBOL TEXT NOT NULL, "
                + "NAME NOT NULL, "
                + "EXCHANGE NOT NULL, "
                + "CURRENCY NOT NULL, "
                + "DATETIME NOT NULL, "
                + "OPEN NOT NULL, "
                + "HIGH NOT NULL, "
                +"LOW NOT NULL)";
        sqLiteDatabase.execSQL(createQuery);*/
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
}


/*
-------- Api-json -------
{
"symbol":"AAPL",
"name":"Apple Inc",
"exchange":"NASDAQ",
"currency":"USD",
"datetime":"2020-11-17",
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