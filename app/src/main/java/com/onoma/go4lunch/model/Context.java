package com.onoma.go4lunch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Context {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("text_fr")
    @Expose
    private String textFr;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("wikidata")
    @Expose
    private String wikidata;
    @SerializedName("language_fr")
    @Expose
    private String languageFr;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("short_code")
    @Expose
    private String shortCode;

    /**
     * No args constructor for use in serialization
     *
     */
    public Context() {
    }

    /**
     *
     * @param textFr
     * @param language
     * @param id
     * @param text
     * @param wikidata
     * @param shortCode
     * @param languageFr
     */
    public Context(String id, String textFr, String text, String wikidata, String languageFr, String language, String shortCode) {
        super();
        this.id = id;
        this.textFr = textFr;
        this.text = text;
        this.wikidata = wikidata;
        this.languageFr = languageFr;
        this.language = language;
        this.shortCode = shortCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextFr() {
        return textFr;
    }

    public void setTextFr(String textFr) {
        this.textFr = textFr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWikidata() {
        return wikidata;
    }

    public void setWikidata(String wikidata) {
        this.wikidata = wikidata;
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

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

}
