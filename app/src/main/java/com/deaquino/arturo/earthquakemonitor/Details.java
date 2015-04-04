package com.deaquino.arturo.earthquakemonitor;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class Details extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView magnitudeTextView = (TextView) this.findViewById(R.id.magnitudeTextView);
        TextView placeTextView = (TextView) this.findViewById(R.id.placeTextView);
        TextView dateTextView = (TextView) this.findViewById(R.id.dateTextView);

        Intent intent = getIntent();
        String dataString = intent.getStringExtra("eInfo");
        Log.d("Stuff from Intent: ", dataString);



        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        GoogleMap map = mapFragment.getMap();


        try {
            JSONObject eInfo = new JSONObject(dataString);
            magnitudeTextView.setText("Magnitude: " + eInfo.getString("magnitude"));
            magnitudeTextView.setBackgroundColor(EarthquakeInfo.setColor((int)(eInfo.getLong("magnitude"))));
            placeTextView.setText(eInfo.getString("place"));
            dateTextView.setText(eInfo.getString("date"));
            LatLng position = new LatLng(eInfo.getLong("latitude"), eInfo.getLong("longitude"));
            map.addMarker(new MarkerOptions()
                    .position(position)
                    .title(eInfo.getString("place")));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)
                    .zoom(10.0f)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
