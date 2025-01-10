package lk.ijse.dep13.server;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class SeverAppInitializer {


        public static void main(String[] args) throws IOException {
            Webcam webcam = Webcam.getDefault();
            webcam.open();
            ServerSocket serverSocket = new ServerSocket(9090);
            System.out.println("Server started on port 9090");
            while (true) {
                System.out.println("Waiting for connection...");
                Socket localSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + localSocket.getRemoteSocketAddress());
                new Thread(()->{
                    try {
                        OutputStream os = localSocket.getOutputStream();
                        BufferedOutputStream bos = new BufferedOutputStream(os);
                        ObjectOutputStream oos = new ObjectOutputStream(bos);

                        while (true) {
                            BufferedImage image = webcam.getImage();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(image, "jpg", baos);
                            oos.writeObject(baos.toByteArray());
                            oos.flush();
                            Thread.sleep(1000 / 27);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
