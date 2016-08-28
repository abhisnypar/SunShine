package com.example.abhim.sunshine.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by abhim on 7/31/2016.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    //Since we want each test to start with a clean state
    public void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database. This makes
        sure that we always have a clean test.
     */

    @Override
    protected void setUp() throws Exception {
        deleteTheDatabase();
    }
    /*
        This only test only the location table has the correct columns, since we
        give you the code for the weather table. This test does not look at the
     */

    public void testCreateDb() throws Throwable {
        // build a HashSet of all the tables names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();

        assertEquals(true,db.isOpen());

        //have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type ='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        //verify the tables that have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables.

        assertTrue("Error: your database was created without both location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        //now, do your tables contain certain columns?
        c = db.rawQuery("PREGMA table info (" + WeatherContract.LocationEntry.TABLE_NAME + ")", null);

        assertTrue("Error:This means that we were unable to query the database for the table information.", c.moveToFirst());

        //Build  a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.LOCATION_SETT);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        //if this fails, it means that your database doesn't contain all of the required location.
        //entry columns

        assertTrue("Error: The database doesn't contain all of the required location entry columns", locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Here is where we will build code to test that we can insert and query the
        location database. We've done a lot of work for you. You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function. You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
     */
    public void testLocationTable() {

        //First step: Get reference to writable database.

        // Create contentValues of what you want to insert
        // (you can use the creatorNorthPoleLocationValues if you wish)
        // Insert Content values into database and get a rowID back
        // Query the database and receive a Cursor back
        // Move the cursor to a valid database row.
        // Validate data in resulting cursor with the original Content values.
        // (You can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        //Finally, close the cursor and database.

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long rowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);
        assertTrue(rowId != -1);
        Cursor cursor = null;

        cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, new String[]{WeatherContract.LocationEntry._ID,WeatherContract.LocationEntry.LOCATION_SETT
                        , WeatherContract.LocationEntry.CITY_NAME, WeatherContract.LocationEntry.COLUMN_LAT, WeatherContract.LocationEntry.COLUMN_LONG}
                , null, null, null, null, null);

        //Move the cursor to the valid database.
        assertTrue("Error:No records are returned from location query", cursor.moveToFirst());

        //Validate the data in resulting Cursor with the original contentValues
        TestUtilities.validateCurrentRecord("Error: Location Query validation Failed", cursor, testValues);

        //Finally close the cursor and the database.
        //Move the cursor to demonstrate that there is only one record in the databse.
        assertFalse("Error: More than one record returned from the location query", cursor.moveToNext());
        cursor.close();
        db.close();
    }


    public void testWeatherTable() {


    }

    public long insertLocation(){
        return -1L;
    }

}
