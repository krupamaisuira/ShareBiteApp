package com.example.sharebiteapp.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences("UserManager", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void loginUser(String userID,String username, String email,Boolean notification) {
        editor.putString("userID", userID);
        editor.putString("userName", username);
        editor.putString("email", email);
        editor.putBoolean("notification", notification);
        editor.putBoolean("UserLoggedIn", true);
        editor.apply();
    }
    public String getUserID() {
        return sharedPreferences.getString("userID", "");
    }
    public String getUsername() {
        return sharedPreferences.getString("userName", "");
    }

    public String getEmail() {
        return sharedPreferences.getString("email", "");
    }
    public boolean getNotificationStatus() {
        return sharedPreferences.getBoolean("notification", false);
    }
    public boolean userLoggedIn() {
        return sharedPreferences.getBoolean("UserLoggedIn", false);
    }
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}

