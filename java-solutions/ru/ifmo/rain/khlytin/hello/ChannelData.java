package ru.ifmo.rain.khlytin.hello;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ChannelData {
    private final int id;
    private int counter = 0;
    int buffSize;
    ByteBuffer buffer;

    ChannelData(final int id, int buffSize) {
        this.id = id;
        this.buffSize = buffSize;
        this.buffer = ByteBuffer.allocate(buffSize);
    }

    public int getId() {
        return id;
    }

    public int getCounter() {
        return counter;
    }

    public void incCounter() {
        counter++;
    }

    public boolean isFinished(int requests) {
        return (counter >= requests);
    }

    public void readyWrite() {
        buffer.clear();
    }

    public void readyRead() {
        buffer.flip();
    }

    public void write(String message) {
        readyWrite();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
    }

    public String read() {
        readyRead();
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
}
