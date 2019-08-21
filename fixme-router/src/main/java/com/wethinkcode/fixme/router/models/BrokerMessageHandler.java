package com.wethinkcode.fixme.router.models;

import lombok.Setter;
import org.omg.CORBA.INITIALIZE;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BrokerMessageHandler implements Runnable {
    private @Setter
    int brokerID = 0;
    private @Setter
    final AsynchronousSocketChannel broker;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    private @Setter
    BrokerMessageHandler next = null;


    public BrokerMessageHandler(AsynchronousSocketChannel broker, int brokerID) throws InterruptedException {
        this.broker = broker;
        this.brokerID = brokerID;
        //        readMessage(broker, brokerID);
//        System.out.println("Created broker message handler");
    }

    @Override
    public void run() {
        try {
            sendNewID();
            readMessage();
            Sleep(8);
            FIXMessage fixMessage = new FIXMessage();
            fixMessage.UnMarshallMessage("8=FIX.4.4|9=106|35=A|34=1|52=20170117- 08:03:04.509|98=0|108=30|141=Y|10=066|");
            sendMessage(fixMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void sendNewID() {
        broker.write(ByteBuffer.wrap(String.valueOf(brokerID).getBytes()));
        Sleep(3);
    }

    public void readMessage() throws InterruptedException {
        System.out.println("Attempting to read message");

//        FIXMessage fixMessage;
//        if (!checkBroker(broker, brokerID)) {
//            createNextBroker(broker);
//            next.readMessage(broker, brokerID);
//        } else {
            System.out.println("Reading broker message");

            Future<Integer> future = broker.read(buffer);

            while (!future.isDone()) {
                System.out.println("Waiting for broker message to be received...");
                Thread.sleep(250);
            }

            System.out.println("Broker said " + new String(buffer.array()).trim());

//            fixMessage = new FIXMessage("");
//        }
//        return fixMessage;/
    }

    public void sendMessage(FIXMessage fixMessage) throws InterruptedException {
//        if (brokerID == fixMessage.getRoutingReceiverID()) {

        System.out.println("Sending " + fixMessage.MarshallMessage());
            Future<Integer> future = broker.write(ByteBuffer.wrap(fixMessage.MarshallMessage().getBytes()));

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
        if (this.broker == null) {
            System.out.println("Creating first broker");
//            setBroker(broker);
//            setBrokerID(brokerID);
            return true;
        } else if (brokerID == this.brokerID && this.broker == broker) {
            return true;
        } else {
            next.checkBroker(broker, brokerID);
        }
        return false;
    }

    private void createNextBroker(AsynchronousSocketChannel broker) throws InterruptedException {
        System.out.println("Creating next broker");
        setNext(new BrokerMessageHandler(broker, ++brokerID));
        FIXMessage fixMessage = new FIXMessage();
        fixMessage.setRoutingReceiverID(brokerID);
    }

    void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
