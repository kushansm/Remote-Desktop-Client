package lk.ijse.dep13.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SeverAppInitializer {

    public static void main(String[] args) throws Exception {

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
                        Robot robot = new Robot();
                        BufferedImage screen = robot
                                .createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(screen, "jpeg", baos);
                        oos.writeObject(baos.toByteArray());
                        oos.flush();
                        Thread.sleep(1000 / 27);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
            new Thread(()->{
                try {
                    InputStream is = localSocket.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    Robot robot = new Robot();
                    while (true){
                        Point coordinates = (Point) ois.readObject();
                        robot.mouseMove(coordinates.x, coordinates.y);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
