package com.wethinkcode.fixme.router.models;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RouterMessageHandler implements Runnable {
    private @Setter int brokerID;
    private @Setter final AsynchronousSocketChannel channel;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    String message;
    private @Setter @Getter Integer messageDestination;
    Hashtable<Integer, AsynchronousSocketChannel> routingTable;

    private AbstractMessageHandler messageChain;
    boolean connected = true;
    static int count = 1;



    public RouterMessageHandler(AsynchronousSocketChannel channel, int brokerID, Hashtable<Integer, AsynchronousSocketChannel> routingTable) throws InterruptedException {
        count = 1;
        this.channel = channel;
        this.brokerID = brokerID;
        this.routingTable = routingTable;

        messageChain = new BrokerMessager();
        messageChain.addNext(new MarketMessager());
    }

    @Override
    public void run() {
        try {
            while (connected) {
                readMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendNewID() {
        channel.write(ByteBuffer.wrap(String.valueOf(brokerID).getBytes()));
    }

    public void readMessage() throws InterruptedException, IOException, ExecutionException {
        Future<Integer> future = channel.read(buffer);

        while (!future.isDone()) {
            Thread.sleep(250);
        }

        if (future.get() != -1) {
            message = new String(buffer.array()).trim();

            FIXMessage FixMessage = new FIXMessage(message);

            buffer.clear();
            if (message.contains("Close Channel")) {
                DisconnectChannel();
            } else if (message.equals("New Connection")) {
                sendNewID();
            } else {
                if (count == 500000) {
                    DisconnectChannel();
                }
                if (!FixMessage.validateChecksum()) {
                    System.out.println("No valid checksum");
                } else {
                    try {
                        messageChain.handleMessage(message, routingTable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                count++;
            }
        } else {
            connected = false;
            System.out.println("A channel was disconnected");
        }
    }

    private void DisconnectChannel() throws IOException {
        routingTable.remove(channel);
        channel.close();
        connected = false;
        count = 1;
        System.out.println("Closed channel connection of channel");
    }

}
