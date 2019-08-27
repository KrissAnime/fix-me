package com.wethinkcode.fixme.market;

import com.wethinkcode.fixme.market.ChannelDetails;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Market {
    String name;
    int marketID;
    public AsynchronousSocketChannel client;
    public int clientId;
    public ByteBuffer buffer;
    public Thread mainThread;
    public boolean isRead;
    public ChannelDetails channelDetails = new ChannelDetails();

    List<Map<String, String>> instruments = new ArrayList<Map<String, String>>();
    public Market(String name){
        this.name = name;
        initInstruments();
//        System.out.println(instruments.get(2).get("name"));
//        System.out.println(this.name);
        try {
            RouterConnection("localhost", 5001);
        } catch (IOException e) {
            System.out.println("1");
            e.printStackTrace();
            System.exit(1);
        } catch (ExecutionException e) {
            System.out.println("connection failed, router down");
//            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("111");
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void initInstruments(){
        for (int i = 0; i < 3; i++){
            Map<String,String> instrument = new HashMap<String, String>();
            if (i == 0) {
                instrument.put("name", "USD");
                instrument.put("qty", "10");
                instrument.put("price", "6");
                instruments.add(instrument);
            } else if (i == 1){
                instrument.put("name", "EUR");
                instrument.put("qty", "10");
                instrument.put("price", "3");
                instruments.add(instrument);
            } else if ( i == 2){
                instrument.put("name", "ZAR");
                instrument.put("qty", "100");
                instrument.put("price", "10");
                instruments.add(instrument);
            }
        }
    }

    public void RouterConnection(String host, int port) throws IOException, ExecutionException, InterruptedException {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress address = new InetSocketAddress(host, port);
        Future<Void> future = channel.connect(address);
        future.get();

        this.channelDetails.socketChannel = channel;
        this.channelDetails.readStatus = 1;
        this.channelDetails.mainThread = Thread.currentThread();
        this.channelDetails.byteBuffer = ByteBuffer.allocate(2048);
        ReadHandler readHandler = new ReadHandler();
        channel.read(this.channelDetails.byteBuffer, channelDetails, readHandler);

        try
        {
            Thread.currentThread().join();
            System.out.println("Joined thread");
        }
        catch(Exception e)
        {

        }
        System.out.println("End of program?");

    }


}
