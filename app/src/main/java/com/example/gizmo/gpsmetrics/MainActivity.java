package com.example.gizmo.gpsmetrics;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLong, p1distance, p2distance, p1lat, p1long, p2lat, p2long;
    String provider;
    class Point {
        double lat, lng;

        public Point(double latitude, double longtitude) {
            this.lat = latitude;
            this.lng = longtitude;
        }
    }
    LocationManager locationManager;
    Location location;
    Point p1 = new Point(52.246557, 104.269823);
    Point p2 = new Point(52.245391, 104.272060);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLat = (TextView) findViewById(R.id.lat);
        tvLong = (TextView) findViewById(R.id.lng);
        p1distance = (TextView) findViewById(R.id.p1distance);
        p2distance = (TextView) findViewById(R.id.p2distance);
        p1lat  = (TextView) findViewById(R.id.p1lat);
        p1long  = (TextView) findViewById(R.id.p1long);
        p2lat  = (TextView) findViewById(R.id.p2lat);
        p2long  = (TextView) findViewById(R.id.p2long);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("my", locationManager.getAllProviders().toString());

        provider = locationManager.getBestProvider(new Criteria(), false);
        Log.d("my", locationManager.getAllProviders().toString());
        Log.d("my", provider);
        try {
            location = locationManager.getLastKnownLocation(provider); }
        catch (SecurityException e) {};

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                double lat =  (location.getLatitude());
                double lng =  (location.getLongitude());
                Log.d("my", "lat " + lat + " long " + lng);
                tvLat.setText("Lat: " + String.format("%.7f",lat));
                tvLong.setText("Long: " + String.format("%.7f",lng));

                float[] result = new float[2];
                Location.distanceBetween(p1.lat, p1.lng, lat, lng, result);
                double p1dst = result[0];
                Log.d("my", "distance to p1 " + p1dst);
                p1distance.setText(String.format("%.1f", p1dst));

                Location.distanceBetween(p2.lat, p2.lng, lat, lng, result);
                double p2dst = result[0];
                Log.d("my", "distance to p2 " + p2dst);
                p2distance.setText(String.format("%.1f", p2dst));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); }
        catch (SecurityException e) {};
    }

    public void onClick(View v)
    {
        p1 = new Point( Double.parseDouble(p1lat.getText().toString()), Double.parseDouble(p1long.getText().toString()));
        p2 = new Point( Double.parseDouble(p2lat.getText().toString()), Double.parseDouble(p2long.getText().toString()));




    }

}
