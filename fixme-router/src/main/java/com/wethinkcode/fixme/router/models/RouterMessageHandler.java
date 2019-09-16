package com.wethinkcode.fixme.router.models;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Hashtable;
import java.util.concurrent.Future;

public class RouterMessageHandler implements Runnable {
    private @Setter int brokerID = 0;
    private @Setter final AsynchronousSocketChannel channel;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    String message;
    private @Setter @Getter Integer messageDestination;
    Hashtable<Integer, AsynchronousSocketChannel> routingTable;

    private AbstractMessageHandler messageChain;



    public RouterMessageHandler(AsynchronousSocketChannel channel, int brokerID, Hashtable<Integer, AsynchronousSocketChannel> routingTable) throws InterruptedException {
        this.channel = channel;
        this.brokerID = brokerID;
        this.routingTable = routingTable;

        messageChain = new BrokerMessager();
        messageChain.addNext(new MarketMessager());
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

        FIXMessage FixMessage = new FIXMessage(message);


        buffer.clear();
        if (message.contains("Close Channel")) {
            System.exit(1);
        } else if (message.equals("New Connection")) {
            sendNewID();
            readMessage();
        } else {

            if (!FixMessage.validateChecksum())
                System.out.println("No valid checksum");

            try {
                messageChain.handleMessage(message, routingTable);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

}
