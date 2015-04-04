package com.deaquino.arturo.earthquakemonitor;

/**
 * Created by peanut on 3/29/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;



public class EarthquakeAdapter  extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder>{

    private List<EarthquakeInfo> earthquakeInfoList;
    private Context context;

    public EarthquakeAdapter(List<EarthquakeInfo> earthquakeInfoList, Context context) {
        this.earthquakeInfoList = earthquakeInfoList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return earthquakeInfoList.size();
    }

    @Override
    public EarthquakeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new EarthquakeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EarthquakeAdapter.EarthquakeViewHolder earthquakeViewHolder, int i) {
        final EarthquakeInfo eInfo = earthquakeInfoList.get(i);
        int magnitude = (int)eInfo.magnitude;
        earthquakeViewHolder.titleTextView.setText(eInfo.place.split(",")[1]);
        //earthquakeViewHolder.titleTextView.setBackgroundColor(eInfo.setColor(magnitude));
        earthquakeViewHolder.magnitudeTextView.setText(String.valueOf(eInfo.magnitude));
        earthquakeViewHolder.magnitudeTextView.setBackgroundColor(eInfo.setColor(magnitude));
        earthquakeViewHolder.dateTextView.setText(eInfo.date.toString());
        earthquakeViewHolder.placeTextView.setText(eInfo.place);
        //earthquakeViewHolder.imageButton.setBackgroundColor(eInfo.setColor(magnitude));
        earthquakeViewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject tempObject = new JSONObject();
                try {
                    tempObject.put("magnitude", eInfo.magnitude);
                    tempObject.put("date", eInfo.date);
                    tempObject.put("place", eInfo.place);
                    tempObject.put("longitude", eInfo.coordinates[0]);
                    tempObject.put("latitude", eInfo.coordinates[1]);
                    tempObject.put("depth", eInfo.coordinates[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(context.getApplicationContext(), Details.class);
                intent.putExtra("eInfo", tempObject.toString());
                context.startActivity(intent);
            }
        });
    }

    public static class EarthquakeViewHolder extends RecyclerView.ViewHolder {
        protected TextView magnitudeTextView;
        protected TextView dateTextView;
        protected TextView placeTextView;
        protected TextView titleTextView;
        protected  ImageButton imageButton;

        public EarthquakeViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById((R.id.title));
            magnitudeTextView =  (TextView) v.findViewById(R.id.txtMagnitude);
            dateTextView = (TextView)  v.findViewById(R.id.txtDate);
            placeTextView = (TextView)  v.findViewById(R.id.txtPlace);
            imageButton = (ImageButton) v.findViewById(R.id.imageButton);
        }
    }
}
