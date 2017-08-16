package com.example.gizmo.gpsmetrics;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLong, p1distance, p2distance, p3distance, p1lat, p1long, p2lat, p2long , p3lat, p3long;
    String provider;
    final double TREASURE_MIN_DISTANCE = 3;

    LocationManager locationManager;
    Location location;
    // боковая калитка 52.251264, 104.260736
    // мусорка 52.250956, 104.259296
    Point b1; // = new Point(52.251264, 104.260736);
    Point b2, b3;// = new Point(52.250956, 104.259296);
    ArrayList<Treasure> treasures;

    // Point b1 = new Point(52.246557, 104.269823);
    // Point b2 = new Point(52.245391, 104.272060);
    void setCoordTextView(TextView tv, double coord) {
        if (tv != null) {
            tv.setText(Double.toString(coord));
        }
    }
    void setPointTextView(TextView tvlat, TextView tvlng, Point p) {
        if (tvlat != null) {
            tvlat.setText(Double.toString(p.lat));
        }
        if (tvlng != null) {
            tvlng.setText(Double.toString(p.lng));
        }

    }
    void setDistanceTextView(TextView tvdst, double lat, double lng, Point beacon) {
        float[] result = new float[2];
        Location.distanceBetween(beacon.lat, beacon.lng, lat, lng, result);
        double p1dst = result[0];
        Log.d("my", "distance to b1 " + p1dst);
        tvdst.setText(String.format("%.1f", p1dst));
    }
    boolean ifTreasureisNear(Treasure t, double lat, double lng) {
        if (t != null) {
            float[] result = new float[2];
            Location.distanceBetween(t.lat, t.lng, lat, lng, result);
            if (result[0] < TREASURE_MIN_DISTANCE)
                return true;
            else return false;
        }
        else return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLat = (TextView) findViewById(R.id.lat);
        tvLong = (TextView) findViewById(R.id.lng);
        p1distance = (TextView) findViewById(R.id.p1distance);
        p2distance = (TextView) findViewById(R.id.p2distance);
        p3distance = (TextView) findViewById(R.id.p3distance);

        p1lat  = (TextView) findViewById(R.id.p1lat);
        p1long  = (TextView) findViewById(R.id.p1long);
        p2lat  = (TextView) findViewById(R.id.p2lat);
        p2long  = (TextView) findViewById(R.id.p2long);
        p3lat  = (TextView) findViewById(R.id.p3lat);
        p3long  = (TextView) findViewById(R.id.p3long);

        String file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"test.csv").getAbsolutePath();
        Log.d("my", file);
        Configuration c = new Configuration(file);
        treasures = c.treasures;
        b1 = c.beacons.get(0); setPointTextView(p1lat, p1long, b1);
        b2 = c.beacons.get(1); setPointTextView(p2lat, p2long, b2);
        b3 = c.beacons.get(2); setPointTextView(p3lat, p3long, b3);

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

                setDistanceTextView(p1distance, lat, lng, b1);
                setDistanceTextView(p2distance, lat, lng, b2);
                setDistanceTextView(p3distance, lat, lng, b3);
                for (Treasure tr: treasures) {
                    if (ifTreasureisNear(tr, lat, lng)) {
                        Toast.makeText(getApplicationContext(), "Подсказка:\n " + tr.hint, Toast.LENGTH_LONG).show();
                    }
                }
                /*
                float[] result = new float[2];
                Location.distanceBetween(b1.lat, b1.lng, lat, lng, result);
                double p1dst = result[0];
                Log.d("my", "distance to b1 " + p1dst);
                p1distance.setText(String.format("%.1f", p1dst));

                Location.distanceBetween(b2.lat, b2.lng, lat, lng, result);
                double p2dst = result[0];
                Log.d("my", "distance to b2 " + p2dst);
                p2distance.setText(String.format("%.1f", p2dst));
                */
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
        b1 = new Point( Double.parseDouble(p1lat.getText().toString()), Double.parseDouble(p1long.getText().toString()));
        b2 = new Point( Double.parseDouble(p2lat.getText().toString()), Double.parseDouble(p2long.getText().toString()));




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
