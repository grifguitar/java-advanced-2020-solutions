package ru.ifmo.rain.khlytin.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    private final static int PORT = 8888;
    private final static String URL = "//localhost:" + PORT + "/bank";

    public static void main(String[] args) {
        try {
            final Bank bank;
            try {
                bank = (Bank) Naming.lookup(URL);
            } catch (NotBoundException e) {
                System.err.println("Bank is not bound: " + e.getMessage());
                return;
            } catch (MalformedURLException e) {
                System.err.println("Bank URL is invalid: " + e.getMessage());
                return;
            }

            String name = args[0];
            String surname = args[1];
            String passportID = args[2];
            String subID = args[3];
            int amount = Integer.parseInt(args[4]);

            Person person = bank.getPerson(passportID, "Remote");
            if (person == null) {
                bank.createPerson(name, surname, passportID);
                person = bank.getPerson(passportID, "Remote");
            } else {
                if (!person.getName().equals(name) || !person.getSurname().equals(surname)) {
                    System.err.println("Incorrect personal data");
                    return;
                }
            }

            Account account = bank.getAccount(person, subID);
            if (!bank.getAllSubID(person).contains(subID)) {
                if (account == null) {
                    bank.createAccount(person.getPassportID() + ":" + subID);
                    account = bank.getAccount(person, subID);
                } else {
                    System.err.println("Account already exists");
                    return;
                }
            }

            System.out.println("Account id: " + account.getID());
            System.out.println("Balance: " + account.getAmount());
            System.out.println("Account balance is changing");
            account.setAmount(account.getAmount() + amount);
            System.out.println("Balance: " + account.getAmount());
        } catch (RemoteException e) {
            System.err.println("Remote exception: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal arguments: " + e.getMessage());
        }
    }
}