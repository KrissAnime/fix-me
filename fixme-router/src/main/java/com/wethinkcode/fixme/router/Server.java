package com.wethinkcode.fixme.router;

import com.wethinkcode.fixme.router.models.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Hashtable;
import java.util.concurrent.*;

public class Server {
    /**
     * Hashmap is not thread safe whereas hashtable is synchronized
     */
    static Hashtable<Integer, AsynchronousSocketChannel> routingTable = new Hashtable<>();

    public static void main(String args[]) {

        try {

            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
            MarketServer marketServer = new MarketServer(group, routingTable);
            BrokerServer brokerServer = new BrokerServer(group, routingTable);
//
            new Thread(brokerServer).start();
            new Thread(marketServer).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


