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
    private static Bank bank;
    private final static int PORT = 8888;
    private final static String URL = "//localhost:" + PORT + "/bank";
    private static final int MAX_COUNT = 20;

    @BeforeClass
    public static void beforeClass() throws Exception {
        bank = new RemoteBank(PORT);
        Registry rmiRegistry = LocateRegistry.createRegistry(PORT);
        UnicastRemoteObject.exportObject(bank, PORT);
        Naming.rebind(URL, bank);
    }

    @org.junit.Test
    public void createAccountTest() throws RemoteException {
        bank.createPerson("test01User", "1", "test01PassportID1");
        Person localPerson = bank.getLocalPerson("test01PassportID1");
        Person remotePerson = bank.getRemotePerson("test01PassportID1");

        bank.createAccount("2", remotePerson);
        Assert.assertNull(bank.getAccount("2", localPerson));
        Assert.assertEquals(1, bank.getAllSubID(remotePerson).size());
        Assert.assertNotNull(bank.getAccount("2", remotePerson));
        Assert.assertNotEquals(bank.getAllSubID(localPerson), bank.getAllSubID(remotePerson));
    }

    @org.junit.Test
    public void getAccountsTest() throws RemoteException {
        for (int i = 0; i < MAX_COUNT; i++) {
            Assert.assertTrue(bank.createPerson("test02User", Integer.toString(i), "test02PassportID" + i));

            Random random = new Random();
            int done = 0;

            Person remotePerson = bank.getRemotePerson("test02PassportID" + i);
            for (int j = 0; j < random.nextInt(MAX_COUNT); ++j) {
                if (bank.createAccount(Integer.toString(random.nextInt()), remotePerson)) {
                    done++;
                }
            }

            Set<String> accounts = bank.getAllSubID(remotePerson);
            Assert.assertNotNull(accounts);
            Assert.assertEquals(done, accounts.size());
        }
    }

    @org.junit.Test
    public void createPersonTest() throws RemoteException {
        for (int i = 0; i < MAX_COUNT; i++) {
            Assert.assertFalse(bank.isPerson("test03User" + i, "", "test03PassportID" + i));
            Assert.assertTrue(bank.createPerson("test03User" + i, "", "test03PassportID" + i));
            Assert.assertTrue(bank.isPerson("test03User" + i, "", "test03PassportID" + i));
        }
    }

    @org.junit.Test
    public void getPersonTest() throws RemoteException {
        Assert.assertNull(bank.getLocalPerson(Integer.toString(-1)));
        Assert.assertNull(bank.getRemotePerson(Integer.toString(-1)));

        for (int i = 0; i < MAX_COUNT; i++) {
            bank.createPerson("test04User" + i, "", "test04PassportID" + i);

            Person remotePerson = bank.getRemotePerson("test04PassportID" + i);
            Assert.assertEquals("test04User" + i, remotePerson.getName());
            Assert.assertEquals("", remotePerson.getSurname());
            Assert.assertEquals("test04PassportID" + i, remotePerson.getPassportID());

            Person localPerson = bank.getLocalPerson("test04PassportID" + i);
            Assert.assertEquals("test04User" + i, localPerson.getName());
            Assert.assertEquals("", localPerson.getSurname());
            Assert.assertEquals("test04PassportID" + i, localPerson.getPassportID());
        }
    }

    @org.junit.Test
    public void localAfterRemoteTest() throws RemoteException {
        bank.createPerson("test05User", "1", "test05PassportID1");
        Person remotePerson = bank.getRemotePerson("test05PassportID1");

        Assert.assertNotNull(remotePerson);
        Assert.assertTrue(bank.createAccount("1", remotePerson));
        Account remoteAccount = bank.getAccount("1", remotePerson);

        Person localPerson1 = bank.getLocalPerson("test05PassportID1");
        Assert.assertNotNull(localPerson1);

        remoteAccount.setAmount(remoteAccount.getAmount() + 100000);

        Person localPerson2 = bank.getLocalPerson("test05PassportID1");
        Assert.assertNotNull(localPerson2);

        Account localAccount1 = bank.getAccount("1", localPerson2);
        Account localAccount2 = bank.getAccount("1", localPerson1);

        Assert.assertEquals(localAccount1.getAmount(), remoteAccount.getAmount());
        Assert.assertEquals(localAccount2.getAmount() + 100000, localAccount1.getAmount());
    }

    @org.junit.Test
    public void remoteAfterLocalTest() throws RemoteException {
        bank.createPerson("test06User", "1", "test06PassportID1");

        Person remotePerson = bank.getRemotePerson("test06PassportID1");
        Assert.assertNotNull(remotePerson);

        Assert.assertTrue(bank.createAccount("1", remotePerson));
        Person localPerson = bank.getLocalPerson("test06PassportID1");
        Assert.assertNotNull(localPerson);

        Account localAccount = bank.getAccount("1", localPerson);
        localAccount.setAmount(localAccount.getAmount() + 100000);

        Account remoteAccount = bank.getAccount("1", remotePerson);

        Assert.assertEquals(100000, localAccount.getAmount());
        Assert.assertEquals(0, remoteAccount.getAmount());
    }

    @org.junit.Test
    public void remoteAfterRemoteTest() throws RemoteException {
        bank.createPerson("test07User", "1", "test07PassportID1");

        Person remotePerson1 = bank.getRemotePerson("test07PassportID1");
        Person remotePerson2 = bank.getRemotePerson("test07PassportID1");

        bank.createAccount("1", remotePerson1);
        bank.createAccount("2", remotePerson2);

        Assert.assertEquals(2, bank.getAllSubID(remotePerson1).size());
        Assert.assertEquals(bank.getAllSubID(remotePerson1).size(), bank.getAllSubID(remotePerson2).size());
    }

    @org.junit.Test
    public void localAfterLocalTest() throws RemoteException {
        bank.createPerson("test08User", "1", "test08PassportID1");

        Person localPerson1 = bank.getLocalPerson("test08PassportID1");
        Person localPerson2 = bank.getLocalPerson("test08PassportID1");

        bank.createAccount("1", localPerson1);
        bank.createAccount("2", localPerson2);

        Person localPerson3 = bank.getLocalPerson("test08PassportID1");

        Assert.assertEquals(2, bank.getAllSubID(localPerson3).size());
        Assert.assertEquals(0, bank.getAllSubID(localPerson1).size());
        Assert.assertEquals(bank.getAllSubID(localPerson1).size(), bank.getAllSubID(localPerson2).size());
    }
}