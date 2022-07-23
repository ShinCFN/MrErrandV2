package com.example.mrerrandv2;

public class Rating {

    int totalstars;
    int totalrates;

    public Rating(){};

    public Rating(int totalstars, int totalrates) {
        this.totalstars = totalstars;
        this.totalrates = totalrates;
    }

    public int getTotalstars() {
        return totalstars;
    }

    public void setTotalstars(int totalstars) {
        this.totalstars = totalstars;
    }

    public int getTotalrates() {
        return totalrates;
    }

    public void setTotalrates(int totalrates) {
        this.totalrates = totalrates;
    }
}
