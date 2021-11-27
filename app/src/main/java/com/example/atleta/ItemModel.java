package com.example.atleta;

public class ItemModel {
    private int image;
    private String name, age, location;

    public ItemModel() {
    }

    public ItemModel(int image, String name, String age, String location) {
        this.image = image;
        this.name = name;
        this.age = age;
        this.location = location;
    }

    public int getImage() {
        return image;
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
