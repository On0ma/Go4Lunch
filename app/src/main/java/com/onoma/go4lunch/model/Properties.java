package com.onoma.go4lunch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("foursquare")
    @Expose
    private String foursquare;
    @SerializedName("landmark")
    @Expose
    private Boolean landmark;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("category")
    @Expose
    private String category;

    /**
     * No args constructor for use in serialization
     *
     */
    public Properties() {
    }

    /**
     *
     * @param address
     * @param foursquare
     * @param landmark
     * @param category
     */
    public Properties(String foursquare, Boolean landmark, String address, String category) {
        super();
        this.foursquare = foursquare;
        this.landmark = landmark;
        this.address = address;
        this.category = category;
    }

    public String getFoursquare() {
        return foursquare;
    }

    public void setFoursquare(String foursquare) {
        this.foursquare = foursquare;
    }

    public Boolean getLandmark() {
        return landmark;
    }

    public void setLandmark(Boolean landmark) {
        this.landmark = landmark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
