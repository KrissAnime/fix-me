package com.wethinkcode.fixme.router.models;

import lombok.Setter;
import org.omg.CORBA.INITIALIZE;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class BrokerMessageHandler {
    @Setter
    int brokerID = 0;
    @Setter
    AsynchronousSocketChannel broker = null;
    ByteBuffer buffer = ByteBuffer.allocate(2048);
    private @Setter
    BrokerMessageHandler next = null;

    public BrokerMessageHandler() {
        System.out.println("Created broker message handler");
    }

    public void readMessage(AsynchronousSocketChannel broker, int brokerID) throws InterruptedException {
        System.out.println("Attempting to read message");

//        FIXMessage fixMessage;
        if (!checkBroker(broker, brokerID)) {
            createNextBroker(broker);
            next.readMessage(broker, brokerID);
        } else {
            System.out.println("Reading broker message");

            Future<Integer> future = broker.read(buffer);

            while (!future.isDone()) {
                System.out.println("Waiting for broker message to be received...");
                Thread.sleep(250);
            }

            System.out.println("Broker said " + new String(buffer.array()).trim());

//            fixMessage = new FIXMessage("");
        }
//        return fixMessage;/
    }

    public void sendMessage(FIXMessage fixMessage) throws InterruptedException {
        if (brokerID == fixMessage.getRoutingReceiverID()) {
            Future<Integer> future = broker.write(ByteBuffer.wrap(fixMessage.MarshallMessage().getBytes()));

            while (!future.isDone()) {
                System.out.println("Waiting for broker message to be sent...");
                Thread.sleep(250);
            }
        } else {
            next.sendMessage(fixMessage);
        }
    }

    private boolean checkBroker(AsynchronousSocketChannel broker, int brokerID) {
        ++brokerID;
        if (this.broker == null) {
            System.out.println("Creating first broker");
            setBroker(broker);
            setBrokerID(brokerID);
            return true;
        } else if (brokerID == this.brokerID && this.broker == broker) {
            return true;
        } else {
            next.checkBroker(broker, brokerID);
        }
        return false;
    }

    private void createNextBroker(AsynchronousSocketChannel broker) {
        System.out.println("Creating next broker");
        setNext(new BrokerMessageHandler());
        next.setBroker(broker);
        next.setBrokerID(brokerID);
        FIXMessage fixMessage = new FIXMessage("");
        fixMessage.setRoutingReceiverID(brokerID);
    }
}
