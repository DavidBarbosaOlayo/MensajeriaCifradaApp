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

    private static final String PUBLIC_KEY_FILE = "publicKey.txt";  // Ruta del archivo de la clave pública

    @Override
    public void start(Stage primaryStage) {
        // Llamamos al método para generar claves cuando se inicia la aplicación
        try {
            PGPManager.generateKeys(PUBLIC_KEY_FILE, "privateKey.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Elementos de la interfaz
        Label label = new Label("Introduce tu mensaje:");
        TextField textField = new TextField();
        Button encryptButton = new Button("Encriptar");
        Button sendButton = new Button("Enviar");
        TextArea messageArea = new TextArea();
        messageArea.setPrefSize(300, 400);
        messageArea.setEditable(false);

        // Botón encriptar
        encryptButton.setOnAction(e -> {
            String message = textField.getText();
            if (!message.isEmpty()) {
                try {
                    // Cargar la clave pública desde el archivo
                    String encryptedMessage = PGPManager.encryptMessage(message, PGPManager.loadPublicKeyFromFile(PUBLIC_KEY_FILE));
                    messageArea.appendText("Mensaje encriptado: " + encryptedMessage + "\n");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    messageArea.appendText("Error al encriptar el mensaje.\n");
                }
            } else {
                messageArea.appendText("Por favor, introduce un mensaje para encriptar.\n");
            }
        });

        // Botón enviar
        sendButton.setOnAction(e -> {
            String message = textField.getText();
            if (!message.isEmpty()) {
                messageArea.appendText("Tú: " + message + "\n");
                textField.clear();
            } else {
                messageArea.appendText("Por favor, introduce un mensaje para enviar.\n");
            }
        });

        VBox root = new VBox(10, label, textField, encryptButton, sendButton, messageArea);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Mensajería P2P Cifrada");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
