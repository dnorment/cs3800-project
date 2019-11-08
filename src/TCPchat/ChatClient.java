package TCPchat;

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

import java.io.IOException;
import java.net.Socket;

public class ChatClient extends Application {

    String username;
    boolean loggedIn = false;

    //client log and user list
    TextArea chatLogArea = new TextArea();
    TextArea userListArea = new TextArea();

    public ChatClient() throws IOException {

    }

    public static void main(String[] args) throws IOException {
        launch(args);

        ChatClient client = new ChatClient();
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
        VBox chatLogVBox = new VBox(5, chatLogLabel, chatLogArea);

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
                username = usernameField.getText();
                
                window.setScene(clientScene);
            }
        });
    }
}