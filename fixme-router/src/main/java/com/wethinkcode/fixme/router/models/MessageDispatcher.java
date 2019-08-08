package com.wethinkcode.fixme.router.models;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher {
    private Map<String, AsynchronousSocketChannel> addresses = new HashMap<>();

    public MessageDispatcher() {
    }

    public void AddAddress(AsynchronousSocketChannel client) throws IOException {
        if (!addresses.containsKey(client.getRemoteAddress().toString().split(":")[1])) {
            addresses.put(client.getRemoteAddress().toString().split(":")[1], client);
        }
    }

    public void DispatchMessage(FIXMessage message) throws IOException {
        if (addresses.containsKey(message.getRoutingReceiverID())) {
            addresses.get(message.getRoutingReceiverID()).write(ByteBuffer.wrap(message.toString().getBytes()));
        }
    }

    public void RemoveAddress(AsynchronousSocketChannel client) throws IOException {
        addresses.remove(client.getRemoteAddress().toString().split(":")[1]);
    }
}
