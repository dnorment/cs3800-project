package TCPchat;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * The main chat client application. It asks the user for a username and will refuse the connection if the name is taken. Starts an inline thread to listen for messages from the server.
 */
public class ChatClient extends Application {

    private String username;
    public Socket clientSocket;

    //client log and user list
    private TextArea chatLogArea = new TextArea();
    private TextArea userListArea = new TextArea();

    /**
     * Adds a log message to the chat box with a timestamp.
     * @param message The message to add to the chat box.
     */
    public void log(String message) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        this.chatLogArea.appendText("[" + formatter.format(date) + "] ");
        this.chatLogArea.appendText(message + "\n");
    }

    public static void main(String[] args) throws IOException {
        launch(args);
        new ChatClient();
    }

    @Override
    public void start(Stage window) throws Exception {
        //login client window
        //setup elements
        Label projectTitle = new Label("CS 3800 Project");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username, ENTER to submit");

        VBox loginVBox = new VBox(5, projectTitle, usernameField);
        loginVBox.setAlignment(Pos.CENTER);

        BorderPane loginPane = new BorderPane(loginVBox);
        loginPane.setPadding(new Insets(5));

        //setup layout
        StackPane loginLayout = new StackPane(loginPane);

        //set scene
        Scene loginScene = new Scene(loginLayout, 300, 500);

        //main client window
        //setup elements
        Label chatLogLabel = new Label("Chat log");
        TextField chatField = new TextField();
        chatField.setPromptText("Enter a message and press ENTER to send (. to exit)");
        VBox chatLogVBox = new VBox(5, chatLogLabel, chatLogArea, chatField);

        Label userListLabel = new Label("User list");
        VBox userListVBox = new VBox(5, userListLabel, userListArea);

        HBox clientHBox = new HBox(5, chatLogVBox, userListVBox);
        clientHBox.setAlignment(Pos.CENTER);

        BorderPane clientPane = new BorderPane(clientHBox);
        clientPane.setPadding(new Insets(5));

        //prevent manual modification of TextAreas
        userListArea.setEditable(false);
        chatLogArea.setEditable(false);

        //setup layout
        StackPane clientLayout = new StackPane(clientPane);

        //set scene
        Scene clientScene = new Scene(clientLayout, 700, 400);

        //dynamic sizing
        chatLogArea.prefWidthProperty().bind(clientScene.widthProperty().multiply(0.8));
        userListArea.prefWidthProperty().bind(clientScene.widthProperty().multiply(0.2));

        chatLogArea.prefHeightProperty().bind(clientScene.heightProperty());
        userListArea.prefHeightProperty().bind(clientScene.heightProperty());

        //default scene
        window.setScene(loginScene);
        window.setTitle("Chat Client");
        window.show();

        //get username and login on text submit
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !usernameField.getText().equals("") && usernameField.getText().length() >= 3) {
                //validate username and change scene
                try {
                    Message msg;
                    clientSocket = new Socket("localhost", 5000);
                    username = usernameField.getText();

                    //form username request and send
                    msg = new Message(Message.REQUEST_LOGIN, username);
                    Message.writeMessage(msg, clientSocket);

                    //wait for response and parse
                    msg = Message.readMessage(clientSocket);

                    //login on successful username choice
                    if (msg.getMsgType() == Message.RESPONSE_LOGIN && msg.getSuccess()) {
                        window.setScene(clientScene);
                        this.log("Connected to server");

                        //inform other clients of new user
                        msg = new Message(Message.USER_CONNECTED, username, username);
                        Message.writeMessage(msg, clientSocket);

                        //request updated user list
                        msg = new Message(Message.REQUEST_UPDATE_USERS);
                        Message.writeMessage(msg, clientSocket);

                        Thread handleThread = new Thread() { //start thread to listen for messages, pass to handler
                            public void run() {
                                while (true) {
                                    Message msg = Message.readMessage(clientSocket);
                                    handle(msg);
                                }
                            }
                        };
                        handleThread.setDaemon(true); //don't let thread keep JVM alive on exit
                        handleThread.start();
                    }

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        //read chat and send to server
        chatField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String text = chatField.getText();
                chatField.clear();

                Message msg;
                if (text.equals(".")) {
                    msg = new Message(Message.USER_DISCONNECTED, username, username);
                    Message.writeMessage(msg, clientSocket);
                    Message.close(clientSocket);
                    window.close();
                } else if (!text.equals("")) {
                    this.log(String.format("%s: %s", username, text));
                    msg = new Message(Message.CHAT_MESSAGE, text, username);
                    Message.writeMessage(msg, clientSocket);
                }
            }
        });
    }

    /**
     * Handles messages from the server, with different behavior depending on the type of message.
     * @param msg The Message to handle.
     */
    private void handle(Message msg) {
        int type = msg.getMsgType();
        switch(type) {
            case Message.USER_CONNECTED: //a new user connects
                this.log(String.format("User %s connected", msg.getMsg()));
                this.newUser(msg.getMsg());
                break;
            case Message.USER_DISCONNECTED: //a user disconnects
                this.log(String.format("User %s disconnected", msg.getMsg()));
                this.userLeft(msg.getMsg());
                break;
            case Message.CHAT_MESSAGE: //a user sends a chat message
                this.log(String.format("%s: %s", msg.getFromUser(), msg.getMsg()));
                break;
            case Message.RESPONSE_UPDATE_USERS: //received response from server with updated user list
                this.userListArea.setText(msg.getMsg());
                break;
            default:
                System.out.println("Error handling message from server");
        }
    }
    
    /**
     * Adds a user to the list of connected users, then sorts the list.
     * @param name The name of the user to add to the connected user list,
     */
    private void newUser(String name) {
        this.userListArea.appendText(name + "\n");
        this.sortUsers();
    }

    /**
     * Removes a client from the list of connected users, then updates the list area.
     * @param name The name of the user to remove from the connected user list.
     */
    void userLeft(String name) {
        String[] users = this.userListArea.getText().split("\n");
        String updatedUsers = "";
        for (String u : users) {
            if (!u.equals(name)) {
                updatedUsers += u + "\n";
            }
        }
        this.userListArea.setText(updatedUsers);
        this.sortUsers();
    }

    /**
     * Sorts the list of connected users by name.
     */
    private void sortUsers() {
        String[] users = this.userListArea.getText().split("\n");
        Arrays.sort(users);
        String updatedUsers = "";
        for (String u : users) {
            updatedUsers += u + "\n";
        }
        this.userListArea.setText(updatedUsers);
    }
}
