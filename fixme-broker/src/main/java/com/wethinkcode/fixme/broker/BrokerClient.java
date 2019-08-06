package com.wethinkcode.fixme.broker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BrokerClient {
    private static SocketChannel client;
    private static ByteBuffer buffer = ByteBuffer.allocate(2048);
    private static BrokerClient broker = new BrokerClient();
    static final String[] randomResponses = {
            "This is broker"};
    static boolean connected = false;
    static String myAddress;
    public static void main(String args[]) throws InterruptedException, ExecutionException, TimeoutException {
//            senderID = lineSplit[0].split("=")[1];
//            message = lineSplit[1].split("=")[1];
//            targetID = lineSplit[2].split("=")[1];
//            market = lineSplit[3].split("=")[1];
//            if (message.equals("6")) {
//                order = lineSplit[4].split("=")[1];
//                instrument = lineSplit[5].split("=")[1];
//                price = Double.parseDouble(lineSplit[6].split("=")[1]);
//                quantity = Double.parseDouble(lineSplit[7].split("=")[1]);
//                checkSum = lineSplit[8].split("=")[1];
//            } else {
//                checkSum = lineSplit[4].split("=")[1];
//            }
//            String FixMessage = "49=601203|35=6|56=47569|MARKET=FOREX|SIDE=1|INSTRUMENT=EU/USD|PRICE=11.75|QUANTITY=0.8|10=123123";
//            String FixMessage2 = "8=FIX.4.4|9=126|35=A|49=theBroker.12345|56=CSERVER|34=1|52=20170117- 08:03:04|57=TRADE|50=any_string|98=0|108=30|141=Y|553=12345|554=passw0rd!|10=131|";
//            client = SocketChannel.open(new InetSocketAddress("localhost",5000));
//
//            while (true) {
//                // Sending the message to the server;
//                buffer = ClearBuffer(buffer);
//                buffer = ByteBuffer.wrap(randomResponses[ThreadLocalRandom.current().nextInt(0, randomResponses.length)].getBytes());
////                System.out.println("Message being written " + new String(buffer.array()).trim());
//                client.write(buffer);
////                buffer.clear();
//                buffer.flip();
////                Sleep(2);
//                    //Reading the response from the server
//                    buffer = ClearBuffer(buffer);
//                    client.read(buffer);
//                    System.out.println("Server response " + new String(buffer.array()).trim());
//                    buffer = ClearBuffer(buffer);
//                    buffer.flip();
////                    buffer = ClearBuffer(buffer);
//                Sleep(2);
//            }

            while (true) {
                try {

                    AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

                    client.connect(new InetSocketAddress(5000)).get(5, TimeUnit.SECONDS);

//                    if (!connected) {
//                        client.write(ByteBuffer.wrap("Connection Request".getBytes()));
//                        Sleep(7);
//                        connected = true;
//                    }

                    client.write(ByteBuffer.wrap("This is the broker".getBytes()));
                    Sleep(2);
                    client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            if (!connected) {
                                String[] address = new String(buffer.array()).trim().split(":");
                                myAddress = address[address.length - 1];
                                System.out.println("My address " + myAddress);
                                connected = true;
                                buffer = ClearBuffer(buffer);
                                Sleep(10);
                            }
                            System.out.println("Server said " + new String(buffer.array()).trim());
                            buffer.flip();
                            buffer = ClearBuffer(buffer);
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
