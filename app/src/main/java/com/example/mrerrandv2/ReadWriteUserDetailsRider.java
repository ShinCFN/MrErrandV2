package com.example.mrerrandv2;

public class ReadWriteUserDetailsRider {
    public String firstname,lastname ,email, mobile, type, verified;
    public int totalstars, totalrates;

    //Constructor
    public ReadWriteUserDetailsRider(){};

    public ReadWriteUserDetailsRider(String textFirstName,String textLastName, String textEmail, String textNum, String textType, int totalstars, int totalrates, String verified) {
        this.firstname = textFirstName;
        this.lastname = textLastName;
        this.email = textEmail;
        this.mobile = textNum;
        this.type = textType;
        this.totalstars = totalstars;
        this.totalrates = totalrates;
        this.verified = verified;
    }
}
