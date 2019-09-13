package com.wethinkcode.fixme.router.models;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Hashtable;

public class BrokerMessager extends AbstractMessageHandler {

    @Override
    public void handleMessage(String message, Hashtable<Integer, AsynchronousSocketChannel> routingTable) throws Exception{
        if (message.contains("50=") && message.contains("56=")) {
            messageDestination = Integer.parseInt(message.split("\\|")[2].split("=")[1]);
            System.out.println("Sending to broker... " + message);
            sendMessage(routingTable.get(messageDestination), message);
        } else {

            next.handleMessage(message, routingTable);
//                validateMessage(message);
//            System.out.println("Sending to market... " + message);
//                sendMessage(routingTable.get(0), message);
        }
    }
}
