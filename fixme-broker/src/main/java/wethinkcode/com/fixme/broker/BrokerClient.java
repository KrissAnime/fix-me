package wethinkcode.com.fixme.broker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class BrokerClient {
    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static BrokerClient broker = new BrokerClient();

    public static void main(String args[]) {
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost",5000));
            buffer = ByteBuffer.allocate(2048);

            while (true) {
                // Sending the message to the server;
                buffer = ByteBuffer.wrap("I am the broker".getBytes());
                client.write(buffer);
                buffer.flip();
                buffer.clear();

                //Reading the response from the server
                client.read(buffer);
                System.out.println("Server response " + new String(buffer.array()).trim());
                buffer.flip();
                buffer.clear();
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Broker attempting to connect to unknown host");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Broker exception during socket creation");
            e.printStackTrace();
        }
    }
}
