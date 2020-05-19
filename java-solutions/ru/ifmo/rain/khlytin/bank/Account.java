package ru.ifmo.rain.khlytin.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Account extends Remote {
    String getID() throws RemoteException;

    int getAmount() throws RemoteException;

    void setAmount(int amount) throws RemoteException;
}