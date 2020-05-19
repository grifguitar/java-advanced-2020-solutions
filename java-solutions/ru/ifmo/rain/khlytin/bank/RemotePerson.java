package ru.ifmo.rain.khlytin.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemotePerson extends UnicastRemoteObject implements Person {
    private String name;
    private String surname;
    private String passportID;

    RemotePerson(String name, String surname, String passportID, int port) throws RemoteException {
        super(port);
        this.name = name;
        this.surname = surname;
        this.passportID = passportID;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassportID() {
        return passportID;
    }
}