package com.onoma.go4lunch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("tags")
    @Expose
    private Tags tags;

    /**
     * No args constructor for use in serialization
     *
     */
    public RestaurantResponse() {
    }

    /**
     *
     * @param lon
     * @param id
     * @param lat
     * @param tags
     */
    public RestaurantResponse(Integer id, Double lat, Double lon, Tags tags) {
        super();
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.tags = tags;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

}
