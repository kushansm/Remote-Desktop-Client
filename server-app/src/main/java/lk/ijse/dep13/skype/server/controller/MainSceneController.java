package lk.ijse.dep13.skype.server.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainSceneController {
    public ListView<String> messageListView;
    public TextField typeTxtFld;

    private PrintWriter writer;

    public void initialize() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5050);
        System.out.println("Server started at port 5050");

        new Thread(() -> {
            try {
                System.out.println("Waiting for connection...");
                Socket localSocket = serverSocket.accept();
                System.out.println("Connection accepted");

                InputStream is = localSocket.getInputStream();
                OutputStream os = localSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                writer = new PrintWriter(os, true);

                new Thread(() -> {
                    try {
                        String clientMessage;
                        while ((clientMessage = reader.readLine()) != null) {
                            String finalClientMessage = clientMessage;
                            Platform.runLater(() -> messageListView.getItems().add( finalClientMessage)); // Display message in ListView
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendBtnOnAction(ActionEvent actionEvent) {
        String message = typeTxtFld.getText().trim();
        if (!message.isEmpty() && writer != null) {
            writer.println("Server: " + message);
            writer.flush();
            messageListView.getItems().add("You: " + message);
            typeTxtFld.clear();
        }
    }
}
