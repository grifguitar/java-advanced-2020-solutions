package ru.ifmo.rain.khlytin.bank;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class RemoteBank implements Bank {
    private int port;
    private ConcurrentMap<String, Account> bankAccounts = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Person> persons = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Set<String>> personAccounts = new ConcurrentHashMap<>();

    RemoteBank(int port) {
        this.port = port;
    }

    public boolean createAccount(String subID, Person person) throws RemoteException {
        if (subID == null || person == null || bankAccounts.containsKey(person.getPassportID() + ":" + subID)) {
            return false;
        }
        String bankAccountID = person.getPassportID() + ":" + subID;
        bankAccounts.put(bankAccountID, new RemoteAccount(subID, port));

        if (personAccounts.get(person.getPassportID()) == null) {
            personAccounts.put(person.getPassportID(), new ConcurrentSkipListSet<>());
        }
        personAccounts.get(person.getPassportID()).add(subID);

        return true;
    }

    public Account getAccount(String subID, Person person) throws RemoteException {
        if (subID == null || person == null) {
            return null;
        }
        String bankAccountID = person.getPassportID() + ":" + subID;
        Account account = bankAccounts.get(bankAccountID);
        if (account == null) {
            return null;
        }

        if (person instanceof LocalPerson) {
            return ((LocalPerson) person).getAccountBySubID(subID);
        }
        return account;
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

    public Person getLocalPerson(String passportID) throws RemoteException {
        if (passportID == null) {
            return null;
        }
        Person person = persons.get(passportID);
        if (person == null) {
            return null;
        }

        Set<String> allSubID = getAllSubID(person);
        Map<String, LocalAccount> personAccounts = new ConcurrentHashMap<>();
        allSubID.forEach((subID) -> {
            try {
                Account account = getAccount(subID, person);
                personAccounts.put(subID, new LocalAccount(account.getID(), account.getAmount()));
            } catch (RemoteException e) {
                System.err.println("Unable to create local account: " + e.getMessage());
            }
        });

        return new LocalPerson(person.getName(), person.getSurname(), person.getPassportID(), personAccounts);
    }

    public Person getRemotePerson(String passportID) {
        if (passportID == null) {
            return null;
        }
        return persons.get(passportID);
    }

    public boolean createPerson(String name, String surname, String passportID) throws RemoteException {
        if (name == null || surname == null || persons.get(passportID) != null) {
            return false;
        }
        persons.put(passportID, new RemotePerson(name, surname, passportID, port));
        personAccounts.put(passportID, new ConcurrentSkipListSet<>());
        return true;
    }

    public boolean isPerson(String name, String surname, String passportID) throws RemoteException {
        if (name == null || surname == null || passportID == null) {
            return false;
        }
        Person person = persons.get(passportID);
        return person != null && person.getName().equals(name) && person.getSurname().equals(surname);
    }
}