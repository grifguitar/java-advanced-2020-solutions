package ru.ifmo.rain.khlytin.hello;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class PacketData {
    ByteBuffer buffer;
    SocketAddress address;

    PacketData(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    PacketData(ByteBuffer buffer, SocketAddress address) {
        this.buffer = buffer;
        this.address = address;
    }
}
