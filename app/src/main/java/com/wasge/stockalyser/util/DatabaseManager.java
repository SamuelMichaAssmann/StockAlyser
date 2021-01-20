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


