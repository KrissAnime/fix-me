package com.wethinkcode.fixme.router.models;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Hashtable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RouterMessageHandler implements Runnable {
    private @Setter int brokerID = 0;
    private @Setter final AsynchronousSocketChannel channel;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    String message;
    private @Setter @Getter Integer messageDestination;
    Hashtable<Integer, AsynchronousSocketChannel> routingTable;



    public RouterMessageHandler(AsynchronousSocketChannel channel, int brokerID, Hashtable<Integer, AsynchronousSocketChannel> routingTable) throws InterruptedException {
        this.channel = channel;
        this.brokerID = brokerID;
        this.routingTable = routingTable;
    }

    @Override
    public void run() {
        try {
            while (true) {
                readMessage();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendNewID() {
        channel.write(ByteBuffer.wrap(String.valueOf(brokerID).getBytes()));
    }

    public void readMessage() throws InterruptedException {
        Future<Integer> future = channel.read(buffer);

        while (!future.isDone()) {
            Thread.sleep(250);
        }

        message = new String(buffer.array()).trim();
        buffer.clear();
        if (message.contains("Close Channel")) {
            System.exit(1);
        } else if (message.equals("New Connection")) {
            sendNewID();
            readMessage();
        } else {
            if (message.contains("50=") && message.contains("56=")) {
                messageDestination = Integer.parseInt(message.split("\\|")[2].split("=")[1]);
                System.out.println("Sending to broker... " + message);
                sendMessage(routingTable.get(messageDestination), message);
            } else {
                System.out.println("Sending to market... " + message);
                sendMessage(routingTable.get(0), message);
            }
        }
    }

    public void sendMessage(AsynchronousSocketChannel destination, String message) throws InterruptedException {
        Future<Integer> future = destination.write(ByteBuffer.wrap(message.getBytes()));
        while (!future.isDone()) {
            Thread.sleep(250);
        }
    }
}
