package wethinkcode.com.fixme.router;

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

public class Server {

    public static void main(String args[]) {

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/

        try {
            System.out.println("Starting up Server...");
            Selector selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open().bind(new InetSocketAddress("localhost", 5000));
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Waiting for client connection");
            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isAcceptable()) {
                        registerClient(selector, server);
                    }

                    if (key.isReadable()) {
                        answerClient(buffer, key);
                    }

                    iterator.remove();
                }
            }
        } catch (IOException e) {
            System.out.println("The selector had in IOException");
            e.printStackTrace();
        }

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
    }

    private static void registerClient(Selector selector, ServerSocketChannel server) throws IOException {
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private static void answerClient(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel)key.channel();
        client.read(buffer);
        if (new String(buffer.array()).trim().equals("Exit")) {
            client.close();
            System.out.println("Disconnecting from the client");
        } else {
            System.out.println("Client says " + new String(buffer.array()).trim());
        }
        buffer.flip();
        buffer.clear();

        buffer = ByteBuffer.wrap("I am the server".getBytes());
        client.write(buffer);
        buffer.flip();
        buffer.clear();
    }
}
