package com.onoma.go4lunch.model;

import java.util.Objects;

public class Restaurant {

    private String name;
    private String adress;
    private String type;
    private double longitude;
    private double latitude;

    public Restaurant(String name, String adress, String type, double longitude, double latitude) {
        this.name = name;
        this.adress = adress;
        this.type = type;
        this.longitude = longitude;
        this.latitude = latitude;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Double.compare(that.longitude, longitude) == 0 && Double.compare(that.latitude, latitude) == 0 && name.equals(that.name) && adress.equals(that.adress) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, adress, type, longitude, latitude);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", adress='" + adress + '\'' +
                ", type='" + type + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
