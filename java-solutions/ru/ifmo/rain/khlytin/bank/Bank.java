package ru.ifmo.rain.khlytin.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Bank extends Remote {
    boolean createAccount(String subID, Person person) throws RemoteException;

    Account getAccount(String subID, Person person) throws RemoteException;

    Set<String> getAllSubID(Person person) throws RemoteException;

    Person getLocalPerson(String passportID) throws RemoteException;

    Person getRemotePerson(String passportID) throws RemoteException;

    boolean createPerson(String name, String surname, String passportID) throws RemoteException;

    boolean isPerson(String name, String surname, String passportID) throws RemoteException;
}
