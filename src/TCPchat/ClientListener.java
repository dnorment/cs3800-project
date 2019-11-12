package TCPchat;

import java.io.IOException;
import java.net.Socket;

public class ClientListener implements Runnable {

    ChatServer chatServer;
    Socket clientSocket;

    public ClientListener(ChatServer server, Socket clientSocket) throws IOException {
        this.chatServer = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        this.chatServer.log("Connecting to a client");

        //accept msg from client
        Message msg = Message.readMessage(this.clientSocket);
        this.chatServer.log("Received request to login from " + msg.getMsg());

        //attempt to login client
        boolean login = this.chatServer.acceptClient(msg.getMsg(), this.clientSocket);
        this.chatServer.log("Attempting to login " + msg.getMsg());

        //send success to client
        msg = new Message(Message.RESPONSE_LOGIN, login);
        Message.writeMessage(msg, this.clientSocket);
        this.chatServer.log("Responding to client with login success");
    }
}
