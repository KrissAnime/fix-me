package com.wethinkcode.fixme.market.java;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Market {

    String name;
    public ChannelDetails channelDetails = new ChannelDetails();

    List<Map<String, String>> instruments = new ArrayList<Map<String, String>>();
    public Market(String name){
        this.name = name;
        try {
            RouterConnection("localhost", 5001);
        } catch (IOException e) {
            System.out.println("1");
            e.printStackTrace();
            System.exit(1);
        } catch (ExecutionException e) {
            System.out.println("connection failed, router down");
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("111");
            e.printStackTrace();
            System.exit(1);
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

        channel.write(ByteBuffer.wrap("New Connection".getBytes()));
        System.out.println("Message sent");

        ReadHandler readHandler = new ReadHandler();
        channel.read(this.channelDetails.byteBuffer, channelDetails, readHandler);

        try
        {
            Thread.currentThread().join();
        }
        catch(Exception e)
        {

        }

    }


}
