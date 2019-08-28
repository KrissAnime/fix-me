package com.wethinkcode.fixme.broker.controller;

import com.wethinkcode.fixme.broker.model.BrokerInstruments;
import com.wethinkcode.fixme.broker.model.FIXMessage;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Broker {
    private static ByteBuffer buffer = ByteBuffer.allocate(2048);
    static boolean connected = false;
    static String idAddress;
    private BrokerInstruments brokerInstruments;


    public Broker() {
        try
        {
            this.brokerInstruments = new BrokerInstruments();
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            client.connect(new InetSocketAddress("localhost", 5000)).get(5, TimeUnit.SECONDS);
            getBrokerId(client);
            sendMessage(client);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("No connection made, no router");
           // e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void getBrokerId(AsynchronousSocketChannel client) {
        Future result = client.read(buffer);
        if (result.isDone()) {
            if (!connected) {
                System.out.println("... ");
                idAddress = new String(buffer.array()).trim();
                System.out.println("Broker id = "+ idAddress);
                connected = true;
                Sleep(10);
            }
        }
    }
    public void readMessage(AsynchronousSocketChannel client) {
        Future result = client.read(buffer);
        if (!result.isDone()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                System.out.println("Sleep");
                // e.printStackTrace();
                System.exit(1);
            }
        }
        FIXMessage fixMessage = new FIXMessage(new String(buffer.array()).trim());
        if (fixMessage.getStatus() == "8") {
            brokerInstruments.AddInstrument(fixMessage.getOrderQuantity(), fixMessage.getPrice(), fixMessage.getSymbol());
        }
        System.out.println("System response "+ new String(buffer.array()).trim());
    }

    public void sendMessage(AsynchronousSocketChannel client) {
        String fixMessage = null;
        String [] messages = new String [] {"50="+idAddress+"|MARKET=jse|54=2|55=zar|44=1.8|38=2", "50="+idAddress+"|35=D|MARKET=jse|54=2|55=usd|44=11.3|38=2"};
        for (int i = 0; i < messages.length; i++) {
//            fixMessage = new FIXMessage(messages[i]).MarshallMessage();
//            byte [] message = fixMessage.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(messages[0].getBytes());
            Future result = client.write(buffer);
            if (result.isDone()) {
                buffer.clear();
                readMessage(client);
            }
            System.out.println(fixMessage);
            Sleep(4);
        }
    }

    private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
