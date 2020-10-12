package com.example.profileapp.models;

import java.io.Serializable;

public class StoreItem implements Serializable {
    public String name, region, photo;
    public double discount, price;
    public int quantity;

    @Override
    public String toString() {
        return "StoreItem{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", photo='" + photo + '\'' +
                ", discount=" + discount +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
