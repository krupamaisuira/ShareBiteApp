package com.example.sharebiteapp.ModelData;

public class Report {
    private   int collections;
    private   int donations;

    public Report(int collections, int donations) {

        this.collections = collections;
        this.donations = donations;
    }

    public int getDonations() {
        return donations;
    }

    public void setDonations(int donations) {
        this.donations = donations;
    }

    public int getCollections() {
        return collections;
    }

    public void setCollections(int collections) {
        this.collections = collections;
    }
}
