package com.wethinkcode.fixme.router;

import com.wethinkcode.fixme.router.models.FIXMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String args[]) {

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/

        FIXMessage fixMessage = new FIXMessage();
        System.out.println("toString return " + fixMessage.toString());

//        try {
//
//            System.out.println("Starting up Server...");
//            Selector selector = Selector.open();
//            ServerSocketChannel server = ServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5000));
//            ByteBuffer buffer = ByteBuffer.allocate(2048);
//            server.configureBlocking(false);
//            server.register(selector, SelectionKey.OP_ACCEPT);
//
//            System.out.println("Waiting for client connection");
//            while (true) {
//                selector.select();
//                Set<SelectionKey> selectionKeys = selector.selectedKeys();
//                Iterator<SelectionKey> iterator = selectionKeys.iterator();
//                while (iterator.hasNext()) {
//                    buffer.clear();
//                    SelectionKey key = iterator.next();
//
//                    if (key.isAcceptable()) {
//                        registerClient(selector, server);
//                    }
//
//                    if (key.isReadable()) {
//                        readClientMessage(buffer, key);
//                    }
//
//                    if (key.isWritable()) {
//                        answerClient(buffer, key);
//                    }
//
//                    iterator.remove();
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("The selector had in IOException");
//            e.printStackTrace();
//        }

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
    }

    private static void registerClient(Selector selector, ServerSocketChannel server) throws IOException {
        SocketChannel client = server.accept();
        System.out.println("Client Connected " + client.getRemoteAddress());
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        Sleep(1);
    }

//    private static void readClientMessage(ByteBuffer buffer, SelectionKey key) throws IOException {
//        SocketChannel client = (SocketChannel)key.channel();
//        System.out.println("Reading client Message");
//        client.read(buffer);
//        if (new String(buffer.array()).trim().equals("Exit")) {
//            client.close();
//            System.out.println("Disconnecting from the client");
//        } else {
//            FIXMessage fixMessage = new FIXMessage(new String(buffer.array()).trim());
//            System.out.println("Client says " + fixMessage.toString());
//        }
//        Sleep(3);
//    }

    private static void answerClient(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel)key.channel();
        buffer.clear();
        System.out.println("Send client message");
        buffer = ByteBuffer.wrap("I am the server".getBytes());
        client.write(buffer);
        buffer.flip();
        buffer.clear();
        Sleep(4);
    }

    private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
