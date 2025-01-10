package lk.ijse.dep13.client.controller;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.Point;
import java.io.*;
import java.net.Socket;

public class MainSceneController {
    public ImageView imgScreen;
    public AnchorPane root;
    private Socket socket;

    public void initialize() throws Exception {
        // Set a light background color
        root.setStyle("-fx-background-color: #f8f9fa;");

        imgScreen.fitWidthProperty().bind(root.widthProperty());
        imgScreen.fitHeightProperty().bind(root.heightProperty());

        socket = new Socket("127.0.0.1", 9090);
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        imgScreen.setOnMouseMoved(mouseEvent -> {
            try {
                oos.writeObject(new Point((int) mouseEvent.getX(), (int) mouseEvent.getY()));
                oos.writeInt(0); // 0 indicates no mouse button clicked
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        imgScreen.setOnMouseClicked(mouseEvent -> {
            try {
                Point point = new Point((int) mouseEvent.getX(), (int) mouseEvent.getY());
                oos.writeObject(point);

                int button = 0;
                if (mouseEvent.getButton() == MouseButton.PRIMARY) button = 1;
                else if (mouseEvent.getButton() == MouseButton.MIDDLE) button = 2;
                else if (mouseEvent.getButton() == MouseButton.SECONDARY) button = 3;

                oos.writeInt(button);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        root.setOnKeyPressed(keyEvent -> {
            try {
                oos.writeInt(keyEvent.getCode().getCode()); // Send the key code
                oos.writeBoolean(true); // True for key press
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        root.setOnKeyReleased(keyEvent -> {
            try {
                oos.writeInt(keyEvent.getCode().getCode()); // Send the key code
                oos.writeBoolean(false); // False for key release
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                InputStream is = socket.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ObjectInputStream ois = new ObjectInputStream(bis);

                while (true) {
                    byte[] image = (byte[]) ois.readObject();
                    ByteArrayInputStream bais = new ByteArrayInputStream(image);
                    Image screen = new Image(bais);
                    updateValue(screen);
                }
            }
        };

        imgScreen.imageProperty().bind(task.valueProperty());
        new Thread(task).start();
    }
}
