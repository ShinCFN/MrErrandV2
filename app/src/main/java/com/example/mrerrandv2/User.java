package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

public class User {

    String firstname,lastname, profileImage;

    @Exclude
    private String key;

    public User(){};

    public User(String firstname, String lastname, String profileImage) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileImage = profileImage;
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
}
