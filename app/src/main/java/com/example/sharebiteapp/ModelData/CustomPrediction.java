package com.example.sharebiteapp.ModelData;

public class CustomPrediction {
    private String placeId;
    private String primaryText;
    private String fullAddress;
    private double latitude;
    private double longitude;

    public CustomPrediction(String placeId, String primaryText) {
        this.placeId = placeId;
        this.primaryText = primaryText;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
