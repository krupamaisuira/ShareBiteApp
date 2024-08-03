package com.example.sharebiteapp.Interface;

public interface ListOperationCallback<T> {
    void onSuccess(T data);
    void onFailure(String error);
}
