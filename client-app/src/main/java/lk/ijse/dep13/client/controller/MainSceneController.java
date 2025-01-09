package lk.ijse.dep13.client.controller;

import javafx.scene.layout.AnchorPane;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.awt.Point;
import java.io.*;
import java.net.Socket;

public class MainSceneController {
    public ImageView imgScreen;
    public AnchorPane root;
    private Socket socket;

    public void initialize() throws Exception {
        imgScreen.fitWidthProperty().bind(root.widthProperty());
        imgScreen.fitHeightProperty().bind(root.heightProperty());

        socket = new Socket("127.0.0.1", 9090);
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        imgScreen.setOnMouseMoved(mouseEvent -> {
            try {
                oos.writeObject(new Point((int) mouseEvent.getX(), (int) mouseEvent.getY()));
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