package com.aiam.clickaride;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aiam.clickaride.actions.IActions;
import com.aiam.clickaride.actions.PassengerActions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    public static String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    IActions action = new PassengerActions();
    SupportMapFragment mapFragment;
    Button btnRide;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRide = (Button) findViewById(R.id.btnRide);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnRide.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.locationTo);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                LatLng from = action.markOrigin(mMap);
                LatLng to = place.getLatLng();

                action.markDestination(mMap, to);
                action.drawRoute(from, to, mMap);
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        action.markOrigin(googleMap);
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
            action.requestRide();
        }
        else {
//            this should ask passenger why cancelled
            System.out.println("BTN CANCEL");
            action.cancel("Cancelled Ride");
        }
    }
}
