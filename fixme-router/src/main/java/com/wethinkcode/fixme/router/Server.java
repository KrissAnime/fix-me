package com.wethinkcode.fixme.router;

import com.wethinkcode.fixme.router.models.FIXMessage;
import com.wethinkcode.fixme.router.models.MessageDispatcher;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    static int brokerID = 99999;

    static final String[] randomResponses = {
            "This is the server",
            "Server system logs can be accessed via log file",
            "Testing server system buffer manager"};

    public static void main(String args[]) {

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/

//        FIXMessage fixMessage = new FIXMessage();
//        System.out.println("toString return " + fixMessage.toString());
        MessageDispatcher messageDispatcher = new MessageDispatcher();

        try {
            SQLite database = new SQLite();
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(3));

            BrokerServer brokerServer = new BrokerServer(group, messageDispatcher, database);
//            MarketServer marketServer = new MarketServer(group, messageDispatcher);

            new Thread(brokerServer).start();
//            new Thread(marketServer).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
    }

    private static void Sleep(long seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }

    private static int setBrokerID() {
        return ++brokerID;
    }
}

class BrokerServer implements Runnable {
    AsynchronousChannelGroup group;
    ByteBuffer buffer = ByteBuffer.allocate(2048);

    List<SocketAddress> clientAddresses;
    MessageDispatcher messageDispatcher = new MessageDispatcher();

    public BrokerServer(AsynchronousChannelGroup group, MessageDispatcher messageDispatcher, SQLite database) {
        this.group = group;
        this.messageDispatcher = messageDispatcher;
        this.database = database;
    }

    @Override
    public void run() {
        try {
            System.out.println("Broker Server is ready");
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5000));
            buffer = ClearBuffer(buffer);

            //Accept message to be passed on
            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel clientSocket, Object attachment) {
                    synchronized (messageDispatcher) {
                        try {
                            messageDispatcher.AddAddress(clientSocket);
                            clientSocket.read(buffer);
                            System.out.println(new String(buffer.array()).trim());
                            FIXMessage fixMessage = new FIXMessage(new String(buffer.array()).trim());
                            if (fixMessage.getMarshallMessage() == null) {
                                System.out.println("Invalid message or bad format");
                            } else {
                                System.out.println("Message received " + fixMessage.toJSONString());
                                synchronized (database) {
                                    try {
                                        database.SaveTransaction(fixMessage);
                                        System.out.println("Saved transaction");
                                    } catch (SQLException e) {
                                        System.out.println("There was an error with the database");
                                        e.printStackTrace();
                                    }

                                FIXMessage fixMessage = new FIXMessage(new String(buffer.array()).trim());

                                messageDispatcher.AddBrokerAddress(fixMessage.getRoutingSenderID(), clientSocket);

                                fixMessage = messageDispatcher.SendMessageToMarket(fixMessage);

                                messageDispatcher.SendMessageToBroker(fixMessage);
//                                buffer.flip();
//                                buffer = ClearBuffer(buffer);
//                                System.out.println("Broker Buffer " + new String(buffer.array()).trim());
//                                System.out.println("Broker attachment" + attachment);
//                                future = clientSocket.write(ByteBuffer.wrap(fixMessage.MarshallMessage().getBytes()));



                                while (!future.isDone()) {
                                    System.out.println("Waiting for FIX message to be sent back to broker...");
                                    Thread.sleep(200);
                                }

//                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (new String(buffer.array()).trim().length() != 0) {
                        System.out.println("Buffer has array " + new String(buffer.array()).trim().length() + " " + new String(buffer.array()).trim());
//                        System.out.println("Buffer has array " + buffer.array().length);
                        buffer = ClearBuffer(buffer);
                    } else {
                        System.out.println("Buffer is empty");
                    }
//                    buffer = ClearBuffer(buffer);
//                    System.out.println("String version " + attachment.toString());
                    server.accept(null, this);
//                    FIXMessage fixMessage = new FIXMessage(new String());
                };

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
        }
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
}

class MarketServer implements Runnable {
    AsynchronousChannelGroup group;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    List<SocketAddress> clientAddresses;

    public MarketServer(AsynchronousChannelGroup group, List<SocketAddress> clientAddresses) {
        this.group = group;
        this.clientAddresses = clientAddresses;
    }

    @Override
    public void run() {
        try {
            System.out.println("Market Server is ready");
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5001));
            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel clientSocket, Object attachment) {
                    synchronized (clientAddresses) {
                        try {
                            if (!clientAddresses.contains(clientSocket.getLocalAddress())) {
                                clientAddresses.add(clientSocket.getLocalAddress());
                                System.out.println("Adding market client " + clientSocket.getLocalAddress());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    clientSocket.read(buffer);
                    System.out.println("Market Buffer " + new String(buffer.array()).trim());
                    buffer.flip();
                    buffer = ClearBuffer(buffer);
                    System.out.println("Market attachment" + attachment);
                    clientSocket.write(ByteBuffer.wrap("This is the server to market".getBytes()));

//                    System.out.println("String version " + attachment.toString());
                    server.accept(null, this);
//                    FIXMessage fixMessage = new FIXMessage(new String());
                };

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("There was an error");

                }
            });
            group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (IOException e) {
            System.out.println("Error starting market server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Market server was interrupted");
            e.printStackTrace();
        }
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
}


