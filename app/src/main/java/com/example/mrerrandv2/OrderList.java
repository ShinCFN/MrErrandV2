package com.example.mrerrandv2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class OrderList implements Serializable {

    @Exclude
    private String key;

    String item,qty,state, price;

    public OrderList() {

    }

    public OrderList(String item, String qty, String state) {
        this.item = item;
        this.qty = qty;
        this.state = state;
    }



    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
