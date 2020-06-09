package ru.ifmo.rain.khlytin.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;

public class HelloUDPNonblockingClient implements HelloClient {
    private static final int SOCKET_TIMEOUT = 200;
    private Selector selector;

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
                new HelloUDPNonblockingClient().run(nameOrIp, port, prefix, threads, requests);
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

    private static boolean check(String requestMessage, String responseMessage) {
        return responseMessage.contains(requestMessage);
    }

    private void tryHandle(SelectionKey key, boolean onlyWritable,
                           final SocketAddress address, String prefix, int requests) {
        try {
            DatagramChannel channel = (DatagramChannel) key.channel();
            ChannelData data = (ChannelData) key.attachment();
            String requestMessage = getRequestMessage(prefix, data.getId(), data.getCounter());
            String responseMessage;
            if (key.isWritable()) {
                data.write(requestMessage);
                data.readyRead();
                channel.send(data.buffer, address);
                logging("Sent: " + requestMessage);
                key.interestOps(SelectionKey.OP_READ);
            }
            if (!onlyWritable && key.isReadable()) {
                data.readyWrite();
                channel.receive(data.buffer);
                responseMessage = data.read();
                if (check(requestMessage, responseMessage)) {
                    logging("Received: " + responseMessage);
                    data.incCounter();
                }
                if (data.isFinished(requests)) {
                    key.channel().close();
                    return;
                }
                key.interestOps(SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void process(final SocketAddress address, String prefix, int requests) {
        while (!selector.keys().isEmpty() && !Thread.interrupted()) {
            try {
                selector.select(SOCKET_TIMEOUT);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            if (!selector.selectedKeys().isEmpty()) {
                for (final Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                    final SelectionKey key = it.next();
                    try {
                        tryHandle(key, false, address, prefix, requests);
                    } finally {
                        it.remove();
                    }
                }
                continue;
            }
            selector.keys().forEach(key -> tryHandle(key, true, address, prefix, requests));
        }
    }

    private void createChannel(final int id, final SocketAddress address) {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.connect(address);
            channel.register(selector, SelectionKey.OP_WRITE,
                    new ChannelData(id, channel.socket().getReceiveBufferSize()));
        } catch (SocketException e) {
            System.err.println("The socket could not be opened: " + e.getMessage());
        } catch (ClosedChannelException e) {
            System.err.println("The channel is already closed: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An error occurred while opening or processing a channel: " + e.getMessage());
        }
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try {
            selector = Selector.open();
            final SocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
            IntStream.range(0, threads).forEach(id -> createChannel(id, address));
            process(address, prefix, requests);
        } catch (UnknownHostException e) {
            System.err.println("Unable to access host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Unable to open selector: " + e.getMessage());
        }
    }
}