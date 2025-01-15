package lk.ijse.dep13.skype.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.Socket;

public class MainSceneController {
    public AnchorPane root;
    public TextField typeTxtFld;
    public ListView<String> messageListView;
    public Button sendBtn;

    private PrintWriter writer;

    public void initialize() throws IOException {
        String ipAddress = "192.168.155.118";
        Socket socket = new Socket(ipAddress, 5050);
        System.out.println("Connected to server");

        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        writer = new PrintWriter(os, true);

        new Thread(() -> {
            try {
                while (true) {
                    String serverMessage = reader.readLine();
                    if (serverMessage != null) {
                        Platform.runLater(() -> messageListView.getItems().add(serverMessage));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendBtnOnAction(ActionEvent actionEvent) {
        String message = typeTxtFld.getText().trim();
        if (!message.isEmpty()) {
            writer.println("Client " + message);
            writer.flush();
            Platform.runLater(() -> messageListView.getItems().add("You: " + message));
            typeTxtFld.clear();
        }
    }
}
