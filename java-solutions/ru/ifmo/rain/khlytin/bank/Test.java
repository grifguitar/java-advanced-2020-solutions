package ru.ifmo.rain.khlytin.bank;

import org.junit.BeforeClass;
import org.junit.Assert;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
        Registry rmiRegistry = LocateRegistry.createRegistry(PORT);
        UnicastRemoteObject.exportObject(bank, PORT);
        Naming.rebind(URL, bank);
    }

    @org.junit.Test
    public void createAccountTest() throws RemoteException {
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
    public void getAccountsTest() throws RemoteException {
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
    public void createPersonTest() throws RemoteException {
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
    public void getPersonTest() throws RemoteException {
        Assert.assertNull(bank.getPerson(Integer.toString(-1), LOCAL));
        Assert.assertNull(bank.getPerson(Integer.toString(-1), REMOTE));

        for (int i = 0; i < MAX_COUNT; i++) {
            bank.createPerson("test04User" + i, "", "test04PassportID" + i);

            Person remotePerson = bank.getPerson("test04PassportID" + i, REMOTE);
            Assert.assertEquals("test04User" + i, remotePerson.getName());
            Assert.assertEquals("", remotePerson.getSurname());
            Assert.assertEquals("test04PassportID" + i, remotePerson.getPassportID());

            Person localPerson = bank.getPerson("test04PassportID" + i, LOCAL);
            Assert.assertEquals("test04User" + i, localPerson.getName());
            Assert.assertEquals("", localPerson.getSurname());
            Assert.assertEquals("test04PassportID" + i, localPerson.getPassportID());
        }
    }

    @org.junit.Test
    public void localAfterRemoteTest() throws RemoteException {
        bank.createPerson("test05User", "1", "test05PassportID1");
        Person remotePerson = bank.getPerson("test05PassportID1", REMOTE);

        Assert.assertNotNull(remotePerson);
        Assert.assertTrue(bank.createAccount(remotePerson.getPassportID() + ":" + "1"));
        Account remoteAccount = bank.getAccount(remotePerson, "1");

        Person localPerson1 = bank.getPerson("test05PassportID1", LOCAL);
        Assert.assertNotNull(localPerson1);

        remoteAccount.setAmount(remoteAccount.getAmount() + 100000);

        Person localPerson2 = bank.getPerson("test05PassportID1", LOCAL);
        Assert.assertNotNull(localPerson2);

        Account localAccount1 = bank.getAccount(localPerson2, "1");
        Account localAccount2 = bank.getAccount(localPerson1, "1");

        Assert.assertEquals(localAccount1.getAmount(), remoteAccount.getAmount());
        Assert.assertEquals(localAccount2.getAmount() + 100000, localAccount1.getAmount());
    }

    @org.junit.Test
    public void remoteAfterLocalTest() throws RemoteException {
        bank.createPerson("test06User", "1", "test06PassportID1");

        Person remotePerson = bank.getPerson("test06PassportID1", REMOTE);
        Assert.assertNotNull(remotePerson);

        Assert.assertTrue(bank.createAccount(remotePerson.getPassportID() + ":" + "1"));
        Person localPerson = bank.getPerson("test06PassportID1", LOCAL);
        Assert.assertNotNull(localPerson);

        Account localAccount = bank.getAccount(localPerson, "1");
        localAccount.setAmount(localAccount.getAmount() + 100000);

        Account remoteAccount = bank.getAccount(remotePerson, "1");

        Assert.assertEquals(100000, localAccount.getAmount());
        Assert.assertEquals(0, remoteAccount.getAmount());
    }

    @org.junit.Test
    public void remoteAfterRemoteTest() throws RemoteException {
        bank.createPerson("test07User", "1", "test07PassportID1");

        Person remotePerson1 = bank.getPerson("test07PassportID1", REMOTE);
        Person remotePerson2 = bank.getPerson("test07PassportID1", REMOTE);

        bank.createAccount(remotePerson1.getPassportID() + ":" + "1");
        bank.createAccount(remotePerson2.getPassportID() + ":" + "2");

        Assert.assertEquals(2, bank.getAllSubID(remotePerson1).size());
        Assert.assertEquals(bank.getAllSubID(remotePerson1).size(), bank.getAllSubID(remotePerson2).size());
    }

    @org.junit.Test
    public void localAfterLocalTest() throws RemoteException {
        bank.createPerson("test08User", "1", "test08PassportID1");

        Person localPerson1 = bank.getPerson("test08PassportID1", LOCAL);
        Person localPerson2 = bank.getPerson("test08PassportID1", LOCAL);

        bank.createAccount(localPerson1.getPassportID() + ":" + "1");
        bank.createAccount(localPerson2.getPassportID() + ":" + "2");

        Person localPerson3 = bank.getPerson("test08PassportID1", LOCAL);

        Assert.assertEquals(2, bank.getAllSubID(localPerson3).size());
        Assert.assertEquals(0, bank.getAllSubID(localPerson1).size());
        Assert.assertEquals(bank.getAllSubID(localPerson1).size(), bank.getAllSubID(localPerson2).size());
    }
}