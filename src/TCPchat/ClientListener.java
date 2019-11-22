package TCPchat;

import java.io.IOException;
import java.net.Socket;

public class ClientListener implements Runnable {

    private ChatServer chatServer;
    private Socket clientSocket;
    private String clientUsername;

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
        clientUsername = msg.getMsg();

        //attempt to login client
        boolean login = this.chatServer.acceptClient(msg.getMsg(), this.clientSocket);
        this.chatServer.log("Attempting to login " + msg.getMsg());

        //send success to client
        msg = new Message(Message.RESPONSE_LOGIN, login);
        Message.writeMessage(msg, this.clientSocket);
        this.chatServer.log("Responding to client with login status");

        while (true) {
            msg = Message.readMessage(this.clientSocket);
            this.handle(msg);
        }
    }

    private void handle(Message msg) {
        int type = msg.getMsgType();
        switch (type) {
            case Message.USER_CONNECTED:
                this.chatServer.log(String.format("User %s connected", msg.getMsg()));
                this.chatServer.dispatch(msg);
                break;
            case Message.USER_DISCONNECTED:
                this.chatServer.log(String.format("User %s disconnected", msg.getMsg()));
                this.chatServer.userLeft(msg.getMsg());
                this.chatServer.dispatch(msg);
                break;
            case Message.CHAT_MESSAGE:
                this.chatServer.log(String.format("%s: %s", msg.getFromUser(), msg.getMsg()));
                this.chatServer.dispatch(msg);
                break;
            case Message.REQUEST_UPDATE_USERS:
                msg = new Message(Message.RESPONSE_UPDATE_USERS, this.chatServer.userListArea.getText());
                Message.writeMessage(msg, clientSocket);
                break;
            default:
                System.out.println("Error reading message from client");
        }
    }
}
