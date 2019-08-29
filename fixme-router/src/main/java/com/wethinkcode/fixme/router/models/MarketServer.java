package com.wethinkcode.fixme.router.models;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

 public class MarketServer implements Runnable {
     AsynchronousChannelGroup group;
     Hashtable<Integer, AsynchronousSocketChannel> routingTable;
     static int brokerID = 999999;

     public MarketServer(AsynchronousChannelGroup group, Hashtable<Integer, AsynchronousSocketChannel> routingTable) {
         this.group = group;
         this.routingTable = routingTable;
     }

     @Override
     public void run() {
         try {
             System.out.println("Market Server is ready");
             AsynchronousServerSocketChannel marketserver = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5001));
             marketserver.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                 @Override
                 public void completed(AsynchronousSocketChannel clientSocket, Object attachment) {
                     try {
                         System.out.println("We have a new market client\tRemote: " + clientSocket.getRemoteAddress() + "\tLocal: " + clientSocket.getLocalAddress() + "\tclient: " + clientSocket);
                         RouterMessageHandler market = new RouterMessageHandler(clientSocket, brokerID);

                         new Thread(market).start();

                         if (!routingTable.contains(clientSocket)) {
                             System.out.println("Adding market to routing table");
                             market.sendNewID();
                             routingTable.put(0, clientSocket);
                         }
                         System.out.println("message " + market.message);
                         TimeUnit.SECONDS.sleep(3);
                         /**]
                          * MARKET = 0
                          * BROKER = 100000
                          */
//                         market.sendMessage(routingTable.get(0));


                     } catch (Exception e) {
                         e.printStackTrace();
                     }

                     marketserver.accept(null, this);
                 }

                 @Override
                 public void failed(Throwable exc, Object attachment) {
                     System.out.println("There was an error");

                 }
             });
             group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
         } catch (IOException e) {
             System.out.println("Error starting broker server");
             e.printStackTrace();
         } catch (InterruptedException e) {
             System.out.println("Broker server was interrupted");
             e.printStackTrace();
         }
     }
 }
