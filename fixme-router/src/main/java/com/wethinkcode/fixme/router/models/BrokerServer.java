package com.wethinkcode.fixme.router.models;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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

    private static ByteBuffer ClearBuffer(ByteBuffer buffer) {
//        System.out.println("Before clear buffer" + new String(buffer.array()).trim());
        buffer.clear();
        buffer = ByteBuffer.allocate(2048);
        buffer.clear();
        buffer.put(new byte[2048]);
        buffer.clear();
//        System.out.println("After clear buffer" + new String(buffer.array()).trim());
        return buffer;
    }

    private static int setBrokerID() {
        return ++brokerID;
    }

    private static void Sleep(long seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
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
                        System.out.println("We have a new client\tRemote: " + clientSocket.getRemoteAddress() + "\tLocal: " + clientSocket.getLocalAddress() + "\tclient: " + clientSocket);

                        routingTable.put(brokerID, clientSocket);
                        RouterMessageHandler messageHandler = new RouterMessageHandler(clientSocket, brokerID, routingTable);

                        new Thread(messageHandler).start();

//                        messageHandler.readMessage();
//                        new Thread(messageHandler).start();
//                        Thread.currentThread().join();

//                        if (messageHandler.firstConnection) {
////                            System.out.println("Sending broker ID " + brokerID);
//////                            messageHandler.sendNewID();
//                            routingTable.put(brokerID, clientSocket);
//                            messageHandler.readMessage();
//                            brokerID++;
//                        } else {
////                            System.out.println("Sending message from broker to market");
////                            messageHandler.sendMessage(routingTable.get(0));
//                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    brokerID++;
                    brokerserver.accept(null, this);

//                    brokerserver.

//                    System.out.println("Waiting for next message");

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
        }/* catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }

}
