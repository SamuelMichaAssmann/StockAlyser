package com.wasge.stockalyser.util;

public class StockDataContract {
    private StockDataContract(){}

    public static String[] getAppropriateColumnMap(String tableName){
        String[] output = new String[]{};
        if(tableName == null) return output;
        if(tableName.contentEquals(YearlyEntry.TABLE_NAME) || tableName.contentEquals(MonthlyEntry.TABLE_NAME) || tableName.contentEquals(WeeklyEntry.TABLE_NAME) || tableName.contentEquals(DailyEntry.TABLE_NAME)){
            return IntervalEntry.getColumnMap();
        } else if(tableName.contentEquals(Stocks.TABLE_NAME)){
            return Stocks.getColumnMap();
        } else if(tableName.contentEquals(Watchlist.TABLE_NAME)){
            return Watchlist.getColumnMap();
        }
        return output;
    }

    //INTERVAL DATA
    public static class DailyEntry extends IntervalEntry{
        public static String TABLE_NAME = "daily";
        public static String EXPIRATION_TIME = "-32 hours";

        private DailyEntry() {}

        public static String createTable(){
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    FOREIGN_ID + " TEXT NOT NULL, " +
                    COLUMN_NAME_DATETIME + " TEXT NOT NULL, " +
                    COLUMN_NAME_OPEN + " REAL NOT NULL, " +
                    COLUMN_NAME_HIGH  + " REAL NOT NULL, " +
                    COLUMN_NAME_LOW  + " REAL NOT NULL, " +
                    COLUMN_NAME_CLOSE + " REAL NOT NULL, " +
                    COLUMN_NAME_VOLUME + " INTEGER NOT NULL)";
        }

        public static String deleteTable(){
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }


    }

    public static class WeeklyEntry extends IntervalEntry{
        public static String TABLE_NAME = "weekly";
        public static String EXPIRATION_TIME = "-8 days";

        private WeeklyEntry() {}

        public static String createTable(){
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    FOREIGN_ID + " TEXT NOT NULL, " +
                    COLUMN_NAME_DATETIME + " TEXT NOT NULL, " +
                    COLUMN_NAME_OPEN + " REAL NOT NULL, " +
                    COLUMN_NAME_HIGH  + " REAL NOT NULL, " +
                    COLUMN_NAME_LOW  + " REAL NOT NULL, " +
                    COLUMN_NAME_CLOSE + " REAL NOT NULL, " +
                    COLUMN_NAME_VOLUME + " INTEGER NOT NULL)";
        }

        public static String deleteTable(){
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }

    public static class MonthlyEntry extends IntervalEntry{
        public static String TABLE_NAME = "monthly";
        public static String EXPIRATION_TIME = "-40 days";

        private MonthlyEntry(){}

        public static String createTable(){
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    FOREIGN_ID + " TEXT NOT NULL, " +
                    COLUMN_NAME_DATETIME + " TEXT NOT NULL, " +
                    COLUMN_NAME_OPEN + " REAL NOT NULL, " +
                    COLUMN_NAME_HIGH  + " REAL NOT NULL, " +
                    COLUMN_NAME_LOW  + " REAL NOT NULL, " +
                    COLUMN_NAME_CLOSE + " REAL NOT NULL, " +
                    COLUMN_NAME_VOLUME + " INTEGER NOT NULL)";
        }

        public static String deleteTable(){
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }

    public static class YearlyEntry extends IntervalEntry{
        public static String TABLE_NAME = "yearly";
        public static String EXPIRATION_TIME = "-370 days";

        private YearlyEntry() {
        }

        public static String createTable(){
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    FOREIGN_ID + " TEXT NOT NULL, " +
                    COLUMN_NAME_DATETIME + " TEXT NOT NULL, " +
                    COLUMN_NAME_OPEN + " REAL NOT NULL, " +
                    COLUMN_NAME_HIGH  + " REAL NOT NULL, " +
                    COLUMN_NAME_LOW  + " REAL NOT NULL, " +
                    COLUMN_NAME_CLOSE + " REAL NOT NULL, " +
                    COLUMN_NAME_VOLUME + " INTEGER NOT NULL)";
        }

        public static String deleteTable(){
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }

    public static class IntervalEntry{


        public static final String FOREIGN_ID = "stock";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_OPEN = "open";
        public static final String COLUMN_NAME_HIGH = "high";
        public static final String COLUMN_NAME_LOW = "low";
        public static final String COLUMN_NAME_CLOSE = "close";
        public static final String COLUMN_NAME_VOLUME = "volume";

        public static final String[] getColumnMap(){
            return new String[]{
                    FOREIGN_ID,
                    COLUMN_NAME_DATETIME,
                    COLUMN_NAME_OPEN,
                    COLUMN_NAME_HIGH,
                    COLUMN_NAME_LOW,
                    COLUMN_NAME_CLOSE,
                    COLUMN_NAME_VOLUME
            };
        }

        public static final String[] getValueColums(){
            return new String[]{
                    COLUMN_NAME_OPEN,
                    COLUMN_NAME_HIGH,
                    COLUMN_NAME_LOW,
                    COLUMN_NAME_CLOSE
            };
        }

    }

    //SINGULAR DATA
    public static class Stocks{
        public static final String TABLE_NAME = "stocks";
        //METADATA
        public static final String ID = "stock";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EXCHANGE = "exchange";
        public static final String COLUMN_NAME_CURRENCY = "currency";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        //CURRENT DATA
        public static final String COLUMN_NAME_OPEN = "open";
        public static final String COLUMN_NAME_HIGH = "high";
        public static final String COLUMN_NAME_LOW = "low";
        public static final String COLUMN_NAME_CLOSE = "close";
        public static final String COLUMN_NAME_VOLUME = "volume";
        public static final String COLUMN_NAME_AVGVOLUME = "average_volume";
        public static final String COLUMN_NAME_PRECLOSE = "previous_close";
        public static final String COLUMN_NAME_RANGE = "range";
        public static final String COLUMN_NAME_PERCHANGE = "change";
        //52WEEKS
        public static final String COLUMN_NAME_52HIGH = "fhigh";
        public static final String COLUMN_NAME_52LOW = "flow";
        public static final String COLUMN_NAME_52HIGH_CHANGE = "fhigh_change";
        public static final String COLUMN_NAME_52LOW_CHANGE = "flow_change";
        public static final String COLUMN_NAME_52HIGH_CHANGE_PERCENT = "fhigh_change_p";
        public static final String COLUMN_NAME_52LOW_CHANGE_PERCENT = "flow_change_p";

        public static final String createTable(){
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                    COLUMN_NAME_EXCHANGE + " TEXT NOT NULL, " +
                    COLUMN_NAME_CURRENCY + " TEXT NOT NULL, " +
                    COLUMN_NAME_DATETIME + " TEXT NOT NULL, " +
                    COLUMN_NAME_OPEN + " REAL NOT NULL, " +
                    COLUMN_NAME_HIGH  + " REAL NOT NULL, " +
                    COLUMN_NAME_LOW  + " REAL NOT NULL, " +
                    COLUMN_NAME_CLOSE + " REAL NOT NULL, " +
                    COLUMN_NAME_VOLUME + " INTEGER NOT NULL, " +
                    COLUMN_NAME_AVGVOLUME + " INTEGER NOT NULL, " +
                    COLUMN_NAME_PRECLOSE + " REAL NOT NULL, " +
                    COLUMN_NAME_RANGE + " TEXT NOT NULL, " +
                    COLUMN_NAME_PERCHANGE + " REAL NOT NULL, " +

                    COLUMN_NAME_52HIGH  + " REAL NOT NULL, " +
                    COLUMN_NAME_52LOW  + " REAL NOT NULL, " +
                    COLUMN_NAME_52HIGH_CHANGE  + " REAL NOT NULL, " +
                    COLUMN_NAME_52LOW_CHANGE  + " REAL NOT NULL, " +
                    COLUMN_NAME_52HIGH_CHANGE_PERCENT  + " REAL NOT NULL, " +
                    COLUMN_NAME_52LOW_CHANGE_PERCENT  + " REAL NOT NULL)" ;
        }
        public static final String deleteTable(){
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        //symbol = 0, name = 1, exchange = 2, currency = 3, date = 4, open = 5,
        // high = 6, low = 7, close = 8, volume = 9, avgvolume = 10, preclose = 11,
        // range = 12, perchange = 13, yearlow = 14, yearhigh = 15, yearlowchange = 16,
        // yearhighchange = 17, yearlowchangeper = 18, yearhighchangeper = 19

        public static final String[] getColumnMap(){
            return new String[]{
                    ID,
                    COLUMN_NAME_NAME,
                    COLUMN_NAME_EXCHANGE,
                    COLUMN_NAME_CURRENCY,
                    COLUMN_NAME_DATETIME,

                    COLUMN_NAME_OPEN,
                    COLUMN_NAME_HIGH,
                    COLUMN_NAME_LOW,
                    COLUMN_NAME_CLOSE,
                    COLUMN_NAME_VOLUME,
                    COLUMN_NAME_AVGVOLUME,
                    COLUMN_NAME_PRECLOSE,
                    COLUMN_NAME_RANGE,
                    COLUMN_NAME_PERCHANGE,

                    COLUMN_NAME_52HIGH,COLUMN_NAME_52LOW,
                    COLUMN_NAME_52HIGH_CHANGE,COLUMN_NAME_52LOW_CHANGE,
                    COLUMN_NAME_52HIGH_CHANGE_PERCENT,COLUMN_NAME_52LOW_CHANGE_PERCENT
            };
        }
    }



    public static class Watchlist{
        public static final String TABLE_NAME = "watchlist";

        // columns: "stock", "name", "exchange", "currency", "average";
        public static final String ID = "stock";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EXCHANGE = "exchange";
        public static final String COLUMN_NAME_CURRENCY = "currency";
        public static final String COLUMN_NAME_AVG = "average";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String createTable(){
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                    COLUMN_NAME_EXCHANGE + " TEXT NOT NULL, " +
                    COLUMN_NAME_CURRENCY + " TEXT NOT NULL, " +
                    COLUMN_NAME_AVG + " REAL NOT NULL," +
                    COLUMN_NAME_DATE + " TEXT NOT NULL)";
        }
        public static final String deleteTable(){
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        public static final String[] getColumnMap(){
            return new String[]{
                    ID,
                    COLUMN_NAME_NAME,
                    COLUMN_NAME_EXCHANGE,
                    COLUMN_NAME_CURRENCY,
                    COLUMN_NAME_AVG,
                    COLUMN_NAME_DATE
            };
        }
    }

}

// (...) "values":[{"datetime":"2021-01-20 10:15:00","open":"130.90010","high":"131.20000",
//                  "low":"130.77000","close":"130.81160","volume":"4603463"},             (...)

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
