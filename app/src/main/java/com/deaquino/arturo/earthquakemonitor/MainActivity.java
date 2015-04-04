package com.deaquino.arturo.earthquakemonitor;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private EarthquakeAdapter adapter;
    private JSONObject currentData;
    private List<EarthquakeInfo> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        currentList = new ArrayList<>();
        currentList = fillList(false, currentList);
        setupAdapter();


        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        fillList(true, currentList);
                        refreshAdapter();

                    }
                });
            }
        });
    }

    void setupAdapter(){
        adapter = new EarthquakeAdapter(currentList, this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private List<EarthquakeInfo> fillList(boolean refreshAdapter, List<EarthquakeInfo> result){

        //List<EarthquakeInfo> result = new ArrayList<EarthquakeInfo>();
        result.clear();
        try {
            currentData = getData();
            JSONArray features = new JSONArray();
            if(currentData != null) {
                features = currentData.getJSONArray("features");
                Log.d("Current list length", String.valueOf(features.length()));
            }
            if(features.length() > 0){
                for(int i = 0; i < features.length(); i++) {
                    EarthquakeInfo info = new EarthquakeInfo();
                    JSONObject tempJson, tempProperties, tempCoordinates;
                    JSONArray coordinatesArray;
                    tempJson = (JSONObject)features.get(i);
                    tempProperties = (JSONObject)tempJson.getJSONObject("properties");
                    tempCoordinates = (JSONObject) tempJson.getJSONObject("geometry");
                    coordinatesArray = (JSONArray)tempCoordinates.getJSONArray("coordinates");
                    info.magnitude = tempProperties.getDouble("mag");
                    info.date = new Date(tempProperties.getLong("time"));
                    info.place = tempProperties.getString("place");
                    info.coordinates = new double[3];
                    for(int n = 0; n < coordinatesArray.length(); n++){
                        info.coordinates[n] = coordinatesArray.getDouble(n);
                    }
                    result.add(info);
                }
                Log.d("ITS WORKING!!", features.get(0).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(adapter != null && refreshAdapter){
            refreshAdapter();
        }
        return result;
    }

    public void refreshAdapter(){
        this.adapter.notifyDataSetChanged();
    }


    public JSONObject getData(){
        RetrieveData data =  new RetrieveData();
        try {
            return data.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private class RetrieveData extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");

            StringBuilder builder = new StringBuilder();
            HttpResponse response;
            StatusLine status;
            HttpEntity entity;
            InputStream content;
            BufferedReader reader;
            JSONObject jsonObject = null;

            try {
                response = client.execute(request);
                status = response.getStatusLine();
                if(status.getStatusCode() == 200){
                    entity = response.getEntity();
                    content = entity.getContent();
                    reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                    Log.d("Response of GET request", builder.toString());

                    try{
                        jsonObject = new JSONObject(builder.toString());
                        currentData = jsonObject;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Error", "Failed to download file.");
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return jsonObject;
        }

    }


}
