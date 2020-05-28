package ru.ifmo.rain.khlytin.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Bank extends Remote {
    boolean createPerson(String name, String surname, String passportID) throws RemoteException;

    Person getPerson(String passportID, String mode) throws RemoteException;

    Set<String> getAllSubID(Person person) throws RemoteException;

    boolean createAccount(String ID) throws RemoteException;

    Account getAccount(Person person, String subID) throws RemoteException;
}
