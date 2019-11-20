package TCPchat;

import java.io.IOException;
import java.net.Socket;

public class ClientListener implements Runnable {

    private ChatServer chatServer;
    private Socket clientSocket;

    ClientListener(ChatServer server, Socket clientSocket) throws IOException {
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

        while (true) {
            msg = Message.readMessage(this.clientSocket);
            this.handleMessage(msg);
        }
    }

    private void handleMessage(Message msg) {
        int type = msg.getMsgType();
        switch (type) {
            case Message.USER_CONNECTED:
                this.chatServer.log(String.format("User %s connected", msg.getMsg()));
                this.chatServer.dispatch(msg);
                break;
            case Message.USER_DISCONNECTED:
                this.chatServer.log(String.format("User %s disconnected", msg.getMsg()));
                this.chatServer.dispatch(msg);
                break;
            case Message.CHAT_MESSAGE:
                this.chatServer.log(String.format("%s: %s", msg.getFromUser(), msg.getMsg()));
                this.chatServer.dispatch(msg);
                break;
            default:
                System.out.println("Error reading message from client");
        }
    }
}
