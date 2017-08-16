package com.example.gizmo.gpsmetrics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
    String teamName;
    int beaconCount, treasureCount;
    ArrayList<Beacon> beacons;
    ArrayList<Treasure> treasures;

    InputStream inputStream;

    public Configuration(String teamName, int beaconCount, int treasureCount, ArrayList<Beacon> beacons, ArrayList<Treasure> treasures) {
        this.teamName = teamName;
        this.beaconCount = beaconCount;
        this.treasureCount = treasureCount;
        this.beacons = beacons;
        this.treasures = treasures;
    }

    public Configuration(String fileName) {
        try {
            inputStream = new FileInputStream(fileName);
        } catch (IOException e) { };
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String csvLine;
        beacons = new ArrayList<>();
        treasures = new ArrayList<>();
        try {
            csvLine = reader.readLine();
            // reading Team name, number of beacons, number of treasures
            String[] row = csvLine.split(",");
            teamName = row[0];
            beaconCount = Integer.parseInt(row[1]);
            treasureCount = Integer.parseInt(row[2]);
            for (int i = 0; i < beaconCount; i++) {
                csvLine = reader.readLine();
                // reading beacon color, latitude and longtitude
                row = csvLine.split(",");
                beacons.add(new Beacon(row[0], Double.parseDouble(row[1]), Double.parseDouble(row[2])));
            }
            for (int i = 0; i < treasureCount; i++) {
                csvLine = reader.readLine();
                // reading treasures name, hint, latitude and longtitude
                row = csvLine.split(",");
                treasures.add(new Treasure(row[0], Double.parseDouble(row[1]), Double.parseDouble(row[2]), row[3]));
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

    }

    @Override
    public String toString() {
        return "Configuration{" +
                "teamName='" + teamName + '\'' +
                ", beaconCount=" + beaconCount +
                ", treasureCount=" + treasureCount +
                ", beacons=" + beacons +
                ", treasures=" + treasures +
                '}';
    }
}
