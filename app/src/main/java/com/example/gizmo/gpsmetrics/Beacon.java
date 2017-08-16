package com.example.gizmo.gpsmetrics;

import android.graphics.Color;

public class Beacon extends Point {
    int color;
    String name;

    public Beacon(String color, double latitude, double longtitude) {
        super(latitude, longtitude);
        this.color = Color.parseColor(color);
    }
}
