package com.wethinkcode.fixme.router.models;

import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class MessageDispatcher {

    Future<Integer> result = null;
    private @Setter
    MessageDispatcher next = null;

    //    Map<String, AsynchronousSocketChannel> marketAddresses = new HashMap<>();
//    Map<String, AsynchronousSocketChannel> brokerAddresses = new HashMap<>();
//
    public MessageDispatcher(AsynchronousSocketChannel client, FIXMessage fixMessage) throws Exception {
        if (result == null) {
            SendMessage(fixMessage, client);
        }
    }

    //
//    public void AddMarketAddress(String address, AsynchronousSocketChannel market) {
//        if (!marketAddresses.containsValue(market)){
//            marketAddresses.put(address, market);
//        }
//    }
//
//    public void RemoveMarketAddress(String address) throws IOException {
//        marketAddresses.get(address).close();
//        marketAddresses.remove(address);
//    }
//
//
//    public void AddBrokerAddress(String address, AsynchronousSocketChannel market) { brokerAddresses.put(address, market); }
//
//    public void RemoveBrokerAddress(String address) throws IOException {
//        brokerAddresses.get(address).close();
//        brokerAddresses.remove(address);
//    }
//
//    public FIXMessage SendMessageToMarket(FIXMessage fixMessage) throws IOException, InterruptedException {
//        System.out.println("Sending message to market");
//        ByteBuffer buffer = ByteBuffer.allocate(2048);
//
//        AsynchronousSocketChannel destination = marketAddresses.get(fixMessage.getRoutingReceiverID());
//        destination.write(ByteBuffer.wrap(fixMessage.MarshallMessage().getBytes()));
//
//        Future<Integer> result = destination.read(buffer);
//        while (!result.isDone()) {
//            System.out.println("Waiting on response from market... " + destination.getRemoteAddress());
//            Thread.sleep(200);
//        }
//        System.out.println("Message received from the market...\t" + new String(buffer.array()).trim());
//        return new FIXMessage(new String(buffer.array()).trim());
//    }
//
    void SendMessage(FIXMessage fixMessage, AsynchronousSocketChannel client) throws Exception {
//        AsynchronousSocketChannel destination = brokerAddresses.get(fixMessage.getRoutingReceiverID());
        result = client.write(ByteBuffer.wrap(fixMessage.MarshallMessage().getBytes()));

//        while (!future.isDone()) {
//            System.out.println("Attempting to send message to the broker...");
//            Thread.sleep(200);
//        }
//        System.out.println("Message was successfully sent to the broker");
        if (result.isDone()) {
            result = null;
        }
    }
}
