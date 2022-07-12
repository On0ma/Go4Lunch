package com.onoma.go4lunch.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("place_type")
    @Expose
    private List<String> placeType = null;
    @SerializedName("relevance")
    @Expose
    private Integer relevance;
    @SerializedName("properties")
    @Expose
    private Properties properties;
    @SerializedName("text_fr")
    @Expose
    private String textFr;
    @SerializedName("place_name_fr")
    @Expose
    private String placeNameFr;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("place_name")
    @Expose
    private String placeName;
    @SerializedName("center")
    @Expose
    private List<Double> center = null;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("context")
    @Expose
    private List<Context> context = null;
    @SerializedName("language_fr")
    @Expose
    private String languageFr;
    @SerializedName("language")
    @Expose
    private String language;

    /**
     * No args constructor for use in serialization
     *
     */
    public Feature() {
    }

    /**
     *
     * @param textFr
     * @param center
     * @param language
     * @param type
     * @param relevance
     * @param placeType
     * @param context
     * @param geometry
     * @param id
     * @param placeNameFr
     * @param text
     * @param placeName
     * @param properties
     * @param languageFr
     */
    public Feature(String id, String type, List<String> placeType, Integer relevance, Properties properties, String textFr, String placeNameFr, String text, String placeName, List<Double> center, Geometry geometry, List<Context> context, String languageFr, String language) {
        super();
        this.id = id;
        this.type = type;
        this.placeType = placeType;
        this.relevance = relevance;
        this.properties = properties;
        this.textFr = textFr;
        this.placeNameFr = placeNameFr;
        this.text = text;
        this.placeName = placeName;
        this.center = center;
        this.geometry = geometry;
        this.context = context;
        this.languageFr = languageFr;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPlaceType() {
        return placeType;
    }

    public void setPlaceType(List<String> placeType) {
        this.placeType = placeType;
    }

    public Integer getRelevance() {
        return relevance;
    }

    public void setRelevance(Integer relevance) {
        this.relevance = relevance;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getTextFr() {
        return textFr;
    }

    public void setTextFr(String textFr) {
        this.textFr = textFr;
    }

    public String getPlaceNameFr() {
        return placeNameFr;
    }

    public void setPlaceNameFr(String placeNameFr) {
        this.placeNameFr = placeNameFr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public List<Double> getCenter() {
        return center;
    }

    public void setCenter(List<Double> center) {
        this.center = center;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public List<Context> getContext() {
        return context;
    }

    public void setContext(List<Context> context) {
        this.context = context;
    }

    public String getLanguageFr() {
        return languageFr;
    }

    public void setLanguageFr(String languageFr) {
        this.languageFr = languageFr;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
