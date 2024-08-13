package com.example.sharebiteapp.Utility.Interface;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.Location;

public interface ILocation {
    void addlocation(Location model, OperationCallback callback);
    void deleteLocationByDonationID(String donationId, OperationCallback callback);
    void getLocationByDonationId(String uid, ListOperationCallback callback);
    void updatelocation(Location model, OperationCallback callback);
}
