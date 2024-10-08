package com.example.sharebiteapp.ModelData;

import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.Utils;

import java.util.HashMap;
import java.util.Map;

public class Location {
    public String locationId;
    public String donationId;
    public String address;
    public double latitude;
    public double longitude;

    public String createdon;
    public String updatedon;
    public Location() {
    }

    public Location(String donationId, String address, double latitude, double longitude) {
        this.donationId = donationId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;

        this.createdon = Utils.getCurrentDatetime();
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setUpdatedon(String updatedon) {
        this.updatedon = updatedon;
    }
    public Map<String, Object> toMapUpdate() {
        Map<String, Object> result = new HashMap<>();
        result.put("address", address);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("updatedon", updatedon);

        return result;
    }
}
