package com.wethinkcode.fixme.router.models;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class BrokerServer implements Runnable {
    AsynchronousChannelGroup group;
    Hashtable<Integer, AsynchronousSocketChannel> routingTable;
    static int brokerID = 99999;


    public BrokerServer(AsynchronousChannelGroup group, Hashtable<Integer, AsynchronousSocketChannel> routingTable) {
        this.group = group;
        this.routingTable = routingTable;
    }

    @Override
    public void run() {
        try {
            System.out.println("Broker Server is ready");
            AsynchronousServerSocketChannel brokerserver = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5000));
            brokerserver.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel clientSocket, Object attachment) {
                    try {
                        System.out.println("We have a new client\tRemote: " + clientSocket.getRemoteAddress() + "\tLocal: " + clientSocket.getLocalAddress() + "\tclient: " + clientSocket);
                        RouterMessageHandler broker = new RouterMessageHandler(clientSocket, ++brokerID);

                        new Thread(broker).start();

                        if (routingTable.contains(clientSocket)) {
                            System.out.println("Client already exists");
                            broker.sendMessage(routingTable.get(0));
                        } else {
                            routingTable.put(brokerID, clientSocket);
                            broker.sendNewID();
                            System.out.println("New client added");
                        }
//                        broker.readMessage();
//                        if (!broker.firstConnection) {
//                            if (routingTable.contains(0)) {
//                                System.out.println("Market not in routing table");
//                            } else {
//                                System.out.println("Sending market a message");
//                                broker.sendMessage(routingTable.get(0));
//                            }
//                        } else {
//                            System.out.println("Adding broker to routing table");
//                            routingTable.put(brokerID, clientSocket);
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    brokerserver.accept(null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
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

}
