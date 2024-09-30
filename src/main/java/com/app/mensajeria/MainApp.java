package com.app.mensajeria;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        //elementos de la interfaz
        Label label = new Label("Introduce tu mensaje:");
        TextField textField = new TextField();
        Button sendButton = new Button("Enviar");
        TextArea messageArea = new TextArea();
        messageArea.setPrefSize(300,400);
        messageArea.setEditable(false);

        //botón enviar
        sendButton.setOnAction(e -> {
            String message = textField.getText();
            if (!message.isEmpty()) {
                messageArea.appendText("Tú: " + message + "\n");
                textField.clear();
            }
        });

        VBox root = new VBox(10, label, textField, sendButton, messageArea);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Mensajería P2P Cifrada");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
