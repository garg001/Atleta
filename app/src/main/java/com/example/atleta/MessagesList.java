package com.example.atleta;

public class MessagesList {
    private String name,imgURL,lastMessage, chatKey;
    private int unseenMessage;
    String uID;

    public MessagesList() {
        this.name = "";
        this.imgURL = "";
        this.lastMessage = "";
        this.chatKey = "";
        this.unseenMessage = 0;
        this.uID = "";
    }

    public MessagesList(String name, String imgURL, String lastMessage, String chatKey, int unseenMessage, String uID) {
        this.name = name;
        this.imgURL = imgURL;
        this.lastMessage = lastMessage;
        this.chatKey = chatKey;
        this.unseenMessage = unseenMessage;
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnseenMessage() {
        return unseenMessage;
    }

    public String getChatKey() {
        return chatKey;
    }

    public String getuID() {
        return uID;
    }
}
