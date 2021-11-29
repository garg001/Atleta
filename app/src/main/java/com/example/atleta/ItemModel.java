package com.example.atleta;

public class ItemModel {
    private String name, age, location,imgURL,uID;

    public ItemModel() {
    }

    public ItemModel(String imgURL, String name, String age, String location,String uID) {
        this.imgURL = imgURL;
        this.name = name;
        this.age = age;
        this.location = location;
        this.uID=uID;
    }

    public String getuID() {
        return uID;
    }

    public String getImgURL() {
        return imgURL;
    }
    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getLocation() {
        return location;
    }
}
