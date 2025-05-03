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

        Scene scene = new Scene(root, 1400, 720);
        controller.setMainMenuScene(scene);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Cờ Tướng - Menu Chính");
        primaryStage.setResizable(true); // Cho phép dùng nút maximize

        // Giới hạn chỉ cho phép kích thước gốc hoặc kích thước full màn hình
        final double initialWidth = 1400;
        final double initialHeight = 720;

        // Lắng nghe thay đổi kích thước bất thường (do kéo chuột)
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized() && newVal.doubleValue() != initialWidth) {
                primaryStage.setWidth(initialWidth);
            }
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized() && newVal.doubleValue() != initialHeight) {
                primaryStage.setHeight(initialHeight);
            }
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}