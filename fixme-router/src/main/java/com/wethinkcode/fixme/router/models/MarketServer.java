package com.wethinkcode.fixme.router.models;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

 /*** OLD VERSION OF SERVER ***/

public class MarketServer implements Runnable {
    AsynchronousChannelGroup group;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    MessageDispatcher messageDispatcher;
    SQLite database;

    public MarketServer(AsynchronousChannelGroup group, SQLite database) {
        this.group = group;
//        this.messageDispatcher = messageDispatcher;
        this.database = database;
    }

    @Override
    public void run() {
        try {
            System.out.println("Market Server is ready");
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5001));
            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel clientSocket, Object attachment) {
//                    synchronized (clientAddresses) {
//                        try {
//                            if (!clientAddresses.contains(clientSocket.getLocalAddress())) {
//                                clientAddresses.add(clientSocket.getLocalAddress());
//                                System.out.println("Adding market client " + clientSocket.getLocalAddress());
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
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
