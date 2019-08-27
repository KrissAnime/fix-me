package com.wethinkcode.fixme.router.models;

import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MessageHandler implements Runnable {
    private @Setter
    int brokerID = 0;
    private @Setter
    final AsynchronousSocketChannel channel;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    private @Setter
    MessageHandler next = null;


    public MessageHandler(AsynchronousSocketChannel channel, int brokerID) throws InterruptedException {
        this.channel = channel;
        this.brokerID = brokerID;
        //        readMessage(broker, brokerID);
//        System.out.println("Created broker message handler");
    }

    @Override
    public void run() {
        try {
            sendNewID();
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

        channel.write(ByteBuffer.wrap(String.valueOf(brokerID).getBytes()));
//        Sleep(3);
    }

    public void readMessage() throws InterruptedException {
        System.out.println("Attempting to read message");

//        FIXMessage fixMessage;
//        if (!checkBroker(broker, brokerID)) {
//            createNextBroker(broker);
//            next.readMessage(broker, brokerID);
//        } else {
            System.out.println("Reading broker message");

            Future<Integer> future = channel.read(buffer);

            while (!future.isDone()) {
                System.out.println("Waiting for broker message to be received...");
                Thread.sleep(250);
            }

            System.out.println("Broker said " + new String(buffer.array()).trim());

//            fixMessage = new FIXMessage("");
//        }
//        return fixMessage;/
    }

    public void sendMessage(String message) throws InterruptedException {
//        if (brokerID == fixMessage.getRoutingReceiverID()) {

//        System.out.println("Sending " + fixMessage.MarshallMessage());
            Future<Integer> future = channel.write(ByteBuffer.wrap(message.getBytes()));
            while (!future.isDone()) {
                System.out.println("Waiting for broker message to be sent...");
                Thread.sleep(250);
            }
//        } else {
//            next.sendMessage(fixMessage);
//        }
    }

    private boolean checkBroker(AsynchronousSocketChannel broker, int brokerID) {
        ++brokerID;
        if (this.channel == null) {
            System.out.println("Creating first broker");
//            setBroker(broker);
//            setBrokerID(brokerID);
            return true;
        } else if (brokerID == this.brokerID && this.channel == broker) {
            return true;
        } else {
            next.checkBroker(broker, brokerID);
        }
        return false;
    }

    private void createNextBroker(AsynchronousSocketChannel broker) throws InterruptedException {
        System.out.println("Creating next broker");
        setNext(new MessageHandler(broker, ++brokerID));
//        FIXMessage fixMessage = new FIXMessage();
//        fixMessage.setDestinationID(brokerID);
    }

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
