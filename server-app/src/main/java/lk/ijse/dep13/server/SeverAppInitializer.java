package lk.ijse.dep13.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
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

            // Screen capture thread
            new Thread(() -> {
                try {
                    OutputStream os = localSocket.getOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    ObjectOutputStream oos = new ObjectOutputStream(bos);

                    Robot robot = new Robot();
                    while (true) {
                        BufferedImage screen = robot
                                .createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(screen, "jpeg", baos);
                        oos.writeObject(baos.toByteArray());
                        oos.flush();
                        Thread.sleep(1000 / 20);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // Mouse input thread
            new Thread(() -> {
                try {
                    InputStream is = localSocket.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    Robot robot = new Robot();
                    while (true) {
                        Point coordinates = (Point) ois.readObject();
                        int button = ois.readInt(); // Read the mouse button pressed
                        robot.mouseMove(coordinates.x, coordinates.y);

                        if (button == 1) {
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); // Left-click
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        } else if (button == 2) {
                            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK); // Middle-click
                            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                        } else if (button == 3) {
                            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK); // Right-click
                            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // Keyboard input thread
            new Thread(() -> {
                try {
                    InputStream is = localSocket.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    Robot robot = new Robot();
                    while (true) {
                        int keyCode = ois.readInt(); // Read the key code
                        boolean isPress = ois.readBoolean(); // Read whether it's a press or release

                        if (isPress) {
                            robot.keyPress(keyCode);
                        } else {
                            robot.keyRelease(keyCode);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
