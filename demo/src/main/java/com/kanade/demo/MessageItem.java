package com.kanade.demo;

public class MessageItem {
    private int id;
    private int senderId;
    private int itemType;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int chatType) {
        this.itemType = chatType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
