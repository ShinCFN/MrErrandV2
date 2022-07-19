package com.example.mrerrandv2;

public class ReadWriteUserDetailsRider {
    public String firstname,lastname ,email, mobilenum, type, licensenum, platenumber;

    //Constructor
    public ReadWriteUserDetailsRider(){};

    public ReadWriteUserDetailsRider(String textFirstName,String textLastName, String textEmail, String textNum, String textType) {
        this.firstname = textFirstName;
        this.lastname = textLastName;
        this.email = textEmail;
        this.mobilenum = textNum;
        this.type = textType;
    }
}
