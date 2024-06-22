package com.example.sharebiteapp.Utility;

import com.google.firebase.database.DataSnapshot;

public interface UserExistenceCallback {
    void onResult(boolean exists, DataSnapshot snapshot);
}
