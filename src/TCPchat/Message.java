package TCPchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * The main communication method between server and client. Has different types of messages for different function handling.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    static final int REQUEST_LOGIN = 1; //request to login from client to server
    static final int RESPONSE_LOGIN = 2; //response to login from server to client
    static final int USER_CONNECTED = 3; //new user connected
    static final int USER_DISCONNECTED = 4; //user left
    static final int CHAT_MESSAGE = 5; //new message from user
    static final int REQUEST_UPDATE_USERS = 6; //request updated user list from server
    static final int RESPONSE_UPDATE_USERS = 7; //send updated user list to client

    private int msgType;
    private String msg;
    private String fromUser;
    private boolean success;

    public int getMsgType() {
        return this.msgType;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getFromUser() {
        return this.fromUser;
    }

    /**
     * Attempts to read a Message object from a Socket.
     * @param client The socket to read from.
     * @return The Message if succeeded reading, else null.
     */
    static Message readMessage(Socket client) {
        Message msg = null;
        try {
            ObjectInputStream inFromClient = new ObjectInputStream(client.getInputStream());
            msg = (Message)inFromClient.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(e.getMessage());
        }
        return msg;
    }

    /**
     * Attempts to write a Message object to a Socket.
     * @param msg The Message to write into the Socket.
     * @param client The socket to write into.
     */
    static void writeMessage(Message msg, Socket client) {
        try {
            ObjectOutputStream outToClient = new ObjectOutputStream(client.getOutputStream());
            outToClient.writeObject(msg);
            outToClient.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Closes the specified Socket after flushing.
     * @param client The Socket to flush and close.
     */
    static void close(Socket client) {
        try {
            client.getOutputStream().flush();
            client.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //type 1, 3, 4, 7: REQUEST_LOGIN, USER_CONNECTED, USER_DISCONNECTED, RESPONSE_UPDATE_USERS
    public Message(int type, String msg) {
        this(type, msg, null,false);
    }

    //type 2: RESPONSE_LOGIN
    public Message(int type, boolean success) {
        this(type, null, null, success);
    }

    //type 5: CHAT_MESSAGE
    public Message(int type, String msg, String fromUser) {
        this(type, msg, fromUser, false);
    }

    //type 6: REQUEST_UPDATE_USERS
    public Message(int type) {
        this(type, null, null, false);
    }

    //general message object
    private Message(int type, String msg, String fromUser, boolean success) {
        this.msgType = type;
        this.msg = msg;
        this.fromUser = fromUser;
        this.success = success;
    }
}
