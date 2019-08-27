package com.wethinkcode.fixme.router.models;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BrokerServer implements Runnable {
    AsynchronousChannelGroup group;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    SQLite database;
    Hashtable<Integer, AsynchronousSocketChannel> brokerMap = new Hashtable<>();
    static int brokerID = 99999;
//    BrokerMessageHandler brokerMessageHandler = new BrokerMessageHandler();

//    List<AsynchronousSocketChannel> brokerAddresses;
//    MessageDispatcher messageDispatcher;

    public BrokerServer(AsynchronousChannelGroup group, SQLite database) {
        this.group = group;
//        this.messageDispatcher = messageDispatcher;
        this.database = database;
    }

    @Override
    public void run() {
        try {
            System.out.println("Broker Server is ready");
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5001));
//            while (true) {
////            buffer = ClearBuffer(buffer);
//
//                Future<AsynchronousSocketChannel> client = server.accept();
//                System.out.println("We have a new client " + client.get().getRemoteAddress());
//                BrokerMessageHandler broker = new BrokerMessageHandler(client.get(), brokerID);
//
//                new Thread(broker).start();
//
//                group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//            }
//            server.accept(null, this);
            //Accept message to be passed on
            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel clientSocket, Object attachment) {
                    try {
                        System.out.println("We have a new client\tRemote: " + clientSocket.getRemoteAddress() + "\tLocal: " + clientSocket.getLocalAddress() + "\tclient: " + clientSocket);
                        BrokerMessageHandler broker = new BrokerMessageHandler(clientSocket, ++brokerID);

                        new Thread(broker).start();

//                        Future<Integer> future = clientSocket.read(buffer);
//
//                        while (!future.isDone()) {
//                            System.out.println("Waiting for broker message...");
//                            Thread.sleep(250);
//
//                        }

                        System.out.println(new String(buffer.array()).trim());

                        buffer.clear();

//                        System.out.println("Connection established " + clientSocket.getLocalAddress());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    server.accept(null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("There was an error");

                }
            });
            group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//            new BrokerMessageHandler(client.get(), brokerID++).start();
//            brokerMessageHandler.start().readMessage(client.get(), brokerID);
//            server.accept(null, null);
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
