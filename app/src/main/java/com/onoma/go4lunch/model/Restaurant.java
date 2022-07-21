package com.onoma.go4lunch.model;

import android.location.Location;

import java.util.Objects;

public class Restaurant {

    private int id;
    private String name;
    private String adress;
    private String type;
    private double longitude;
    private double latitude;

    public Restaurant(int id, String name, String adress, String type, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.adress = adress;
        this.type = type;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDistance(double posLong, double posLat) {
        float[] results = new float[3];
        Location.distanceBetween(this.getLatitude(), this.getLongitude(), posLat, posLong, results);
        return String.valueOf((int)results[0] + "m");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return id == that.id && Double.compare(that.longitude, longitude) == 0 && Double.compare(that.latitude, latitude) == 0 && name.equals(that.name) && adress.equals(that.adress) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, adress, type, longitude, latitude);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", adress='" + adress + '\'' +
                ", type='" + type + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
