package com.example.abhim.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ForecastFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this add items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle
        int id = item.getItemId();
        if (id == R.id.action_map){
            onPreferencesMapLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onPreferencesMapLocation() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location  = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

        //Using URI Scheme for showing a location found . This super-handy
        //intent can is detailed in the Common Intents page of Android's developer site:
        //http:developers.android.com/guide/components/intents-common.html#maps

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",location)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else {
            Log.d(LOG_TAG,"Couldn't call the " +location+ " ,no receiving apps installed");
        }
    }
}
