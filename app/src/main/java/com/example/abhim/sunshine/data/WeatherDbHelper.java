package com.example.abhim.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abhim on 7/28/2016.
 *
 * Manages a local database for weather data.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    //If you change the database schema, you must increment the database version
    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE" + WeatherContract.WeatherEntry.TABLE_NAME + "(" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                WeatherContract.WeatherEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +


                // ID of the location entry associated with this weather data
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + "INTEGER NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_DATE + "INTEGER NOT NULL" +
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + "TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + "INTEGER NOT NULL," +

                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + "REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + "REAL NOT NULL," +

                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + "REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE + "REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + "REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_DEGREES + "REAL NOT NULL," +

                //Set up location column as a foreign key to location tables

                "FOREIGN KEY ( " + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                WeatherContract.LocationEntry.TABLE_NAME + "( " + WeatherContract.LocationEntry._ID + ")," +

                //To ensure the application have just one weather entry per day
                //per location, it's created a UNIQUE constraint with REPLACE strategy
                "UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ", " +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.    }

        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }  
}