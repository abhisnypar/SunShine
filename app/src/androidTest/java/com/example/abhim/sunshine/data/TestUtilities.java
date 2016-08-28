package com.example.abhim.sunshine.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.abhim.sunshine.Utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by abhim on 8/3/2016.
 */
public class TestUtilities extends AndroidTestCase {

    private static final String TEST_LOCATION = "99705";
    private static final long TEST_DATE = 1419033600L;

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues){

        assertTrue("Empty cursor returned. " +error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {

        Set<Map.Entry<String,Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String,Object> entry: valueSet){

            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column'" +columnName + "'not found."+ error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value'" +entry.getValue().toString()+"'did not match the expected value'"+
            expectedValue+"'."+ error,expectedValue,valueCursor.getString(idx));

        }
    }
    /*
        Use this to create some default values for the database tests.
     */
    public static ContentValues createWeatherValues(long locationRowId){
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY,locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE,TEST_DATE);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,65);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,321);

        return weatherValues;
    }

    public static ContentValues createNorthPoleLocationValues() {

        //Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(WeatherContract.LocationEntry.LOCATION_SETT, TEST_LOCATION);
        testValues.put(WeatherContract.LocationEntry.CITY_NAME, "North Pole");
        testValues.put(WeatherContract.LocationEntry.COLUMN_LAT, 64.7856);
        testValues.put(WeatherContract.LocationEntry.COLUMN_LONG, -147.353);

        return testValues;
    }

    public static long insertNorthPoleLocationValues(Context context){
        //insert our test records into the database
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,testValues);

        //Verify we got a row back
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    public static class TestContentObserver extends ContentObserver {

        private final HandlerThread mHT;
        private boolean mContentChanged;


        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserver Thread");
            ht.start();
            return new TestContentObserver(ht);
        }

        public TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {

            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
        static TestContentObserver getTestContentObserver(){
            return TestContentObserver.getTestContentObserver();
        }

}
