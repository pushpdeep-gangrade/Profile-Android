package com.example.profileapp.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    public String transactionId, type, status, cardType;
    public double amount;
    public String maskedNumber;
}
