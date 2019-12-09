package TCPchat;

import java.io.IOException;
import java.net.Socket;

/**
 * A listener thread for each individual client. Executes the login sequence for clients and refuses duplicate names. Does the handling of messages from the client.
 */
public class ClientListener implements Runnable {

    private ChatServer chatServer;
    private Socket clientSocket;

    ClientListener(ChatServer server, Socket clientSocket) throws IOException {
        this.chatServer = server;
        this.clientSocket = clientSocket;
    }

    /**
     * Starts the listener for an individual client. Runs the login sequence then listens indefinitely for new messages to handle.
     */
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
        this.chatServer.log("Responding to client with login status");

        while (true) { //constantly listen for messages from client
            msg = Message.readMessage(this.clientSocket);
            this.handle(msg);
        }
    }

    /**
     * Handles messages from the client, with different behavior depending on the type of message.
     * @param msg The Message to handle.
     */
    private void handle(Message msg) {
        int type = msg.getMsgType();
        switch (type) {
            case Message.USER_CONNECTED: //a new user connects
                this.chatServer.log(String.format("User %s connected", msg.getMsg()));
                this.chatServer.dispatch(msg);
                break;
            case Message.USER_DISCONNECTED: //a user disconnects
                this.chatServer.log(String.format("User %s disconnected", msg.getMsg()));
                this.chatServer.userLeft(msg.getMsg());
                this.chatServer.dispatch(msg);
                break;
            case Message.CHAT_MESSAGE: //a user sends a chat message
                this.chatServer.log(String.format("%s: %s", msg.getFromUser(), msg.getMsg()));
                this.chatServer.dispatch(msg);
                break;
            case Message.REQUEST_UPDATE_USERS: //respond to new clients with the list of connected users
                msg = new Message(Message.RESPONSE_UPDATE_USERS, this.chatServer.userListArea.getText());
                Message.writeMessage(msg, clientSocket);
                break;
            default:
                System.out.println("Error reading message from client");
        }
    }
}
