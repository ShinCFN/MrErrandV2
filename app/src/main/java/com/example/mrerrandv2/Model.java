package com.example.mrerrandv2;

public class Model {

    private String profileImage;
    public Model(){}

    public Model(String imageUri){
        this.profileImage = imageUri;
    }

    public String getImageUri() {
        return profileImage;
    }

    public void setImageUri(String imageUri) {
        this.profileImage = imageUri;
    }
}
