package com.example.cotuong;

import com.example.cotuong.controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("/com/example/cotuong/fxml/MainMenu.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        MainMenuController controller = loader.getController();
        controller.setStage(primaryStage);

        // Lấy kích thước màn hình
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Tạo scene với kích thước đầy màn hình
        Scene scene = new Scene(root, screenWidth, screenHeight);

        controller.setMainMenuScene(scene);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Cờ Tướng - Menu Chính");
        primaryStage.setResizable(true);

        // Đặt cửa sổ full màn hình khi khởi tạo

        primaryStage.show();}

    public static void main(String[] args) {
        launch(args);
    }
}