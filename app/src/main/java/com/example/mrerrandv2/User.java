package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

public class User {

    String firstname,lastname, profileImage;
    int totalstars, totalrates;

    @Exclude
    private String key;

    public User(){};

    public User(String firstname, String lastname, String profileImage, int totalstars, int totalrates) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileImage = profileImage;
        this.totalstars = totalstars;
        this.totalrates = totalrates;
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
}
