package com.wethinkcode.fixme.router.models;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class MessageDispatcher {
    Map<String, AsynchronousSocketChannel> marketAddresses = new HashMap<>();
    Map<String, AsynchronousSocketChannel> brokerAddresses = new HashMap<>();

    public MessageDispatcher(){}

    public void AddMarketAddress(String address, AsynchronousSocketChannel market) {
        if (!marketAddresses.containsValue(market)){
            marketAddresses.put(address, market);
        }
    }

    public void RemoveMarketAddress(String address) throws IOException {
        marketAddresses.get(address).close();
        marketAddresses.remove(address);
    }

    public void AddBrokerAddress(String address, AsynchronousSocketChannel market) { brokerAddresses.put(address, market); }

    public void RemoveBrokerAddress(String address) throws IOException {
        brokerAddresses.get(address).close();
        brokerAddresses.remove(address);
    }

    public FIXMessage SendMessageToMarket(FIXMessage fixMessage) throws IOException, InterruptedException {
        System.out.println("Sending message to market");
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        AsynchronousSocketChannel destination = marketAddresses.get(fixMessage.getRoutingReceiverID());
        destination.write(ByteBuffer.wrap(fixMessage.MarshallMessage().getBytes()));

        Future<Integer> result = destination.read(buffer);
        while (!result.isDone()) {
            System.out.println("Waiting on response from market... " + destination.getRemoteAddress());
            Thread.sleep(200);
        }
        System.out.println("Message received from the market...\t" + new String(buffer.array()).trim());
        return new FIXMessage(new String(buffer.array()).trim());
    }

    public void SendMessageToBroker(FIXMessage fixMessage) throws InterruptedException {
        AsynchronousSocketChannel destination = brokerAddresses.get(fixMessage.getRoutingReceiverID());
        Future<Integer> future = destination.write(ByteBuffer.wrap(fixMessage.MarshallMessage().getBytes()));

        while (!future.isDone()) {
            System.out.println("Attempting to send message to the broker...");
            Thread.sleep(200);
        }
        System.out.println("Message was successfully sent to the broker");
    }
}
