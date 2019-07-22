package wethinkcode.com.fixme.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Server {

    static AsynchronousServerSocketChannel ConnectionControl() {
        try {
            return AsynchronousServerSocketChannel.open();
        } catch (IOException e) {
            System.out.println("There was an error creating the server");
            e.printStackTrace();
            return null;
        }
    }

    static void SendMessage(String message, Socket target) {
        try {
            PrintWriter printWriter = new PrintWriter(target.getOutputStream());
            printWriter.print(message);
            printWriter.flush();
        } catch (IOException e) {
            System.out.println("There was a problem sending the message to the socket");
            e.printStackTrace();
        }
    }


    public static void main(String args[]) {
        try {
//            final AsynchronousServerSocketChannel server = ConnectionControl().bind(new InetSocketAddress(5000));

            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors
                    .newSingleThreadExecutor());
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group).bind(
                    new InetSocketAddress(5000));


            System.out.println("Router is waiting for connection");
            // (insert server.accept() logic here)
//            Future<AsynchronousSocketChannel> response = server.accept();

//            System.out.println(response.get().write(ByteBuffer.wrap("Test".getBytes())));

            server.accept("Client connection",
                    new CompletionHandler<AsynchronousSocketChannel, Object>() {
                        public void completed(AsynchronousSocketChannel ch, Object att) {
                            System.out.println("Accepted a connection");

                            System.out.println("The broker has connected to the routing service");
                            try {
                                System.out.println(ch.provider().toString());
                                System.out.println("Connection established to broker");
//                                PrintWriter printWriter = new PrintWriter();
//                                printWriter.print(message);
//                                printWriter.flush();
                                System.out.println("Returned message");
                            } catch (Exception e) {
                                System.out.println("There was a problem responding to the broker");
                                e.printStackTrace();
                            }
                            // accept the next connection
                            server.accept("Client connection", this);
                            System.out.println("Message: " + att.toString());

                            // handle this connection
                            //TODO handle(ch);
                        }

                        public void failed(Throwable exc, Object att) {
                            System.out.println("Failed to accept connection");
                        }
                    });

            // wait until group.shutdown()/shutdownNow(), or the thread is interrupted:
            group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);


        } catch (Exception error) {
            System.out.println("An error occurred opening the socket connection");
            error.printStackTrace();
        }
    }
}
