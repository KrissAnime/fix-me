package com.wethinkcode.fixme.router.models;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Hashtable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RouterMessageHandler implements Runnable {
    private @Setter int brokerID = 0;
    private @Setter final AsynchronousSocketChannel channel;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
//    private @Setter RouterMessageHandler next = null;
    String message;
    boolean firstConnection;
    private @Setter @Getter Integer messageDestination;
    Hashtable<Integer, AsynchronousSocketChannel> routingTable;



    public RouterMessageHandler(AsynchronousSocketChannel channel, int brokerID, Hashtable<Integer, AsynchronousSocketChannel> routingTable) throws InterruptedException {
        this.channel = channel;
        this.brokerID = brokerID;
        this.routingTable = routingTable;
    }

    @Override
    public void run() {
        try {
            readMessage();
//            sendNewID();
//            buffer.clear();
//            readMessage();
//            Sleep(2);
//            sendNewID();
//            FIXMessage fixMessage = new FIXMessage();
//            fixMessage.UnMarshallMessage("8=FIX.4.4|9=106|35=A|34=1|52=20170117- 08:03:04.509|98=0|108=30|141=Y|10=066|");
//            sendMessage(fixMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendNewID() {

        firstConnection = true;
//        System.out.println("Broker ID " + brokerID);
        channel.write(ByteBuffer.wrap(String.valueOf(brokerID).getBytes()));
//        message = "|50=00001|56=100000|MARKET=jse|55=ZAR|38=1|44=1.9|54=1|10=066";

        //        Sleep(3);
    }

    public void readMessage() throws InterruptedException {
        System.out.println("Attempting to read message");

//        System.out.println("Reading broker message");

        Future<Integer> future = channel.read(buffer);

        while (!future.isDone()) {
//            System.out.println("Waiting for message to be received...");
            Thread.sleep(250);
        }

        message = new String(buffer.array()).trim();
        buffer.clear();
        System.out.println("Message from client\t" + message);

        if (message.equals("New Connection")) {
            firstConnection = true;
            sendNewID();
            readMessage();
//            Thread.sleep(9000);
//            message = "|50=00001|56=100000|MARKET=jse|55=ZAR|38=1|44=1.9|54=1|10=066";
        } else {

            message = new String(buffer.array()).trim();
            if (message.contains("50=") && message.contains("56=")) {
                System.out.println("message = " + message);
                String[] test = message.split("|");
//                messageDestination = Integer.parseInt(message.split("\\|")[0].split("=")[1]);
                System.out.println("First split " + test[0]);
                System.out.println("Second split " + test[1]);
//                sendMessage(routingTable.get(messageDestination), message);
            } else {
                System.out.println("Sending to market...");
                sendMessage(routingTable.get(0), message);
            }
            firstConnection = false;
        }
    }

    public void sendMessage(AsynchronousSocketChannel destination, String message) throws InterruptedException {
        Future<Integer> future = destination.write(ByteBuffer.wrap(message.getBytes()));
        while (!future.isDone()) {
            System.out.println("Waiting for broker message to be sent...");
            Thread.sleep(250);
        }
    }

//    private boolean checkBroker(AsynchronousSocketChannel broker, int brokerID) {
//        ++brokerID;
//        if (this.channel == null) {
//            System.out.println("Creating first broker");
////            setBroker(broker);
////            setBrokerID(brokerID);
//            return true;
//        } else if (brokerID == this.brokerID && this.channel == broker) {
//            return true;
//        } else {
//            next.checkBroker(broker, brokerID);
//        }
//        return false;
//    }

//    private void createNextBroker(AsynchronousSocketChannel broker) throws InterruptedException {
//        System.out.println("Creating next broker");
//        setNext(new RouterMessageHandler(broker, ++brokerID));
////        FIXMessage fixMessage = new FIXMessage();
////        fixMessage.setDestinationID(brokerID);
//    }

    void Sleep(long time) {
        try {
            System.out.println("Sleeping for " + time + " seconds...");
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean ValidateMessage(String message) {

        return true;
    }
}
