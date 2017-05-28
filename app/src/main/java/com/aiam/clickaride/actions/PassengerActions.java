package com.aiam.clickaride.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.aiam.clickaride.LoginActivity;
import com.aiam.clickaride.MainActivity;
import com.aiam.clickaride.dto.Passenger;
import com.aiam.clickaride.dto.RequestRiderDTO;
import com.aiam.clickaride.util.Constants;
import com.aiam.clickaride.util.DrawDirectionTask;
import com.aiam.clickaride.util.Util;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by aiam on 5/12/2017.
 */

public class PassengerActions {
    public LatLng markOrigin(GoogleMap googleMap, Context context, GoogleApiClient mGoogleApiClient) {
        LatLng origin = Util.getCurrentLocation(context, mGoogleApiClient);
        googleMap.addMarker(new MarkerOptions().position(origin).title("Me"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
        return origin;
    }

    public void markDestination(GoogleMap googleMap, LatLng dest) {
//        note: the current value must be coming from the search field
        MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(options.position(dest).title("To"));
    }

    public void drawRoute(LatLng origin, LatLng dest, GoogleMap googleMap) {
        // Start downloading json data abd draw routes from Google Directions API
        DrawDirectionTask downloadTask = new DrawDirectionTask(origin, dest, googleMap);
        downloadTask.draw();
    }

    public void alert(String message) {

    }

    public void register(final SharedPreferences sharedpreferences, final String email, final String password) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Passenger pass = new Passenger();
                    pass.setUserName(email);
                    pass.setPassword(password);

                    final String url = "http://10.0.2.2:8080/registerPassenger";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity message = restTemplate.postForEntity(url, pass, Passenger.class);
                    Passenger p = (Passenger) message.getBody();
                    System.out.println(p.getId());
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Constants.USERNAME, email);
                    editor.commit();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void requestRide(final String username, final String origin, final String destination) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestRiderDTO request = new RequestRiderDTO();
                    request.setAcceptedBy(username);
                    request.setRequestLocationOrigin(origin);
                    request.setRequestLocationDestination(destination);

                    final String url = "http://10.0.2.2:8080/requestRide";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity message = restTemplate.postForEntity(url, request, RequestRiderDTO.class);
                    RequestRiderDTO p = (RequestRiderDTO) message.getBody();
                    System.out.println(p.getStatus());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void cancel(String message) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Passenger pass = new Passenger();
                    pass.setFirstName("pass1");
                    pass.setLastName("pass1");
                    pass.setUserName("pass1");
                    pass.setPassword("pass1");

                    final String url = "http://10.0.2.2:8080/cancelRequestRider";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    String message = restTemplate.postForObject(url, pass, String.class);
                    System.out.println(message);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(Activity act) {
        Intent myIntent = new Intent(act, LoginActivity.class);
        act.startActivity(myIntent);
    }

}
