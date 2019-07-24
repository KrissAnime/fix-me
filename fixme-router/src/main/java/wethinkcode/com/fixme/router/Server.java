package wethinkcode.com.fixme.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {

    public static void main(String args[]) {

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/

        try {
            ServerSocketChannel server = ServerSocketChannel.open().bind(new InetSocketAddress(4000));
            Selector selector = Selector.open();

            while (true) {
                SocketChannel client = server.accept();
                client.configureBlocking(false);

                client.register(selector, SelectionKey.OP_CONNECT);
            }
        } catch (IOException e) {
            System.out.println("The selector had in IOException");
            e.printStackTrace();
        }

        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
        /*******TESTING WINDOW*********/
    }
}
