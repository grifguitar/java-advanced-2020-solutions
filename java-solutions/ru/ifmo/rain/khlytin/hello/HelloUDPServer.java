package ru.ifmo.rain.khlytin.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author khlyting
 */
public class HelloUDPServer implements HelloServer {
    //finals?
    private static final int TERMINATION_AWAIT = 1;
    private DatagramSocket socket;
    private ExecutorService mainExecutor;
    private ExecutorService workers;
    private int buffSize = 0;

    public static void main(String[] args) {
        if (args == null || args.length != 2 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("No null arguments expected: port threads");
        } else {
            try {
                int port = Integer.parseInt(args[0]);
                int threads = Integer.parseInt(args[1]);
                new HelloUDPServer().start(port, threads);
            } catch (NumberFormatException e) {
                System.err.println("Numeric arguments expected: port, threads. " + e.getMessage());
            }
        }
    }

    private static String getResponseMessage(final DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    private static void setData(final DatagramPacket packet, String message) {
        packet.setData(message.getBytes(StandardCharsets.UTF_8));
    }

    private void process() {
        try {
            while (!socket.isClosed() && !Thread.interrupted()) {
                final DatagramPacket packet = new DatagramPacket(new byte[buffSize], buffSize);
                socket.receive(packet);
                workers.submit(() -> {
                    final String requestMessage = getResponseMessage(packet);
                    String responseMessage = "Hello, " + requestMessage;
                    setData(packet, responseMessage);
                    try {
                        socket.send(packet);
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            System.err.println("An error occurred while sending the response: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.err.println("An error occurred while processing the request: " + e.getMessage());
            }
        }
    }

    @Override
    public void start(int port, int threads) {
        try {
            socket = new DatagramSocket(port);
            buffSize = socket.getReceiveBufferSize();
            workers = Executors.newFixedThreadPool(threads);
            mainExecutor = Executors.newSingleThreadExecutor();
            mainExecutor.submit(this::process);
        } catch (SocketException e) {
            System.err.println("The socket could not be created or opened: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        socket.close();
        mainExecutor.shutdown();
        workers.shutdown();
        try {
            mainExecutor.awaitTermination(TERMINATION_AWAIT, TimeUnit.SECONDS);
            workers.awaitTermination(TERMINATION_AWAIT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Failed to terminate executor pools: " + e.getMessage());
        }
    }
}