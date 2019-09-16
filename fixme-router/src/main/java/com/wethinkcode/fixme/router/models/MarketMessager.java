package com.wethinkcode.fixme.router.models;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Hashtable;

public class MarketMessager extends AbstractMessageHandler {

    @Override
    public void handleMessage(String message, Hashtable<Integer, AsynchronousSocketChannel> routingTable) throws Exception{
//        sendMessage(routingTable.get(messageDestination), message);
        if (!routingTable.contains(0)) {
            System.out.println("No market connected");
        } else {
            sendMessage(routingTable.get(0), message);
        }

    }
}
