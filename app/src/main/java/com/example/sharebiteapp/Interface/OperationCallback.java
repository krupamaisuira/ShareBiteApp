package com.example.sharebiteapp.Interface;

import com.example.sharebiteapp.ModelData.User;

public interface OperationCallback {
    void onSuccess();
    void onFailure(String errMessage);
}
