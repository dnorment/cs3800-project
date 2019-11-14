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

    public static Message readMessage(Socket client) {
        Message msg = null;
        try {
            ObjectInputStream inFromClient = new ObjectInputStream(client.getInputStream());
            msg = (Message)inFromClient.readObject();
            inFromClient.reset();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(e.getMessage());
        }
        return msg;
    }

    public static void writeMessage(Message msg, Socket client) {
        try {
            ObjectOutputStream outToClient = new ObjectOutputStream(client.getOutputStream());
            outToClient.writeObject(msg);
            outToClient.flush();
            outToClient.reset();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //type 1, 3, 4, 5: REQUEST_LOGIN, USER_CONNECTED, USER_DISCONNECTED, CHAT_MESSAGE
    public Message(int type, String msg) {
        this(type, msg, false);
    }

    //type 2: RESPONSE_LOGIN
    public Message(int type, boolean success) {
        this(type, null, success);
    }

    //type ?: general message
    public Message(int type, String msg, boolean success) {
        this.msgType = type;
        this.msg = msg;
        this.success = success;
    }
}
