package com.example.amrgamal.weartracker.models;

/**
 * Created by amrga on 04/02/2018.
 */

public class WearUser {
    String name,adress;
    double longitude,latitude;
    String  time;

    String key;

    public WearUser() {
    }

    public WearUser(String name, String  time, String adress, double longitude, double latitude) {
        this.name = name;
        this.time = time;
        this.adress = adress;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public WearUser(String name, String adress, String  time) {
        this.name = name;
        this.adress = adress;
        this.time = time;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getAdress() {
        return adress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String  time) {
        this.time = time;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
