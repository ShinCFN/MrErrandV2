package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Rider implements Serializable {

    String firstname,lastname, profileImage, verified;
    int totalstars, totalrates;

    @Exclude
    private String key;

    public Rider(){};

    public Rider(String firstname, String lastname, String profileImage, int totalstars, int totalrates, String verified) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileImage = profileImage;
        this.totalstars = totalstars;
        this.totalrates = totalrates;
        this.verified = verified;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public int getTotalstars() {
        return totalstars;
    }

    public int getTotalrates() {
        return totalrates;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
