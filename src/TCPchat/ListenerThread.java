package TCPchat;

import java.io.IOException;
import java.net.Socket;

public class ListenerThread implements Runnable {

    private ChatServer chatServer;

    public ListenerThread(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        while (this.chatServer.acceptingClients) {
            try {
                Socket clientSocket = this.chatServer.serverSocket.accept();
                Thread clientListener = new Thread(new ClientListener(this.chatServer, clientSocket));
                clientListener.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
