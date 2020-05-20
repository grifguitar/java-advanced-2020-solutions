package ru.ifmo.rain.khlytin.bank;

public abstract class AbstractPerson implements Person {
    private final String name;
    private final String surname;
    private final String passportID;

    AbstractPerson(String name, String surname, String passportID) {
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