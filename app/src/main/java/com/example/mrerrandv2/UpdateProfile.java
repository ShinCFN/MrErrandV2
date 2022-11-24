package com.example.mrerrandv2;

public class UpdateProfile {

    String firstname;
    String lastname;
    String mobile;
    String street;
    String city;
    String province;
    String zip;

    public UpdateProfile(){}

    public UpdateProfile(String firstname, String lastname, String mobile, String street, String city, String province, String zip) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobile = mobile;
        this.street = street;
        this.city = city;
        this.province = province;
        this.zip = zip;
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
