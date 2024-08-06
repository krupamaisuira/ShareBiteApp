package com.example.sharebiteapp.Utility.Interface;

import com.example.sharebiteapp.Interface.OperationCallback;
import com.example.sharebiteapp.ModelData.DonateFood;
import com.example.sharebiteapp.ModelData.RequestFood;

public interface IRequestFood {
    void requestfood(RequestFood model, OperationCallback callback);
}
