package com.example.sharebiteapp.ModelData;



    public class Photos {
        private String photoId;
        private String donationId;
        private String imagePath;
        private int order;
        public Photos() {}

        public Photos(String donationId, String imagePath,int order) {

            this.donationId = donationId;
            this.imagePath = imagePath;
            this.order = order;
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

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }

