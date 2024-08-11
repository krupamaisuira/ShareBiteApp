package com.example.sharebiteapp.ModelData;



public class Photos {
    private String photoId;
    private String donationId;
    private String imagePath;

    public Photos() {}

    public Photos(String donationId, String imagePath) {

        this.donationId = donationId;
        this.imagePath = imagePath;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

