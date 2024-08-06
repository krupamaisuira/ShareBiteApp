package com.example.sharebiteapp.Utility.Interface;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.Location;

import java.util.List;


public interface IDonateFood {
    void donatefood(DonateFood food, OperationCallback callback);
    void addLocationForDonatedFood(String donationid, Location location, OperationCallback callback);
    void getAllDonatedFood(String userId,final ListOperationCallback<List<DonateFood>> callback);
    void deleteDonatedFood(String uid,OperationCallback callback);
    void deleteLocation(String donationid, OperationCallback callback);
    void getDonationDetail(String uid, ListOperationCallback<DonateFood> callback);
    void getAllRequestFoodList(String userId,final ListOperationCallback<List<DonateFood>> callback);
}
