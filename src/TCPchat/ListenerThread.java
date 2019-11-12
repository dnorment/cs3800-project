package TCPchat;

import java.io.IOException;
import java.net.Socket;

public class ListenerThread implements Runnable {

    ChatServer chatServer;
    Socket clientSocket;

    public ListenerThread(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                clientSocket = this.chatServer.serverSocket.accept();
                Thread clientListener = new Thread(new ClientListener(this.chatServer, this.clientSocket));
                clientListener.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
