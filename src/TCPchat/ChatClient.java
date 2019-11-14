package TCPchat;

import java.io.*;
import java.net.*;

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

public class ChatClient extends Application {

    String username;
    boolean loggedIn = false;
    Socket clientSocket;

    //client log and user list
    TextArea chatLogArea = new TextArea();
    TextArea userListArea = new TextArea();

    public ChatClient() throws IOException {

    }

    public void log(String message) {
        this.chatLogArea.appendText(message + "\n");
    }

    public static void main(String[] args) throws IOException {
        launch(args);

        ChatClient client = new ChatClient();

        while (client.loggedIn) {

            Message msg = Message.readMessage(client.clientSocket);
            client.handle(msg);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;
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
            if (event.getCode() == KeyCode.ENTER) {
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
                        loggedIn = true;
                        this.log("Connected to server");
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

                if (text.equals(".")) {
                    Message msg;
                    msg = new Message(Message.USER_DISCONNECTED, username);
                    Message.writeMessage(msg, clientSocket);
                    loggedIn = false;
                    window.close();
                } else {
                    Message msg;
                    msg = new Message(Message.CHAT_MESSAGE, text);
                    Message.writeMessage(msg, clientSocket);
                }
            }
        });
    }

    public void handle(Message msg) {
        int type = msg.getMsgType();
        switch(type) {
            case Message.USER_CONNECTED:
                break;
            case Message.USER_DISCONNECTED:
                break;
            case Message.CHAT_MESSAGE:
                break;
            case Message.UPDATE_USERS:
                break;
            default:
                System.out.println("Error handling message from server");
        }
    }
}
