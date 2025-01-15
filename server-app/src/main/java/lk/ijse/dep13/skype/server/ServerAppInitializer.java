/*package lk.ijse.dep13.skype.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerAppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/scene/MainScene.fxml"))));
        primaryStage.setTitle("Server");
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}*/



package lk.ijse.dep13.skype.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerAppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/scene/MainScene.fxml"))));
            primaryStage.setTitle("Server");
            primaryStage.show();
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load the FXML file. Check the path and controller setup.");
        }
    }
}

