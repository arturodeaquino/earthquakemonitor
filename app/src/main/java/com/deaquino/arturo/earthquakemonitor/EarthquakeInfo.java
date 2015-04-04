package com.deaquino.arturo.earthquakemonitor;

import java.util.Date;

/**
 * Created by peanut on 3/29/2015.
 */
public class EarthquakeInfo {
    protected double magnitude;
    protected Date date;
    protected double coordinates[];
    protected String place;

    public static int setColor(int magnitude){
        int[] color = new int[3];
        color[0] = 36 * magnitude;
        color[1] = 255 - (36 * magnitude);
        color[2] = 0;
        return android.graphics.Color.rgb(color[0], color[1], color[2]);
    }
}
