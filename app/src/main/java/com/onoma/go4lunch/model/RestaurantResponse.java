package com.onoma.go4lunch.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantResponse {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("query")
    @Expose
    private List<String> query = null;
    @SerializedName("features")
    @Expose
    private List<Feature> features = null;
    @SerializedName("attribution")
    @Expose
    private String attribution;

    /**
     * No args constructor for use in serialization
     *
     */
    public RestaurantResponse() {
    }

    /**
     *
     * @param features
     * @param query
     * @param attribution
     * @param type
     */
    public RestaurantResponse(String type, List<String> query, List<Feature> features, String attribution) {
        super();
        this.type = type;
        this.query = query;
        this.features = features;
        this.attribution = attribution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getQuery() {
        return query;
    }

    public void setQuery(List<String> query) {
        this.query = query;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

}