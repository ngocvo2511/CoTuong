package com.example.cotuong;

import com.example.cotuong.controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Debug CSS
        URL cssUrl = getClass().getResource("/com/example/cotuong/css/main_menu.css");
        System.out.println("CSS URL: " + cssUrl);
        if (cssUrl == null) {
            System.err.println("Cannot find main_menu.css");
        }

        // Debug FXML
        URL fxmlUrl = getClass().getResource("/com/example/cotuong/fxml/MainMenu.fxml");
        System.out.println("FXML URL: " + fxmlUrl);
        if (fxmlUrl == null) {
            System.err.println("Cannot find MainMenu.fxml");
        }

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