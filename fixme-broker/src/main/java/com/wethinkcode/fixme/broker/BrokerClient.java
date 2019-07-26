package com.wethinkcode.fixme.broker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class BrokerClient {
    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static BrokerClient broker = new BrokerClient();

    public static void main(String args[]) {
        try {
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
            String FixMessage = "49=601203|35=6|56=47569|MARKET=FOREX|SIDE=1|INSTRUMENT=EU/USD|PRICE=11.75|QUANTITY=0.8|10=123123";
            client = SocketChannel.open(new InetSocketAddress("localhost",5000));
            buffer = ByteBuffer.allocate(2048);

            while (true) {
                // Sending the message to the server;
                buffer = ByteBuffer.wrap(FixMessage.getBytes());
                client.write(buffer);
                buffer.flip();
                buffer.clear();
                try {
                    TimeUnit.SECONDS.sleep(10);
                    //Reading the response from the server
                    client.read(buffer);
                    System.out.println("Server response " + new String(buffer.array()).trim());
                    buffer.flip();
                    buffer.clear();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Broker attempting to connect to unknown host");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Broker exception during socket creation");
            e.printStackTrace();
        }
    }
}
