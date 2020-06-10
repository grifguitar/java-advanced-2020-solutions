package ru.ifmo.rain.khlytin.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPNonblockingServer implements HelloServer {
    //finals?
    private static final int TERMINATION_AWAIT = 1;
    private Selector selector;
    private DatagramChannel channel;
    private ExecutorService mainExecutor;
    private ExecutorService workers;
    //LinkedDeque not queue?
    private ConcurrentLinkedDeque<PacketData> sendingQueue;
    private ConcurrentLinkedDeque<PacketData> receivingQueue;

    public static void main(String[] args) {
        //copypaste
        if (args == null || args.length != 2 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("No null arguments expected: port threads");
        } else {
            try {
                int port = Integer.parseInt(args[0]);
                int threads = Integer.parseInt(args[1]);
                new HelloUDPNonblockingServer().start(port, threads);
            } catch (NumberFormatException e) {
                System.err.println("Numeric arguments expected: port, threads. " + e.getMessage());
            }
        }
    }

    private static String getMessage(ByteBuffer buffer) {
        buffer.flip();
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    private static ByteBuffer getBuffer(String message) {
        return ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
    }

    private void tryHandle(SelectionKey key) {
        DatagramChannel channel = (DatagramChannel) key.channel();
        if (key.isReadable()) {
            workers.submit(() -> {
                synchronized (key) {
                    PacketData inputData = receivingQueue.pop();
                    ByteBuffer buffer = inputData.buffer.clear();
                    if (receivingQueue.isEmpty()) {
                        key.interestOpsAnd(~SelectionKey.OP_READ);
                        selector.wakeup();
                    }
                    SocketAddress address = null;
                    try {
                        address = channel.receive(buffer);
                    } catch (IOException e) {
                        System.err.println("An error occurred while receiving data: " + e.getMessage());
                    }
                    String requestMessage = getMessage(buffer);
                    String responseMessage = "Hello, " + requestMessage;
                    PacketData outputData = new PacketData(getBuffer(responseMessage), address);
                    if (sendingQueue.isEmpty()) {
                        sendingQueue.add(outputData);
                        key.interestOpsOr(SelectionKey.OP_WRITE);
                        selector.wakeup();
                    } else {
                        sendingQueue.add(outputData);
                    }
                }
            });
        }
        if (key.isWritable()) {
            workers.submit(() -> {
                synchronized (key) {
                    PacketData packetData = sendingQueue.removeFirst();
                    if (sendingQueue.isEmpty()) {
                        key.interestOpsAnd(~SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
                    try {
                        channel.send(packetData.buffer, packetData.address);
                    } catch (IOException e) {
                        System.err.println("An error occurred while sending data: " + e.getMessage());
                    }
                    packetData.buffer.clear();
                    packetData.buffer.flip();
                    if (receivingQueue.isEmpty()) {
                        receivingQueue.add(packetData);
                        key.interestOpsOr(SelectionKey.OP_READ);
                        selector.wakeup();
                    } else {
                        receivingQueue.add(packetData);
                    }
                }
            });
        }
    }

    private void process() {
        while (!channel.socket().isClosed() && !Thread.interrupted()) {
            try {
                selector.select();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            for (final Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                final SelectionKey key = it.next();
                try {
                    tryHandle(key);
                } finally {
                    it.remove();
                }
            }
        }
    }

    private void createChannel(final SocketAddress address) {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.bind(address);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (SocketException e) {
            System.err.println("The socket could not be opened: " + e.getMessage());
        } catch (ClosedChannelException e) {
            System.err.println("The channel is already closed: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An error occurred while opening or processing a channel: " + e.getMessage());
        }
    }

    @Override
    public void start(int port, int threads) {
        try {
            selector = Selector.open();
            createChannel(new InetSocketAddress(port));
            int buffSize = channel.socket().getReceiveBufferSize();
            sendingQueue = new ConcurrentLinkedDeque<>();
            receivingQueue = new ConcurrentLinkedDeque<>();
            for (int i = 0; i < threads; i++) {
                receivingQueue.add(new PacketData(ByteBuffer.allocate(buffSize)));
            }
            workers = Executors.newFixedThreadPool(threads);
            mainExecutor = Executors.newSingleThreadExecutor();
            mainExecutor.submit(this::process);
        } catch (UnknownHostException e) {
            System.err.println("Unable to access host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Unable to open selector: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            selector.close();
            channel.close();
            mainExecutor.shutdown();
            workers.shutdown();
            mainExecutor.awaitTermination(TERMINATION_AWAIT, TimeUnit.SECONDS);
            workers.awaitTermination(TERMINATION_AWAIT, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            System.err.println("Failed to terminate executor pools or channel: " + e.getMessage());
        }
    }
}