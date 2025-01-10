package lk.ijse.dep13.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;

public class MainSceneController {

    public AnchorPane root;
    public Button btnSend;

    public void lblShowOnMouseClicked(MouseEvent mouseEvent) {
        // Trigger file upload when the label is clicked
        uploadFile("Uthpala", "/Users/admin/Downloads/fc3187d7de27041f5d78b86f195ed34c (1).jpg");
    }

    public void btnSendOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (file != null) {
            uploadFile("Uthpala", file.getAbsolutePath());
        } else {
            System.out.println("No file selected");
        }
    }

    private void uploadFile(String username, String filePath) {
        String host = "192.168.8.102";
        int port = 6060;

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found");
            return;
        }

        Socket socket = null;
        try {
            socket = new Socket(host, port);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(username);
            bw.newLine();
            bw.write(file.getName());
            bw.newLine();
            bw.flush();

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(os);

            System.out.println("Start: File uploading");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            System.out.println("Finish: File uploading");
            bis.close();
        } catch (IOException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
