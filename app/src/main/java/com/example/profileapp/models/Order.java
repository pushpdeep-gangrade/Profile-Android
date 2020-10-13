package com.example.profileapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    public String orderId;
    public String orderDate;
    public List<StoreItem> items;
}
