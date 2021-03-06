package com.aiam.clickaride;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aiam.clickaride.actions.ClickARideActions;
import com.aiam.clickaride.service.AppLocationService;
import com.aiam.clickaride.util.ClickARideType;
import com.aiam.clickaride.util.Constants;
import com.aiam.clickaride.util.LocationAddress;
import com.aiam.clickaride.util.Util;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks {
    public static String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    ClickARideActions action = new ClickARideActions();
    SupportMapFragment mapFragment;
    Button btnRide;
    Button btnAccept;
    Button btnCancel;
    Button btnComplete;
    Button btnLogin;

    SharedPreferences sharedpreferences;
    String username;
    String destination;
    double distance;
    public double price;

    MainActivity context;
    AppLocationService appLocationService;
    String origin;
    Location currentLocation;
    TextView lblStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (ClickARideType.isDriver()) {
            setContentView(R.layout.activity_main_driver);
        }
        else {
            setContentView(R.layout.activity_main);
        }

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRide = (Button) findViewById(R.id.btnRide);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnComplete = (Button) findViewById(R.id.btnComplete);

        if (btnCancel != null) btnCancel.setOnClickListener(this);
        if (btnLogin != null) btnLogin.setOnClickListener(this);
        if (btnRide != null) btnRide.setOnClickListener(this);
        if (btnAccept != null) btnAccept.setOnClickListener(this);
        if (btnComplete != null) btnComplete.setOnClickListener(this);

        lblStatus = (TextView) findViewById(R.id.lblStatus);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.locationTo);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();

                Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
//                } else {
//                    showSettingsAlert();
                }

                currentLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
                action.markOrigin(mMap, currentLocation);
                LatLng from = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                destination = place.getAddress().toString();
                LatLng dest = place.getLatLng();

                action.markDestination(mMap, dest);
                action.drawRoute(from, dest, mMap);

                float[] results = new float[1];
                Location.distanceBetween(from.latitude, from.longitude, dest.latitude, dest.longitude, results);
                distance = results[0];
                action.computePrice(context, origin, destination, distance, lblStatus);
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString(ClickARideType.USERNAME, null);
        if (username != null && !username.isEmpty()) {
            btnLogin.setVisibility(View.GONE);
        }
        else {
            if (btnRide != null) btnRide.setVisibility(View.GONE);
            if (btnCancel != null) btnCancel.setVisibility(View.GONE);
        }
        appLocationService = new AppLocationService(MainActivity.this);
        action.displayLastStatus(username, lblStatus);

        if (ClickARideType.isDriver()) {
            Util.runDriverPolling(this, lblStatus, username);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        currentLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        action.markOrigin(googleMap, currentLocation);
    }

    @Override
    public void onResume() {
        mapFragment.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        if (v == btnRide) {
            System.out.println("BTN RIDE");
            action.requestRide(username, origin, destination, distance, price, lblStatus);
        }
        else if (v == btnLogin){
            System.out.println("BTN LOGIN");
            action.login(this);
        }
        else if (v == btnAccept){
            System.out.println("BTN ACCEPT");
            String passenger = null;//this must comes from popup screen, poll only
            action.accept(passenger, username, lblStatus);
        }
        else if (v == btnComplete){
            System.out.println("BTN COMPLETE");
            action.complete(username, lblStatus);
        }
        else {
//            this should ask passenger why cancelled
            System.out.println("BTN CANCEL");
            action.cancel("Cancelled Ride", lblStatus);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    origin = bundle.getString("address");
                    break;
                default:
                    origin = null;
            }
//            tvAddress.setText(locationAddress);
        }
    }
}
