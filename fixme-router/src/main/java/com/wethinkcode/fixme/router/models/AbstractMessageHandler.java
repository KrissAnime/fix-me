package com.wethinkcode.fixme.router.models;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Hashtable;
import java.util.concurrent.Future;

public abstract class AbstractMessageHandler
{
    private AbstractMessageHandler next;
    protected Integer messageDestination;

    public void addNext(AbstractMessageHandler messageHandler) {
        this.next = messageHandler;
    }

    public abstract void handleMessage(String message, Hashtable<Integer, AsynchronousSocketChannel> routingTable) throws Exception;

    public void sendMessage(AsynchronousSocketChannel destination, String message) throws InterruptedException {
        Future<Integer> future = destination.write(ByteBuffer.wrap(message.getBytes()));
        while (!future.isDone()) {
            Thread.sleep(250);
        }
    }
}
