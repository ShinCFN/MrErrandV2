package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Order implements Serializable {

    @Exclude
    private String key;

    String firstname;
    String orderlist;
    String isAccepted;
    String lastname;
    String mobilenum;
    String useremail;
    String address;


    public void setStatus(String status) {
        this.status = status;
    }

    String status;
    public Order(){};

    public Order(String firstname, String orderlist, String isAccepted, String lastname, String mobilenum, String useremail, String status, String address) {
        this.firstname = firstname;
        this.orderlist = orderlist;
        this.isAccepted = isAccepted;
        this.lastname = lastname;
        this.mobilenum = mobilenum;
        this.useremail = useremail;
        this.status = status;
        this.address = address;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getOrderlist() {
        return orderlist;
    }

    public String getIsAccepted() {
        return isAccepted;
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

    public String getMobilenum() {
        return mobilenum;
    }

    public String getUseremail() {
        return useremail;
    }

    public String getAddress() {
        return address;
    }
}
