package com.example.profileapp.models;

import java.io.Serializable;

public class User implements Serializable {
    public String firstName, lastName, email, address;
    public int age;

    public User(){

    }

    User(String firstName, String lastName, String email, String address, int age){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.age = age;
    }
}
