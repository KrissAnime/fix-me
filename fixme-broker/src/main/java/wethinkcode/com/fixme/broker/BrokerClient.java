package wethinkcode.com.fixme.broker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class BrokerClient {
    public static void main(String args[]) {
        try {
//            ServerSocket brokerListener = new ServerSocket(4000);
            Socket broker = new Socket("localhost", 5000);
            InputStreamReader inputStreamReader = new InputStreamReader(broker.getInputStream());
            BufferedReader buff = new BufferedReader(inputStreamReader);

            PrintWriter printWriter = new PrintWriter(broker.getOutputStream());
            printWriter.print("Client connection");
            printWriter.flush();

//            Socket routerResponse = brokerListener.accept();
            System.out.println("Awaiting connection");

            System.out.println("Router response " + buff.readLine());

        } catch (UnknownHostException e) {
            System.out.println("Broker attempting to connect to unknown host");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Broker exception during socket creation");
            e.printStackTrace();
        }
    }
}
