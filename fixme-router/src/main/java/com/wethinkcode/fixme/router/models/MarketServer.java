package com.wethinkcode.fixme.router.models;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

 /*** OLD VERSION OF SERVER ***/

 public class MarketServer implements Runnable {
     AsynchronousChannelGroup group;
     Hashtable<Integer, AsynchronousSocketChannel> routingTable;
     static int brokerID = 0;


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
                         MessageHandler broker = new MessageHandler(clientSocket, brokerID);

                         new Thread(broker).start();


                         //                         broker.readMessage();
                         if (!routingTable.contains(clientSocket)) {
                             System.out.println("Adding market to routing table");
                             routingTable.put(brokerID, clientSocket);
//
                         }

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
         }/* catch (ExecutionException e) {
            e.printStackTrace();
        }*/



     }

     private static ByteBuffer ClearBuffer(ByteBuffer buffer) {
//        System.out.println("Before clear buffer" + new String(buffer.array()).trim());
         buffer.clear();
         buffer = ByteBuffer.allocate(2048);
         buffer.clear();
         buffer.put(new byte[2048]);
         buffer.clear();
//        System.out.println("After clear buffer" + new String(buffer.array()).trim());
         return buffer;
     }

     private static int setBrokerID() {
         return ++brokerID;
     }

     private static void Sleep(long seconds) throws InterruptedException {
         TimeUnit.SECONDS.sleep(seconds);
     }
 }
