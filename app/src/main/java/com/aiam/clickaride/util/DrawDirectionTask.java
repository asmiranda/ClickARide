package com.aiam.clickaride.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aiam on 5/21/2017.
 */

public class DrawDirectionTask extends AsyncTask {
    GoogleMap googleMap;
    LatLng origin;
    LatLng dest;
    String url;
    PolylineOptions lineOptions = null;

    public DrawDirectionTask(LatLng origin, LatLng dest, GoogleMap googleMap) {
        super();
        this.googleMap = googleMap;
        this.origin = origin;
        this.dest = dest;
        url = Util.getDirectionsUrl(origin, dest);
    }

    @Override
    protected String doInBackground(Object... url) {
        String data = "";
        try {
            data = Util.downloadUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        drawData(data);
        return data;
    }

    private void drawData(String data) {
        JSONObject jObject;
        List<List<HashMap<String,String>>> routes = null;
        try {
            jObject = new JSONObject(data);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            routes = parser.parse(jObject);

            ArrayList points = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }

// Drawing polyline in the Google Map for the i-th route

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw() {
        this.execute(url);
    }

    @Override
    protected void onPostExecute(Object o) {
        googleMap.addPolyline(lineOptions);
    }
}
