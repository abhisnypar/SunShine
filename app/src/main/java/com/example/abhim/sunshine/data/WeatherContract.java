package com.example.abhim.sunshine.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by abhim on 7/28/2016.
 *
 * Defines tables and columns for the weather database.
 */
public class WeatherContract {

    //To make it east to query for the exact date, we normalize all dates that go
    //in to the database to start of the Julian dat at UTC.

    public static long normalizeDate(long startDate){
        //normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
    /*
    Inner class that defines the table contents of the location table
    Students: This is where we will add strings. (Similar to what has been done
    for weatherEntry)
     */

    public static final class LocationEntry implements BaseColumns{
        public static final String TABLE_NAME = "location";

        //Column With location settings
        public static final String LOCATION_SETT = "location_setting";
        //Latitude and Longitude Column is stored as a float
        public static final String COLUMN_LAT = "coord_lat";
        public static final String COLUMN_LONG = "coord_lon";


        //Column with the City name for the Location
        public static final String CITY_NAME = "city_name";
    }

    /* Inner class the defines the contents of the weather table */
    public static final class WeatherEntry implements BaseColumns{

        public static final String TABLE_NAME = "weather";

        //Column with the foreign Key into location table
        public static final String COLUMN_LOC_KEY = "location_id";
        //Date, stored as milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        //Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        //Short description and long description of the weather, as provided by API.
        //eg: "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";


        //Min and Max temperatures for the day (stores as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        //Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        //Pressure us stored as a float representing percentage
        public static final String COLUMN_PRESSURE  = "pressure";

        //Wind speed is stored as a float representing wind speed mph
        public static final String COLUMN_WIND_SPEED = "wind";

        //Degree are meteorological degrees (e.g, 0 is north, 180 is south). Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

    }
}
