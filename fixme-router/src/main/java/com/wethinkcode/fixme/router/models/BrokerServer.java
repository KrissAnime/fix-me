package com.wethinkcode.fixme.router.models;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class BrokerServer implements Runnable {
    static int brokerID = 100000;
    AsynchronousChannelGroup group;
    Hashtable<Integer, AsynchronousSocketChannel> routingTable;


    public BrokerServer(AsynchronousChannelGroup group, Hashtable<Integer, AsynchronousSocketChannel> routingTable) {
        this.group = group;
        this.routingTable = routingTable;
    }

    @Override
    public void run() {
        try {
            System.out.println("Broker Server is ready");
            AsynchronousServerSocketChannel brokerserver = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5000));
            brokerserver.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel clientSocket, Void attachment) {
                    try {
                        routingTable.put(brokerID, clientSocket);
                        System.out.println("New broker connected " + brokerID);
                        RouterMessageHandler messageHandler = new RouterMessageHandler(clientSocket, brokerID, routingTable);

                        new Thread(messageHandler).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    brokerID++;
                    brokerserver.accept(null, this);
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    try {
                        brokerserver.close();
                    } catch (IOException e) {
                        System.out.println("IOException " + e.getMessage());
                    }
                    System.out.println("There was an error");

                }
            });
            group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (IOException e) {
            System.out.println("Error starting broker server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Broker server was interrupted");
            e.printStackTrace();
        }
    }

}
