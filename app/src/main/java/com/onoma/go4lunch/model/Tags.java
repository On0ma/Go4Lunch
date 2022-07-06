package com.onoma.go4lunch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tags {

    @SerializedName("addr:housenumber")
    @Expose
    private String addrHousenumber;
    @SerializedName("addr:postcode")
    @Expose
    private String addrPostcode;
    @SerializedName("addr:street")
    @Expose
    private String addrStreet;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private String openingHours;
    @SerializedName("phone")
    @Expose
    private String phone;

    /**
     * No args constructor for use in serialization
     *
     */
    public Tags() {
    }

    /**
     *
     * @param addrPostcode
     * @param addrStreet
     * @param phone
     * @param name
     * @param openingHours
     * @param addrHousenumber
     */
    public Tags(String addrHousenumber, String addrPostcode, String addrStreet, String name, String openingHours, String phone) {
        super();
        this.addrHousenumber = addrHousenumber;
        this.addrPostcode = addrPostcode;
        this.addrStreet = addrStreet;
        this.name = name;
        this.openingHours = openingHours;
        this.phone = phone;
    }

    public String getAddrHousenumber() {
        return addrHousenumber;
    }

    public void setAddrHousenumber(String addrHousenumber) {
        this.addrHousenumber = addrHousenumber;
    }

    public String getAddrPostcode() {
        return addrPostcode;
    }

    public void setAddrPostcode(String addrPostcode) {
        this.addrPostcode = addrPostcode;
    }

    public String getAddrStreet() {
        return addrStreet;
    }

    public void setAddrStreet(String addrStreet) {
        this.addrStreet = addrStreet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
