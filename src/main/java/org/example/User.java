package org.example;

import java.util.ArrayList;

public class User {

    public User() {
    }

    //Methods
    public int getUID() {
        return userID;
    }

    public ArrayList<Double> getAccounts() {
        return accounts;
    }

    public void deposit(String currency, double amount) {

    }

    public void withdraw(String currency, double amount) {


    }

    public void transfer(int userID, String currency, double amount) {


    }

    public void exchange(String from, String to, double amount) {

    }

    //Fields
    private int userID;
    private ArrayList<Double> accounts;



}
