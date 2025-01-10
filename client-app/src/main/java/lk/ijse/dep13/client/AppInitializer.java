package lk.ijse.dep13.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pane root = FXMLLoader.load(getClass().getResource("/scene/MainScene.fxml"));

        // Set a light background color for the root pane
        root.setStyle("-fx-background-color: #f8f9fa;"); // Light grayish white background

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Remote Desktop Client");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
