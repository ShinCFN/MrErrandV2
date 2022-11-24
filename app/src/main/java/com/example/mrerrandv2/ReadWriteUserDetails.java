package com.example.mrerrandv2;

public class ReadWriteUserDetails {
    public String firstname,lastname ,email, mobile, type;
    public int totalstars, totalrates;

    //Constructor
    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textFirstName,String textLastName, String textEmail, String textNum, String textType, int totalstars, int totalrates) {
        this.firstname = textFirstName;
        this.lastname = textLastName;
        this.email = textEmail;
        this.mobile = textNum;
        this.type = textType;
        this.totalstars = totalstars;
        this.totalrates = totalrates;
    }
}
