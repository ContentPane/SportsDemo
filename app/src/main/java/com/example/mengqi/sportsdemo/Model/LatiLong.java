package com.example.mengqi.sportsdemo.Model;

public class LatiLong {
    double latitude;
    double longitude;

    public LatiLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatiLong() {
        super();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }
}
