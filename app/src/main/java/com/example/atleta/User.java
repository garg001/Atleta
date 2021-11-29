package com.example.atleta;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    private String userName,userEmail,age,favorite,experience,frequency,location,DpURL;
    @Exclude
    private String uID;

    public User() {
    }

    public User(String uID,String userName, String userEmail) {
        this.uID=uID;
        this.userName = userName;
        this.userEmail = userEmail;
        age="";
        favorite="";
        experience="";
        frequency="";
        location="";
        DpURL="";

    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getDpURL() {
        return DpURL;
    }

    public void setDpURL(String dpURL) {
        DpURL = dpURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
