package com.example.cotuong;

import com.example.cotuong.controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        URL fxmlUrl = getClass().getResource("/com/example/cotuong/fxml/MainMenu.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        MainMenuController controller = loader.getController();
        controller.setStage(primaryStage);
        Scene scene = new Scene(root, 1200, 720);
        controller.setMainMenuScene(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cờ Tướng - Menu Chính");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}