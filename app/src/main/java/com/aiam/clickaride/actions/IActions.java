package com.aiam.clickaride.actions;

import android.graphics.Color;

import com.aiam.clickaride.dto.Driver;
import com.aiam.clickaride.dto.Passenger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aiam on 5/12/2017.
 */

public interface IActions {
//    Step1
    LatLng markOrigin(GoogleMap googleMap);
    void markDestination(GoogleMap googleMap, LatLng latLng);
    void drawRoute(LatLng from, LatLng to, GoogleMap googleMap);

//    Step2
    void alert(String message);

//    Step3
    void accept();
    void acceptedBy(Driver driver);
    void acceptedBy(Passenger driver);

//    Step4
    void cancel(String message);
}
