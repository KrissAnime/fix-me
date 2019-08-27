package com.wethinkcode.fixme.broker;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class BrokerClient {


    public static void main(String args[]) throws Exception {

        new TestClient("This is broker").start();

//        TestClient testClient1 = new TestClient("This is broker 1");
//        TestClient testClient2 = new TestClient("This is broker 2");

        System.out.println("Broker started");
//        testClient1.start();
//        testClient1.run();
//        new Thread(testClient1).start();

//        TimeUnit.SECONDS.sleep(10);
//        new TestClient("This is broker 2nd").start();
//
//        System.out.println("Second broker started");
//        testClient2.start();
//        new Thread(testClient2).start();
    }
}

class TestClient extends Thread {
    private static SocketChannel client;
    private static ByteBuffer buffer = ByteBuffer.allocate(2048);
    private static BrokerClient broker = new BrokerClient();
    static String message;
    static boolean connected = false;
    static String myAddress = null;

    public TestClient(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        while (true) {
            try {

                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

                client.connect(new InetSocketAddress("localhost", 5000)).get(5, TimeUnit.SECONDS);
                System.out.println("Client connect " + client.getRemoteAddress());

//                    if (!connected) {
//                        client.write(ByteBuffer.wrap("Connection Request".getBytes()));
//                        Sleep(7);
//                        connected = true;
//                    }
                if (myAddress == null) {
                    client.write(ByteBuffer.wrap(message.getBytes()));
                } else {

                    client.write(ByteBuffer.wrap(message.getBytes()));
                }
                Sleep(5);
                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        if (!connected) {
//                            String[] address = new String(buffer.array()).trim().split(":");
                            myAddress = new String(buffer.array()).trim();
                            System.out.println("My address " + myAddress);
                            connected = true;
                            buffer = ClearBuffer(buffer);
                            Sleep(10);
                        }
                        System.out.println("Server said " + new String(buffer.array()).trim());
                        buffer.flip();
                        buffer = ClearBuffer(buffer);
                        Sleep(8);

                        client.read(buffer, buffer, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            client.close();
                            System.exit(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (UnknownHostException e) {

                System.out.println("Market attempting to connect to unknown host");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Market exception during socket creation");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException exception during socket creation");
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("TimeoutException exception during socket creation");
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("ExecutionException exception during socket creation");
                e.printStackTrace();
            }
            Sleep(10);
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

    private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
