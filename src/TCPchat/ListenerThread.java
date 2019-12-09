package TCPchat;

import java.io.IOException;
import java.net.Socket;

/**
 * A thread to listen for new clients, which will accept clients and start new listeners for each successful client connection.
 */
public class ListenerThread implements Runnable {

    private ChatServer chatServer; //the chat server the thread will pass clients onto

    public ListenerThread(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    /**
     * Runs the listener thread for the server. Attempts to connect clients to the server and starts a ClientListener for successful connections.
     */
    @Override
    public void run() {
        while (true) {
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
