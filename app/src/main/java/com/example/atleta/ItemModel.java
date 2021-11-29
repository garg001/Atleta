package com.example.atleta;

public class ItemModel {
    private String name, age, location,imgURL;

    public ItemModel() {
    }

    public ItemModel(String imgURL, String name, String age, String location) {
        this.imgURL = imgURL;
        this.name = name;
        this.age = age;
        this.location = location;
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
