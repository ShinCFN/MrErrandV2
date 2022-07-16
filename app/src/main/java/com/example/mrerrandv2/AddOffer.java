package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class AddOffer implements Serializable {

    @Exclude
    private String key;

    public AddOffer(String ridername, String offer, String isAccepted, String riderlastname, String rideremail, String mobilenum, String platenum) {
        this.ridername = ridername;
        this.offer = offer;
        this.isAccepted = isAccepted;
        this.riderlastname = riderlastname;
        this.rideremail = rideremail;
        this.mobilenum = mobilenum;
        this.platenum = platenum;
    }

    String ridername, offer, isAccepted,riderlastname, rideremail, mobilenum, platenum;
    public AddOffer(){}

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

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getRiderlastname() {
        return riderlastname;
    }

    public void setRiderlastname(String riderlastname) {
        this.riderlastname = riderlastname;
    }

    public String getRideremail() {
        return rideremail;
    }

    public void setRideremail(String rideremail) {
        this.rideremail = rideremail;
    }

    public String getMobilenum() {
        return mobilenum;
    }

    public void setMobilenum(String mobilenum) {
        this.mobilenum = mobilenum;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPlatenum() {
        return platenum;
    }

    public void setPlatenum(String platenum) {
        this.platenum = platenum;
    }
}