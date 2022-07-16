package com.example.mrerrandv2;

public class ReadWriteUserDetails {
    public String firstname,lastname ,email, mobilenum, type, address;

    //Constructor
    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textFirstName,String textLastName, String textEmail, String textNum, String textType) {
        this.firstname = textFirstName;
        this.lastname = textLastName;
        this.email = textEmail;
        this.mobilenum = textNum;
        this.type = textType;
    }
}
