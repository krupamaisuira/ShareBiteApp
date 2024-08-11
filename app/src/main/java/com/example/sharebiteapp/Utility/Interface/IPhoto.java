package com.example.sharebiteapp.Utility.Interface;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.Photos;

public interface IPhoto {
    void addFoodPhotos(Photos model, OperationCallback callback);
}
