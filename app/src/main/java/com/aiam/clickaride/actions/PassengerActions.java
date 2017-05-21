package com.aiam.clickaride.actions;

import com.aiam.clickaride.dto.Driver;
import com.aiam.clickaride.dto.Passenger;
import com.aiam.clickaride.util.DrawDirectionTask;
import com.aiam.clickaride.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by aiam on 5/12/2017.
 */

public class PassengerActions implements IActions {
    @Override
    public LatLng markOrigin(GoogleMap googleMap) {
//        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        LatLng current = Util.getCurrentLocation(googleMap);
        googleMap.addMarker(new MarkerOptions().position(current).title("Me"));
        return current;
    }

    @Override
    public LatLng markDestination(GoogleMap googleMap) {
//        note: the current value must be coming from the search field
        LatLng current = Util.getToLocation(googleMap);
        MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(options.position(current).title("To"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 10));
        return current;
    }

    @Override
    public void drawRoute(LatLng origin, LatLng dest, GoogleMap googleMap) {
        // Start downloading json data abd draw routes from Google Directions API
        DrawDirectionTask downloadTask = new DrawDirectionTask(origin, dest, googleMap);
        downloadTask.draw();
    }

    @Override
    public void alert(String message) {

    }

    @Override
    public void accept() {

    }

    @Override
    public void acceptedBy(Driver driver) {

    }

    @Override
    public void acceptedBy(Passenger driver) {

    }

    @Override
    public void cancel(String message) {

    }

}
