package com.example.amrgamal.weartracker.models;

/**
 * Created by amrga on 02/02/2018.
 */

public class Chat_Model {

    String Name,lastMessage,lastMessagetime,key;

    public Chat_Model() {
    }


    public Chat_Model(String name, String lastMessage, String lastMessagetime, String key) {
        Name = name;
        this.lastMessage = lastMessage;
        this.lastMessagetime = lastMessagetime;
        this.key = key;
    }

    public String getName() {
        return Name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessagetime() {
        return lastMessagetime;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastMessagetime(String lastMessagetime) {
        this.lastMessagetime = lastMessagetime;
    }
}
