package com.example.sharebiteapp.Utility.Interface;

import com.example.sharebiteapp.Interface.ListOperationCallback;
import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.RequestFood;

import java.util.List;
import java.util.Map;

public interface IRequestFood {
    void requestfood(RequestFood model, OperationCallback callback);
    void isRequestFoodExist(RequestFood model,final ListOperationCallback<RequestFood> callback);
    void requestFoodCancel(String uid,String cancelby,OperationCallback callback);
    void fetchDonationRequests(String userId, ListOperationCallback<List<String>> callback);
}
