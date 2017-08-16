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
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLong, p1distance, p2distance, p1lat, p1long, p2lat, p2long;
    String provider;

    LocationManager locationManager;
    Location location;
    // боковая калитка 52.251264, 104.260736
    // мусорка 52.250956, 104.259296
    Point p1 = new Point(52.251264, 104.260736);
    Point p2 = new Point(52.250956, 104.259296);

    // Point p1 = new Point(52.246557, 104.269823);
    // Point p2 = new Point(52.245391, 104.272060);
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
                sendGPSLocation(lat, lng);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        catch (SecurityException e) {};
    }

    public void onClick(View v)
    {
        p1 = new Point( Double.parseDouble(p1lat.getText().toString()), Double.parseDouble(p1long.getText().toString()));
        p2 = new Point( Double.parseDouble(p2lat.getText().toString()), Double.parseDouble(p2long.getText().toString()));




    }
    public void sendGPSLocation(final double lat, final double lng)   {

        class MyThread extends Thread {
            String output = "";
            // creating connection
            public void run() {
                try {
                    String set_server_url = "http://194.176.114.21:8020/";
                    Date date = new Date();
                    long timestamp = date.getTime() / 1000;
                    String data = String.format(Locale.US, "{\"timestamp\" : %d, \"lat\" : %f, \"lng\": %f }", timestamp, lat, lng);
                    Log.d("my", data);

                    URL url = null;
                    try {
                        url = new URL(set_server_url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true); // setting POST method

                    // creating stream for writing request
                    OutputStream out = urlConnection.getOutputStream();
                    out.write(data.getBytes());

                    // reading response
                    Scanner in = new Scanner(urlConnection.getInputStream());
                    while (in.hasNext()) {
                        output += in.nextLine();
                    }

                    urlConnection.disconnect();
                }
                catch(IOException e)
                {
                    Log.d("my", e.getMessage());
                    output = e.getMessage();
                }
            }
        }
        MyThread mt = new MyThread();
        mt.start();
        while (mt.isAlive()); // do nothing
        if (mt.output.equals("")) {
            mt.output = "No output received";
        }
        Toast.makeText(this, mt.output, Toast.LENGTH_SHORT).show();


    }

}
