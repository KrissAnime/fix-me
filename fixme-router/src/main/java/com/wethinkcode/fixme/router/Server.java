package com.wethinkcode.fixme.router;

import com.wethinkcode.fixme.router.models.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Hashtable;
import java.util.concurrent.*;

public class Server {
    static int brokerID = 99999;
    final int routerID = 984312;

    /**
     * Hashmap is not thread safe whereas hashtable is synchronized
     */
    static Hashtable<Integer, String> routingTable = new Hashtable<>();

    static final String[] randomResponses = {
            "This is the server",
            "Server system logs can be accessed via log file",
            "Testing server system buffer manager"};

    public static void main(String args[]) {

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/

//        FIXMessage fixMessage = new FIXMessage();
//        System.out.println("toString return " + fixMessage.toString());

        try {



//            FIXMessage fixMessage1 = new FIXMessage("8=FIX.4.4|9=106|35=A|34=1|49=CSERVER|50=TRADE|52=20170117- 08:03:04.509|56=theBroker.12345|57=any_string|98=0|108=30|141=Y|10=066|");

//            FIXMessage fixMessage2 = new FIXMessage("8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|");
//            int test = getID();


//            System.out.println("First Message " + fixMessage1.toJSONString());
//            System.out.println("Second Message " + fixMessage2.toJSONString());


            /**
             * Sidelined code until later use
             */
//            MessageDispatcher messageDispatcher = new MessageDispatcher();
            SQLite database = new SQLite();
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(3));
//
            BrokerServer brokerServer = new BrokerServer(group, database);
//            MarketServer marketServer = new MarketServer(group, messageDispatcher, database);
//
            new Thread(brokerServer).start();
//            new Thread(marketServer).start();
            /**
             * Sidelined code until later use
             */

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
    }

    private static void Sleep(long seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }

    private static final int getID() {
        return ++brokerID;
    }
}


