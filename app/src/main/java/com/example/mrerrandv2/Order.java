package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Order implements Serializable {

    @Exclude
    private String key;

    String firstname;
    String orderlist;
    String lastname;
    String UID;
    String profilePic;
    String ordertype;
    int rating;


    public void setStatus(String status) {
        this.status = status;
    }

    String status;
    public Order(){};

    public Order(String firstname, String orderlist, String isAccepted, String lastname,String profilePic, String status, String UID, String ordertype, int rating) {
        this.firstname = firstname;
        this.orderlist = orderlist;
        this.lastname = lastname;
        this.profilePic = profilePic;
        this.status = status;
        this.UID = UID;
        this.ordertype = ordertype;
        this.rating = rating;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getOrderlist() {
        return orderlist;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public String getLastname() {
        return lastname;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public int getRating() {
        return rating;
    }
}
