package com.wethinkcode.fixme.router;

import com.wethinkcode.fixme.router.models.FIXMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Server {
    static int brokerID = 99999;
    enum BufferState {
        READ,
        WRITE;
    }
    static BufferState state = BufferState.READ;
    static HashMap<SocketAddress, FIXMessage> fixMessageHashMap = new HashMap<>();
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
            int ports = 5001 | 5000;
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(3));
            System.out.println("Starting server");
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(ports));
//            server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(5001));
            //            AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withFixedThreadPool(2, );
//            server.bind(new InetSocketAddress(5001));
//            while (true) {
//                AsynchronousSocketChannel channel = server.accept().get();
//            }
            System.out.println(server.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//
//            System.out.println("Starting up Server...");
//            Selector selector = Selector.open();
//            ServerSocketChannel server = ServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5000));
//            server.bind(new InetSocketAddress(5001));
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
//                Sleep(2);
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
        int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        System.out.println("Client Connected " + client.getRemoteAddress());
        client.configureBlocking(false);
        client.register(selector, interestSet, client.getRemoteAddress());
        Sleep(1);
    }

    private static void readClientMessage(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel)key.channel();
        System.out.println("Reading client Message  of " + key.attachment().toString());
//        System.out.println("Client says before clear " + new String(buffer.array()).trim());
        buffer = ClearBuffer(buffer);
        if (state != BufferState.READ) {
            buffer.flip();
            state = BufferState.READ;
        }
        client.read(buffer);
        if (new String(buffer.array()).trim().equals("Exit")) {
            System.out.println("Disconnecting from the client with ID " + key.attachment());
            client.close();
        } else {
            FIXMessage fixMessage = new FIXMessage(new String(buffer.array()).trim());
            fixMessageHashMap.putIfAbsent(client.getRemoteAddress(), fixMessage);
            System.out.println("Client says " + new String(buffer.array()).trim());
        }
//        ClearBuffer(buffer);
//        Sleep(2);
    }

    private static void answerClient(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel)key.channel();
        if (fixMessageHashMap.containsKey(client.getRemoteAddress())) {
//            System.out.println("Before answer clearing " + new String(buffer.array()).trim());
            buffer = ClearBuffer(buffer);
            if (state != BufferState.WRITE) {
                buffer.flip();
                state = BufferState.WRITE;
            }
//            System.out.println("After answer clearing " + new String(buffer.array()).trim());
//        System.out.println("Send client message");
//        System.out.println("Stringed buffer " + new String(buffer.array()).trim());
            buffer = ByteBuffer.wrap(randomResponses[ThreadLocalRandom.current().nextInt(0, randomResponses.length)].getBytes());
            client.write(buffer);
        }
//        System.out.println(fixMessageHashMap.toString());
//        ClearBuffer(buffer);
//        Sleep(2);
    }

    private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int setBrokerID() {
        return ++brokerID;
    }

    private static ByteBuffer ClearBuffer(ByteBuffer buffer) {
        buffer.clear();
        buffer.put(new byte[2048]);
        buffer.clear();
        return buffer;
    }
}
