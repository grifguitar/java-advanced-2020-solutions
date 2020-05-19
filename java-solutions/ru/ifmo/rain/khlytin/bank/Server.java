package ru.ifmo.rain.khlytin.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private final static int PORT = 8888;
    private final static String URL = "//localhost:" + PORT + "/bank";

    public static void main(final String... args) {
        final Bank bank = new RemoteBank(PORT);
        try {
            Registry rmiRegistry = LocateRegistry.createRegistry(PORT);
            UnicastRemoteObject.exportObject(bank, PORT);
            Naming.rebind(URL, bank);
        } catch (final RemoteException | MalformedURLException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Server started");
    }
}