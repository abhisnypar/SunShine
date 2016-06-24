package com.example.abhim.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by abhim on 6/17/2016.
 */
public class ForecastFragment extends Fragment {


    public ArrayAdapter<String> mForecastAdapter;
    private ListView forecast_items;

    public ForecastFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add this line in order for this fragment to handle menu event
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        forecast_items = (ListView) rootView.findViewById(R.id.list_view_forecast);
        ArrayList<String> list_items = new ArrayList<String>();
        list_items.add("Mon 6/23 - Sunny - 31/17");
        list_items.add("Tomorrow-Foggy-70/46");
        list_items.add("Weds-Cloudy-72/63");
        list_items.add("Thurs-Rainy-64/51");
        list_items.add("Fri-Foggy-70/46");
        list_items.add("Sat-Sunny-76/68");
        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textView, list_items);
        forecast_items.setAdapter(mForecastAdapter);
        forecast_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forCastText = mForecastAdapter.getItem(position);
                Toast.makeText(getContext(), "This is the toast message" + forCastText, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastffragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
        Handle action bar item clicks here. The action bar will
        automatically handle clicks on the home/up button, so long
        as you specify a parent activity in AndroidManifest.xml
         */
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute("60007");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        /*
        *  Tha date/time conversion code is going to be moved outside the asynctask later,
        *  so for convenience we're breaking it out into its own method now.
        */

        private String getReadableDataString(long time) {
            //Because the API returns unique timestamp
            //it must be converted to milliseconds in order to be converted to valid date.

            SimpleDateFormat shortedenedDateFormat = new SimpleDateFormat("EE MM dd");
            return shortedenedDateFormat.format(time);
        }

        /**
         * Prepare the weather highs/lows for presentation
         */
        private String formatHighLows(double high, double low) {
            //For presentation, the user doesn't care about tenths of a degree
            long roundHigh = Math.round(high);
            long roundLow = Math.round(low);

            String highLowStr = roundHigh + "/" + roundLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON format and
         * pull out the data we need to construct the strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy: constructor takes the JSON string and convert it
         * in to an object hierarchy for us.
         */

        private String[] getWeatherDataFromJSon(String forecastJsonStr, int numDays) throws JSONException {

            //These are the Names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_temperature = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = new JSONArray();
            weatherArray = forecastJson.getJSONArray(OWM_LIST);


            //OWM returns daily forecasts based upon the local time of the city that is being
            //asked for, which means that we need to know the GMT offset to translate this data
            //properly.

            //Since this data is also sent in-order and the first day is always the
            //Current day, we're going to take advantage of that to get a nice
            //normalized UTC date for all of our weather.

            Time daytime = new Time();
            daytime.setToNow();

            //We start at the day returned by local time. Otherwise this is mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), daytime.gmtoff);

            //now we work exclusively in UTC.
            daytime = new Time();

            String[] resultStrns = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                //For now, using the format "Day,Description, hi/low
                String day;
                String description;
                String highAndLow;

                //Get the JSON Object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                //The date/time is returned as a long. We need to convert that
                //into something human-readable, since most people won't read "1400356800" as
                //"this saturday"
                long dateTime;
                //cheating to convert this to UTC time, which is what we want anyhow
                dateTime = daytime.setJulianDay(julianStartDay + i);
                day = getReadableDataString(dateTime);

                //description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                //Temperatures are in a child object called "temp". Try not to name variables
                //"temp" when working with temperature. It confuses everybody.

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_temperature);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrns[i] = day + "-" + description + "-" + highAndLow;

            }
            for (String s : resultStrns) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrns;
        }


        @Override
        protected String[] doInBackground(String... params) {

            //If there's no zip code, there's nothing to look up. Verify the size of params.

            if (params.length == 0) {
                //do nothing
                return null;
            }
            //These two should be declared outside of the try/catch block
            //so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //will contain raw json response as a string
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the openWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http: //openWeatherMap.org/API#forecast

                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built_URL" + builtUri.toString());
                // Create the request to openWeatherMAp, and open the connection

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the inputStream in to a string
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    //Since it's JSON, adding a newline isn't necessary (it won't effect parsing)
                    //But it does make debugging a *lot* easier if you print out the completed
                    //buffer for debugging.

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    //Stream was empty. No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("PlaceHolderFragment", "Error", e);
                /*
                If the code didn't successfully get the openWeatherMap data, no point
                in attempting to parse it.
                 */
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDataFromJSon(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            //This will happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
                // New data is back from the server. Hooray!
            }
        }


    }

}
