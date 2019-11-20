package TCPchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    static final int REQUEST_LOGIN = 1;
    static final int RESPONSE_LOGIN = 2;
    static final int USER_CONNECTED = 3;
    static final int USER_DISCONNECTED = 4;
    static final int CHAT_MESSAGE = 5;
    static final int UPDATE_USERS = 6;

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

    static void writeMessage(Message msg, Socket client) {
        try {
            ObjectOutputStream outToClient = new ObjectOutputStream(client.getOutputStream());
            outToClient.writeObject(msg);
            outToClient.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //type 1, 3, 4: REQUEST_LOGIN, USER_CONNECTED, USER_DISCONNECTED
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

    //type ?: general message
    private Message(int type, String msg, String fromUser, boolean success) {
        this.msgType = type;
        this.msg = msg;
        this.fromUser = fromUser;
        this.success = success;
    }
}
