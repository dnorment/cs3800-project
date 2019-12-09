package TCPchat;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatServer extends Application {

    private HashMap<String, Socket> clients = new HashMap<String, Socket>();
    ServerSocket serverSocket;

    boolean acceptingClients = true;

    //server log and user list
    TextArea chatLogArea = new TextArea();
    TextArea userListArea = new TextArea();

    public ChatServer() throws IOException {
        this.log("Opening server socket on port 5000");
        serverSocket = new ServerSocket(5000);
        this.log("Opening client listener");
        Thread listenerThread = new Thread(new ListenerThread(this));
        listenerThread.start();
    }

    public boolean acceptClient(String username, Socket client) {
        //check each username and add client to userlist if no match
        for (String u : this.userListArea.getText().split("\n")) {
            if (u.equals(username)) {
                return false;
            }
        }
        this.newUser(username); //add client to userlist
        this.log("Accepted client " + username);

        this.clients.put(username, client); //add new client to map
        return true;
    }

    public void log(String message) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        this.chatLogArea.appendText("[" + formatter.format(date) + "] ");
        this.chatLogArea.appendText(message + "\n");
    }

    public static void main(String[] args) throws IOException {
        launch(args);
        new ChatServer();
    }

    public void dispatch(Message msg) {
        for (Map.Entry<String,Socket> e : clients.entrySet()) {
             if (!msg.getFromUser().equals(e.getKey())) { //don't send to sender
                 Message.writeMessage(msg, e.getValue());
             }
        }
    }
    
    private void newUser(String name) {
        this.userListArea.appendText(name + "\n");
        this.sortUsers();
    }


    void userLeft(String name) {
        this.clients.remove(name);
        if (!this.clients.isEmpty()) {
            String[] users = this.userListArea.getText().split("\n");
            this.userListArea.clear();
            String updatedUsers = "";
            for (String u : users) {
                if (!u.equals(name) && u.length() > 0) {
                    updatedUsers += u + "\n";
                }
            }
            this.userListArea.setText(updatedUsers);
            this.sortUsers();
        } else {
            this.userListArea.clear();
        }
    }

    private void sortUsers() {
        String[] users = this.userListArea.getText().split("\n");
        Arrays.sort(users);
        String updatedUsers = "";
        for (String u : users) {
            updatedUsers += u + "\n";
        }
        this.userListArea.setText(updatedUsers);
    }

    @Override
    public void start(Stage window) throws Exception {
        //setup elements
        Label chatLogLabel = new Label("Chat log");
        VBox chatLogVBox = new VBox(5, chatLogLabel, chatLogArea);

        Label userListLabel = new Label("User list");
        VBox userListVBox = new VBox(5, userListLabel, userListArea);

        HBox serverHBox = new HBox(5, chatLogVBox, userListVBox);
        serverHBox.setAlignment(Pos.CENTER);

        BorderPane serverPane = new BorderPane(serverHBox);
        serverPane.setPadding(new Insets(5));

        //prevent manual modification of TextAreas
        userListArea.setEditable(false);
        chatLogArea.setEditable(false);

        //setup layout
        StackPane serverLayout = new StackPane(serverPane);

        //set scene
        Scene serverScene = new Scene(serverLayout, 700, 400);

        //dynamic sizing
        chatLogArea.prefWidthProperty().bind(serverScene.widthProperty().multiply(0.8));
        userListArea.prefWidthProperty().bind(serverScene.widthProperty().multiply(0.2));

        chatLogArea.prefHeightProperty().bind(serverScene.heightProperty());
        userListArea.prefHeightProperty().bind(serverScene.heightProperty());

        //display scene
        window.setScene(serverScene);
        window.setTitle("Chat Server");
        window.show();
    }

}