package com.example.atleta;

public class ChatList {

    private String uId,name,message,date,time;

    public ChatList(String uId, String name, String message, String date, String time) {
        this.uId = uId;
        this.name = name;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getuId() {
        return uId;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
