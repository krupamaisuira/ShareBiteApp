package com.example.sharebiteapp.ModelData;

import com.example.sharebiteapp.Utility.FoodStatus;
import com.example.sharebiteapp.Utility.Utils;

import java.util.HashMap;
import java.util.Map;

public class DonateFood {
      public String donationId ;
      public String donatedBy ;
      public String title;
      public String description ;
      public String bestBefore;
      public double price;
      public Boolean fooddeleted ;
      public String createdon;
      public int status;
      public  Location location;

    public DonateFood() {
    }
    public DonateFood( String donatedBy, String title, String description, String bestBefore, double price,Location location) {


        this.donatedBy = donatedBy;
        this.title = title;
        this.description = description;
        this.bestBefore = bestBefore;
        this.price = price;
        this.fooddeleted = false;
        this.status = FoodStatus.Available.getIndex();
        this.createdon = Utils.getCurrentDatetime();
        this.location = location;
    }

    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getDonatedBy() {
        return donatedBy;
    }

    public void setDonatedBy(String donatedBy) {
        this.donatedBy = donatedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(String bestBefore) {
        this.bestBefore = bestBefore;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public String getFoodStatus()
    {
        return FoodStatus.getByIndex(status).toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("donationId", donationId);
        result.put("donatedBy", donatedBy);
        result.put("title", title);
        result.put("description", description);
        result.put("bestBefore", bestBefore);
        result.put("price", price);
        result.put("fooddeleted", fooddeleted);
        result.put("createdon", createdon);
        result.put("status", status);

        return result;
    }
}
