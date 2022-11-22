package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class User implements Serializable {

    String firstname,lastname, profileImage, coords;
    int totalstars, totalrates;

    @Exclude
    private String key;

    public User(){};

    public User(String firstname, String lastname, String profileImage, int totalstars, int totalrates, String coords) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileImage = profileImage;
        this.totalstars = totalstars;
        this.totalrates = totalrates;
        this.coords = coords;
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

    public int getTotalstars() {
        return totalstars;
    }

    public int getTotalrates() {
        return totalrates;
    }

    public String getKey() {
        return key;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }
}
