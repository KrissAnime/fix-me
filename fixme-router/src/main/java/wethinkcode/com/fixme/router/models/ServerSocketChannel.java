package wethinkcode.com.fixme.router.models;

import lombok.Getter;
import lombok.Setter;

import java.nio.channels.SocketChannel;

public class ServerSocketChannel {
    private @Getter @Setter SocketChannel socketChannel;
    private @Getter @Setter int socketID;

    public ServerSocketChannel(SocketChannel socketChannel, int socketID) {
        setSocketChannel(socketChannel);
        setSocketID(socketID);
    }
}
