package com.example.sharebiteapp.ModelData;

import com.example.sharebiteapp.Utility.Utils;

public class User {
    String userID,username,mobilenumber,email,password,createdon;
    Boolean profiledeleted,notification;



    public User(String userID,String username, String mobilenumber, String email, String password) {
        this.userID = userID;
        this.username = username;
        this.mobilenumber = mobilenumber;
        this.email = email;
        this.password = password;
        this.profiledeleted = false;
        this.notification =  true;
        this.createdon = Utils.getCurrentDatetime();

    }

    public String getCreatedon() {
        return createdon;
    }

    public Boolean getProfiledeleted() {
        return profiledeleted;
    }



    public Boolean getNotification() {
        return notification;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
