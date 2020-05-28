package ru.ifmo.rain.khlytin.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class RemoteBank implements Bank {
    private final ConcurrentMap<String, Account> bankAccounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Person> persons = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> personAccounts = new ConcurrentHashMap<>();
    private final int port;

    RemoteBank(int port) {
        this.port = port;
    }

    public boolean createPerson(String name, String surname, String passportID) throws RemoteException {
        if (name == null || surname == null || persons.get(passportID) != null) {
            return false;
        }
        RemotePerson person = new RemotePerson(name, surname, passportID);
        UnicastRemoteObject.exportObject(person, port);
        persons.put(passportID, person);
        personAccounts.put(passportID, new ConcurrentSkipListSet<>());
        return true;
    }

    public Person getPerson(String passportID, String mode) throws RemoteException {
        Person person;
        if (passportID == null || (person = persons.get(passportID)) == null) {
            return null;
        }
        if (mode.equals("Remote")) {
            return person;
        }
        Map<String, Account> personAccounts = new ConcurrentHashMap<>();
        for (String subID : getAllSubID(person)) {
            try {
                Account account = getAccount(person, subID);
                personAccounts.put(subID, new RemoteAccount(account.getID(), account.getAmount()));
            } catch (RemoteException e) {
                System.err.println("Fail to create local account: " + e.getMessage());
            }
        }
        return new LocalPerson(person.getName(), person.getSurname(), person.getPassportID(), personAccounts);
    }

    public Set<String> getAllSubID(Person person) throws RemoteException {
        if (person == null) {
            return null;
        }
        if (person instanceof LocalPerson) {
            return ((LocalPerson) person).getAllSubID();
        }
        return personAccounts.get(person.getPassportID());
    }

    public boolean createAccount(String ID) throws RemoteException {
        String[] tokens = ID.split(":");
        String passportID = tokens[0];
        String subID = tokens[1];
        if (subID == null || bankAccounts.containsKey(ID)) {
            return false;
        }
        Account account = new RemoteAccount(subID);
        UnicastRemoteObject.exportObject(account, port);
        bankAccounts.put(ID, account);
        if (personAccounts.get(passportID) == null) {
            personAccounts.put(passportID, new ConcurrentSkipListSet<>());
        }
        personAccounts.get(passportID).add(subID);
        return true;
    }

    public Account getAccount(Person person, String subID) throws RemoteException {
        if (person == null || subID == null) {
            return null;
        }
        if (person instanceof LocalPerson) {
            return ((LocalPerson) person).getAccountBySubID(subID);
        }
        String ID = person.getPassportID() + ":" + subID;
        return bankAccounts.get(ID);
    }
}