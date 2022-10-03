package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Chat implements Serializable {

    @Exclude
    private String key;

    public Chat(){};

    String name;
    String message;
    String timestamp;
    String time;
    String read;
    String uid;
    String type;
    String profImg;

    public Chat(String name, String message, String timestamp, String time, String read, String uid, String type, String profImg) {
        this.name = name;
        this.message = message;
        this.timestamp = timestamp;
        this.time = time;
        this.read = read;
        this.uid = uid;
        this.type = type;
        this.profImg = profImg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfImg() {
        return profImg;
    }

    public void setProfImg(String profImg) {
        this.profImg = profImg;
    }
}
