package com.wethinkcode.fixme.broker;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SeverExample {

    public static void main(String[] args){
        try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {

            server.bind(new InetSocketAddress("localhost", 5000));
            Future<AsynchronousSocketChannel> acceptCon = server.accept();
            AsynchronousSocketChannel client = acceptCon.get(10, TimeUnit.SECONDS);

            if ((client!= null) && (client.isOpen())) {

                ByteBuffer buffer = ByteBuffer.allocate(1024);
              /*  Future<Integer> readval = client.read(buffer);
                System.out.println("Received from client: " + new String(buffer.array()).trim());
                readval.get();
                buffer.flip(); */
                String str= "123456";
                Future<Integer> writeVal = client.write(ByteBuffer.wrap(str.getBytes()));
                System.out.println("Writing back to client: " +str);
                writeVal.get();
                buffer.flip();
                buffer.clear();
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
