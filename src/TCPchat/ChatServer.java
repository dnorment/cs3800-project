package TCPchat;

import java.io.IOException;

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

    TextArea chatLogArea = new TextArea();
    TextArea userListArea = new TextArea();

    public ChatServer() throws IOException {

    }

    public void log(String message) {
        this.chatLogArea.appendText(message);
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;

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