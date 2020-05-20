package ru.ifmo.rain.khlytin.bank;

import java.io.Serializable;

public class LocalAccount implements Account, Serializable {
    //finals?
    private String id;
    private int amount;

    LocalAccount(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getID() {
        return id;
    }

    public synchronized int getAmount() {
        return amount;
    }

    public synchronized void setAmount(int amount) {
        this.amount = amount;
    }
}