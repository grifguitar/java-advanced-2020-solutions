package ru.ifmo.rain.khlytin.bank;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class LocalPerson extends AbstractPerson implements Serializable {
    private final Map<String, Account> accounts;

    LocalPerson(String name, String surname, String passportID, Map<String, Account> accounts) {
        super(name, surname, passportID);
        this.accounts = accounts;
    }

    public Account getAccountBySubID(String subID) {
        return accounts.get(subID);
    }

    public Set<String> getAllSubID() {
        return accounts.keySet();
    }
}