package TCPchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListener implements Runnable {

    ChatServer chatServer;

    public ClientListener(ChatServer server) throws IOException {
        this.chatServer = server;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                Socket client = serverSocket.accept();

                this.chatServer.log("Accepted a client");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
