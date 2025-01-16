package lk.ijse.dep13.skype.server.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainSceneController {
    public ListView<String> messageListView;
    public TextField typeTxtFld;

    private PrintWriter writer;
    private int typingIndex = -1; // Keeps track of the "Typing" message index

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

                // Listen for live typing updates
                typeTxtFld.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (writer != null) {
                        writer.println("LIVE_TYPING: " + newValue);
                        writer.flush();
                    }
                });

                new Thread(() -> {
                    try {
                        String clientMessage;
                        while ((clientMessage = reader.readLine()) != null) {
                            if (clientMessage.startsWith("LIVE_TYPING: ")) {
                                // Handle live typing updates
                                String liveMessage = clientMessage.replace("LIVE_TYPING: ", "");
                                Platform.runLater(() -> {
                                    if (typingIndex == -1) {
                                        // Add "Typing" message if it doesn't exist
                                        typingIndex = messageListView.getItems().size();
                                        messageListView.getItems().add(liveMessage);
                                    } else {
                                        // Update the existing "Typing" message
                                        messageListView.getItems().set(typingIndex, liveMessage);
                                    }
                                });
                            } else {
                                // Handle regular messages
                                String finalClientMessage = clientMessage;
                                Platform.runLater(() -> {
                                    if (typingIndex != -1) {
                                        // Remove the "Typing" message
                                        messageListView.getItems().remove(typingIndex);
                                        typingIndex = -1;
                                    }
                                    messageListView.getItems().add(finalClientMessage);
                                });
                            }
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
            try {
                // Get the server's hostname
                String serverName = InetAddress.getLocalHost().getHostName();

                // Send the message with the server's hostname
                writer.println(serverName + ": " + message);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
                writer.println("Unknown Server: " + message); // Fallback if hostname cannot be retrieved
                writer.flush();
            }

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
