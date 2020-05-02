package ru.ifmo.rain.khlytin.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author khlyting
 */
public class HelloUDPClient implements HelloClient {
    private static final int REQUEST_TIMEOUT = 10;
    private static final int SOCKET_TIMEOUT = 200;

    public static void main(String[] args) {
        if (args == null || args.length != 5 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("No null arguments expected: [name or ip] port prefix threads requests");
        } else {
            try {
                String nameOrIp = args[0];
                int port = Integer.parseInt(args[1]);
                String prefix = args[2];
                int threads = Integer.parseInt(args[3]);
                int requests = Integer.parseInt(args[4]);
                new HelloUDPClient().run(nameOrIp, port, prefix, threads, requests);
            } catch (NumberFormatException e) {
                System.err.println("Numeric arguments expected: port, threads and requests. " + e.getMessage());
            }
        }
    }

    private static void logging(String message) {
        System.out.println(message);
    }

    private static String getRequestMessage(String prefix, int threadId, int requestId) {
        return prefix + threadId + "_" + requestId;
    }

    private static String getResponseMessage(final DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    private static void setData(final DatagramPacket packet, String message) {
        packet.setData(message.getBytes(StandardCharsets.UTF_8));
    }

    private static void resetData(final DatagramPacket packet, int buffSize) {
        packet.setData(new byte[buffSize]);
    }

    private static boolean check(String requestMessage, String responseMessage) {
        return responseMessage.contains(requestMessage);
    }

    private static void process(final SocketAddress address, String prefix, int threadId, int requests) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(SOCKET_TIMEOUT);
            int buffSize = socket.getReceiveBufferSize();
            final DatagramPacket packet = new DatagramPacket(new byte[buffSize], buffSize, address);
            for (int requestId = 0; requestId < requests; requestId++) {
                String requestMessage = getRequestMessage(prefix, threadId, requestId);
                String responseMessage;
                logging("Sent: " + requestMessage);
                boolean isReceived = false;
                while (!isReceived && !socket.isClosed() && !Thread.interrupted()) {
                    try {
                        setData(packet, requestMessage);
                        socket.send(packet);
                        resetData(packet, buffSize);
                        socket.receive(packet);
                        responseMessage = getResponseMessage(packet);
                        isReceived = check(requestMessage, responseMessage);
                        if (isReceived) {
                            logging("Received: " + responseMessage);
                        }
                    } catch (IOException e) {
                        System.err.println("An error occurred while sending the request or processing the response: "
                                + e.getMessage());
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("The socket could not be created or opened: " + e.getMessage());
        }
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try {
            final ExecutorService executorService = Executors.newFixedThreadPool(threads);
            final SocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
            IntStream.range(0, threads).forEach(threadId -> executorService.submit(() ->
                    process(address, prefix, threadId, requests)));
            executorService.shutdown();
            executorService.awaitTermination(requests * threads * REQUEST_TIMEOUT, TimeUnit.SECONDS);
        } catch (UnknownHostException e) {
            System.err.println("Unable to access host: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}