package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class SaveTransaction implements Serializable {

    @Exclude
    private String key;

    public String order;
    public String ordertype;
    public String date;
    public String simpleDate;
    public String timestamp;
    public String time;
    public String rideruid;
    public int rating;
    public String receiptimg;

    public SaveTransaction(){};

    public SaveTransaction(String order, String ordertype, String date, String simpleDate, String timestamp, String time, String rideruid, int rating, String receiptimg) {
        this.order = order;
        this.ordertype = ordertype;
        this.date = date;
        this.simpleDate = simpleDate;
        this.timestamp = timestamp;
        this.time = time;
        this.rideruid = rideruid;
        this.rating = rating;
        this.receiptimg = receiptimg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRideruid() {
        return rideruid;
    }

    public void setRideruid(String rideruid) {
        this.rideruid = rideruid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSimpleDate() {
        return simpleDate;
    }

    public void setSimpleDate(String simpleDate) {
        this.simpleDate = simpleDate;
    }

    public String getReceiptimg() {
        return receiptimg;
    }

    public void setReceiptimg(String receiptimg) {
        this.receiptimg = receiptimg;
    }
}
