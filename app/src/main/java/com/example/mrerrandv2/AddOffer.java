package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class AddOffer implements Serializable {

    @Exclude
    private String key;


    String ridername, offer;
    public AddOffer(){}

    public AddOffer(String ridername, String offer) {
        this.ridername = ridername;
        this.offer = offer;
    }

    public String getRidername() {
        return ridername;
    }

    public void setRidername(String ridername) {
        this.ridername = ridername;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}