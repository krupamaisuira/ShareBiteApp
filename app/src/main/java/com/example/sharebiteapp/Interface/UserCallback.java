package com.example.sharebiteapp.Interface;

import com.example.sharebiteapp.ModelData.User;

public interface UserCallback {
    void onSuccess(User user);
    void onFailure(String errMessage);
}
