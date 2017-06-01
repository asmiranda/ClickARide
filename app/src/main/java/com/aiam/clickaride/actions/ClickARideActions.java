package com.aiam.clickaride.actions;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.aiam.clickaride.LoginActivity;
import com.aiam.clickaride.MainActivity;
import com.aiam.clickaride.dto.Driver;
import com.aiam.clickaride.dto.LastStatusDTO;
import com.aiam.clickaride.dto.Passenger;
import com.aiam.clickaride.dto.RequestRiderDTO;
import com.aiam.clickaride.util.Constants;
import com.aiam.clickaride.util.DrawDirectionTask;
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

public class ClickARideActions {
    public void markOrigin(GoogleMap googleMap, Location currentLocation) {
        if (currentLocation != null) {
            LatLng origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(origin).title("Me"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
        }
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

    public void requestRide(final String username, final String origin, final String destination, final TextView lblStatus) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestRiderDTO request = new RequestRiderDTO();
                    request.setRequestor(username);
                    request.setRequestLocationOrigin(origin);
                    request.setRequestLocationDestination(destination);

                    final String url = "http://10.0.2.2:8080/requestRide";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity message = restTemplate.postForEntity(url, request, RequestRiderDTO.class);
                    RequestRiderDTO p = (RequestRiderDTO) message.getBody();
                    lblStatus.setText("Ride Requested. Please wait...");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void cancel(final String message, final TextView lblStatus) {
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
                    lblStatus.setText("Ride Cancelled");
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

    public void displayLastStatus(final String username, final TextView lblStatus) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LastStatusDTO request = new LastStatusDTO();
                    request.setUsername(username);

                    final String url = "http://10.0.2.2:8080/getPassengerLastStatus";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity message = restTemplate.postForEntity(url, request, LastStatusDTO.class);
                    LastStatusDTO p = (LastStatusDTO) message.getBody();
                    System.out.println(p.getStatus());
                    lblStatus.setText("Ride "+p.getStatus());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void complete(final String driver, final TextView lblStatus) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestRiderDTO request = new RequestRiderDTO();
                    request.setAcceptedBy(driver);

                    final String url = "http://10.0.2.2:8080/completeAcceptRide";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity message = restTemplate.postForEntity(url, request, RequestRiderDTO.class);
                    lblStatus.setText("Ride Completed");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void accept(final String passenger, final String driver, final TextView lblStatus) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestRiderDTO request = new RequestRiderDTO();
                    request.setRequestor(passenger);
                    request.setAcceptedBy(driver);

                    final String url = "http://10.0.2.2:8080/acceptRide";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity message = restTemplate.postForEntity(url, request, RequestRiderDTO.class);
                    RequestRiderDTO p = (RequestRiderDTO) message.getBody();
                    lblStatus.setText("Ride Accepted");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void alertForNewRequest(final Activity activity, final TextView lblStatus, final String driver) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Driver pass = new Driver();
                    pass.setUserName(driver);

                    final String url = "http://10.0.2.2:8080/checkNewRequest";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity message = restTemplate.postForEntity(url, pass, RequestRiderDTO.class);
                    final RequestRiderDTO p = (RequestRiderDTO) message.getBody();
                    if (p.getRequestor() != null) {
                        activity.runOnUiThread(new AcceptRunnable(activity, lblStatus, p));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class AcceptRunnable implements Runnable, DialogInterface.OnClickListener {
        Activity activity;
        RequestRiderDTO p;
        TextView lblStatus;

        AcceptRunnable(Activity activity, TextView lblStatus, RequestRiderDTO p) {
            this.activity = activity;
            this.lblStatus = lblStatus;
            this.p = p;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == 1) {
                System.out.println();
                accept(p.getRequestor(), p.getAcceptedBy(), lblStatus);
            }
        }

        @Override
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("New Ride Request");
            builder.setMessage("New Ride Request from "+p.getRequestLocationOrigin()+" to "+p.getRequestLocationDestination());
            builder.setPositiveButton("OK", this);
            builder.setNegativeButton("Cancel", this);
            builder.show();
        }
    }
}
