package ru.ifmo.rain.khlytin.bank;

import org.junit.BeforeClass;
import org.junit.Assert;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Set;

public class Test {
    private static final int MAX_COUNT = 50;
    private static final int PORT = 8888;
    private static final String URL = "//localhost:" + PORT + "/bank";
    private static final String LOCAL = "Local";
    private static final String REMOTE = "Remote";
    private static Bank bank;

    @BeforeClass
    public static void beforeClass() throws Exception {
        bank = new RemoteBank(PORT);
        LocateRegistry.createRegistry(PORT);
        UnicastRemoteObject.exportObject(bank, PORT);
        Naming.rebind(URL, bank);
    }

    @org.junit.Test
    public void test1() throws RemoteException {
        String name = "test01User";
        String surname = "1";
        String passportID = "test01PassportID1";

        bank.createPerson(name, surname, passportID);
        Person localPerson = bank.getPerson(passportID, LOCAL);
        Person remotePerson = bank.getPerson(passportID, REMOTE);

        String subID = "2";
        String ID = remotePerson.getPassportID() + ":" + subID;

        bank.createAccount(ID);
        Assert.assertNull(bank.getAccount(localPerson, subID));
        Assert.assertEquals(1, bank.getAllSubID(remotePerson).size());
        Assert.assertNotNull(bank.getAccount(remotePerson, subID));
        Assert.assertNotEquals(bank.getAllSubID(localPerson), bank.getAllSubID(remotePerson));
    }

    @org.junit.Test
    public void test2() throws RemoteException {
        for (int i = 0; i < MAX_COUNT; i++) {
            String name = "test02User";
            String surname = Integer.toString(i);
            String passportID = "test02PassportID" + i;

            Assert.assertTrue(bank.createPerson(name, surname, passportID));
            Random random = new Random();
            Person remotePerson = bank.getPerson(passportID, REMOTE);
            int cnt = 0;
            for (int j = 0; j < random.nextInt(MAX_COUNT); j++) {
                if (bank.createAccount(remotePerson.getPassportID() + ":" + random.nextInt())) {
                    cnt++;
                }
            }
            Set<String> accounts = bank.getAllSubID(remotePerson);
            Assert.assertNotNull(accounts);
            Assert.assertEquals(cnt, accounts.size());
        }
    }

    @org.junit.Test
    public void test3() throws RemoteException {
        for (int i = 0; i < MAX_COUNT; i++) {
            String name = "test03User" + i;
            String surname = "";
            String passportID = "test03PassportID" + i;
            Person person;

            person = bank.getPerson(passportID, REMOTE);
            Assert.assertFalse(person != null && person.getName().equals(name)
                    && person.getSurname().equals(surname));

            Assert.assertTrue(bank.createPerson(name, surname, passportID));

            person = bank.getPerson(passportID, REMOTE);
            Assert.assertTrue(person != null && person.getName().equals(name)
                    && person.getSurname().equals(surname));
        }
    }

    @org.junit.Test
    public void test4() throws RemoteException {
        Assert.assertNull(bank.getPerson(Integer.toString(-1), LOCAL));
        Assert.assertNull(bank.getPerson(Integer.toString(-1), REMOTE));
        for (int i = 0; i < MAX_COUNT; i++) {
            String name = "test04User" + i;
            String surname = "";
            String passportID = "test04PassportID" + i;

            bank.createPerson(name, surname, passportID);

            Person remotePerson = bank.getPerson(passportID, REMOTE);
            Assert.assertEquals(name, remotePerson.getName());
            Assert.assertEquals(surname, remotePerson.getSurname());
            Assert.assertEquals(passportID, remotePerson.getPassportID());

            Person localPerson = bank.getPerson(passportID, LOCAL);
            Assert.assertEquals(name, localPerson.getName());
            Assert.assertEquals(surname, localPerson.getSurname());
            Assert.assertEquals(passportID, localPerson.getPassportID());
        }
    }

    @org.junit.Test
    public void test5() throws RemoteException {
        String name = "test05User";
        String surname = "1";
        String passportID = "test05PassportID1";
        String subID = "1";

        bank.createPerson(name, surname, passportID);
        Person remotePerson = bank.getPerson(passportID, REMOTE);
        Assert.assertNotNull(remotePerson);
        Assert.assertTrue(bank.createAccount(remotePerson.getPassportID() + ":" + subID));

        Account remoteAccount = bank.getAccount(remotePerson, subID);
        Person localPerson1 = bank.getPerson(passportID, LOCAL);
        Assert.assertNotNull(localPerson1);

        remoteAccount.setAmount(remoteAccount.getAmount() + 100000);
        Person localPerson2 = bank.getPerson(passportID, LOCAL);
        Assert.assertNotNull(localPerson2);

        Account localAccount1 = bank.getAccount(localPerson2, subID);
        Account localAccount2 = bank.getAccount(localPerson1, subID);
        Assert.assertEquals(localAccount1.getAmount(), remoteAccount.getAmount());
        Assert.assertEquals(localAccount2.getAmount() + 100000, localAccount1.getAmount());
    }

    @org.junit.Test
    public void test6() throws RemoteException {
        String name = "test06User";
        String surname = "2";
        String passportID = "test06PassportID1";
        String subID = "2";

        bank.createPerson(name, surname, passportID);
        Person remotePerson = bank.getPerson(passportID, REMOTE);
        Assert.assertNotNull(remotePerson);
        Assert.assertTrue(bank.createAccount(remotePerson.getPassportID() + ":" + subID));

        Person localPerson = bank.getPerson(passportID, LOCAL);
        Assert.assertNotNull(localPerson);
        Account localAccount = bank.getAccount(localPerson, subID);
        localAccount.setAmount(localAccount.getAmount() + 100000);
        Account remoteAccount = bank.getAccount(remotePerson, subID);
        Assert.assertEquals(100000, localAccount.getAmount());
        Assert.assertEquals(0, remoteAccount.getAmount());
    }

    @org.junit.Test
    public void test7() throws RemoteException {
        String name = "test07User";
        String surname = "1";
        String passportID = "test07PassportID1";
        String subID1 = "1";
        String subID2 = "2";

        bank.createPerson(name, surname, passportID);
        Person remotePerson1 = bank.getPerson(passportID, REMOTE);
        Person remotePerson2 = bank.getPerson(passportID, REMOTE);

        bank.createAccount(remotePerson1.getPassportID() + ":" + subID1);
        bank.createAccount(remotePerson2.getPassportID() + ":" + subID2);
        Assert.assertEquals(2, bank.getAllSubID(remotePerson1).size());
        Assert.assertEquals(bank.getAllSubID(remotePerson1).size(), bank.getAllSubID(remotePerson2).size());
    }

    @org.junit.Test
    public void test8() throws RemoteException {
        String name = "test08User";
        String surname = "1";
        String passportID = "test08PassportID1";

        bank.createPerson(name, surname, passportID);
        Person localPerson1 = bank.getPerson(passportID, LOCAL);
        Person localPerson2 = bank.getPerson(passportID, LOCAL);

        String subID1 = "1";
        String subID2 = "2";
        bank.createAccount(localPerson1.getPassportID() + ":" + subID1);
        bank.createAccount(localPerson2.getPassportID() + ":" + subID2);

        Person localPerson3 = bank.getPerson(passportID, LOCAL);
        Assert.assertEquals(2, bank.getAllSubID(localPerson3).size());
        Assert.assertEquals(0, bank.getAllSubID(localPerson1).size());
        Assert.assertEquals(bank.getAllSubID(localPerson1).size(), bank.getAllSubID(localPerson2).size());
    }
}