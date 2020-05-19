package ru.ifmo.rain.khlytin.bank;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class LocalPerson implements Person, Serializable {
    private String name;
    private String surname;
    private String passportID;
    private Map<String, LocalAccount> accounts;

    LocalPerson(String name, String surname, String passportID, Map<String, LocalAccount> accounts) {
        this.name = name;
        this.surname = surname;
        this.passportID = passportID;
        this.accounts = accounts;
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

    public Account getAccountBySubID(String subID) {
        return accounts.get(subID);
    }

    Set<String> getAllSubID() {
        return accounts.keySet();
    }
}