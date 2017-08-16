package com.example.gizmo.gpsmetrics;

/**
 * Created by student on 16.08.17.
 */

public class Treasure extends Point {
    String name, hint;

    public Treasure(String name, double latitude, double longtitude, String hint) {
        super(latitude, longtitude);
        this.name = name;
        this.hint = hint;
    }
}
