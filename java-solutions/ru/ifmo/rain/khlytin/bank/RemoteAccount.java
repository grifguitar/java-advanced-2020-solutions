package ru.ifmo.rain.khlytin.bank;

public class RemoteAccount implements Account {
    private final String id;
    private int amount;

    public RemoteAccount(final String id) {
        this.id = id;
        amount = 0;
    }

    public RemoteAccount(final String id, final int amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getID() {
        return id;
    }

    public synchronized int getAmount() {
        return amount;
    }

    public synchronized void setAmount(final int amount) {
        this.amount = amount;
    }
}