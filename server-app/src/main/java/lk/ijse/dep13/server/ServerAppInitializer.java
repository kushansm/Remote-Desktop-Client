package lk.ijse.dep13.server;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAppInitializer {

    public static void main(String[] args) throws IOException {
        File depDrive = new File("dep-drive");
        depDrive.mkdir();

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("Server started on port 6060");
        } catch (BindException e) {
            System.out.println("6060 port is already in use");
            serverSocket = new ServerSocket(0);
            System.out.println("Re-try: server started on port " + serverSocket.getLocalPort());
        }

        while (true){
            Socket localSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + localSocket.getRemoteSocketAddress());
            new Thread(()->{
                try {
                    InputStream is = localSocket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String username = br.readLine().strip();
                    if (!(username.length() >= 3 && username.length() <= 10)){
                        System.out.println(username);
                        System.out.println("Invalid username");
                        return;
                    }

                    File userFolder = new File(depDrive, username);
                    userFolder.mkdir();

                    String fileName;
                    fileName = br.readLine().strip();
                    if (fileName.isEmpty()){
                        System.out.println("Invalid filename");
                        return;
                    }

                    File file = new File(userFolder, fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    BufferedInputStream bis = new BufferedInputStream(is);

                    while (true){
                        byte[] buffer = new byte[1024];
                        int read = bis.read(buffer);
                        if (read == -1){
                            break;
                        }
                        bos.write(buffer, 0, read);
                    }
                    System.out.printf("File: %s uploaded successfully%n", fileName);
                    bos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}