package com.wethinkcode.fixme.market;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class MarketClient {
    private static SocketChannel client;
    private static ByteBuffer buffer = ByteBuffer.allocate(2048);
    private static MarketClient marketClient = new MarketClient();
    static final String[] randomResponses = {
            "This is market 1",
            "This is market 2",
            "This is market 3"};

    public static void main(String args[]) throws InterruptedException, ExecutionException, TimeoutException {
        while (true) {
            try {
                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

                client.connect(new InetSocketAddress(5001)).get(5, TimeUnit.SECONDS);

                client.write(ByteBuffer.wrap("This is the market".getBytes()));
                Sleep(2);
                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        System.out.println("Server said " + new String(buffer.array()).trim());
                        buffer.flip();
                        buffer = ClearBuffer(buffer);
                        client.read(buffer, buffer, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            client.close();
                            System.exit(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (UnknownHostException e) {

                System.out.println("Market attempting to connect to unknown host");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Market exception during socket creation");
                e.printStackTrace();
            }
            Sleep(10);
        }
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

    private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
