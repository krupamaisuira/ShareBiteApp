package com.example.sharebiteapp.Utility.Interface;

import android.net.Uri;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.Photos;

import java.util.List;

public interface IPhoto {
    void addFoodPhotos(Photos model, OperationCallback callback);
    void updatePhotoOrder(String donationId, OperationCallback callback);
    void deleteImage(String donationId, String photoPath);
    void uploadImages(String donationId, Uri[] imageUris,OperationCallback callback);
    void getAllPhotosByDonationId(String uid, ListOperationCallback<List<Uri>> callback);
}
