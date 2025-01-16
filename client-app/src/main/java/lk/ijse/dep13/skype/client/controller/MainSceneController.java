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
    private int typingIndex = -1; // Keeps track of the "Typing" message index

    public void initialize() throws IOException {
        String ipAddress = "192.168.155.118";
        Socket socket = new Socket(ipAddress, 5050);
        System.out.println("Connected to server");

        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        writer = new PrintWriter(os, true);

        // Listen for live typing updates
        typeTxtFld.textProperty().addListener((observable, oldValue, newValue) -> {
            if (writer != null) {
                writer.println("LIVE_TYPING: " + newValue);
                writer.flush();
            }
        });

        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = reader.readLine()) != null) {
                    if (serverMessage.startsWith("LIVE_TYPING: ")) {
                        // Handle live typing updates
                        String liveMessage = serverMessage.replace("LIVE_TYPING: ", "");
                        Platform.runLater(() -> {
                            if (typingIndex == -1) {
                                // Add "Typing" message if it doesn't exist
                                typingIndex = messageListView.getItems().size();
                                messageListView.getItems().add(liveMessage);
                            } else {
                                // Update the existing "Typing" message
                                messageListView.getItems().set(typingIndex,liveMessage);
                            }
                        });
                    } else {
                        // Handle regular messages
                        String finalServerMessage = serverMessage;
                        Platform.runLater(() -> {
                            if (typingIndex != -1) {
                                // Remove the "Typing" message
                                messageListView.getItems().remove(typingIndex);
                                typingIndex = -1;
                            }
                            messageListView.getItems().add(finalServerMessage);
                        });
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
            writer.println("Client: " + message);
            writer.flush();

            // Remove the typing indicator and add the finalized message
            if (typingIndex != -1) {
                messageListView.getItems().remove(typingIndex);
                typingIndex = -1;
            }
            messageListView.getItems().add("You: " + message);
            typeTxtFld.clear();
        }
    }
}
