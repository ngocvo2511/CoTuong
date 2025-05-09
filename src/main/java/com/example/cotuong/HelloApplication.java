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
        primaryStage.setMaximized(true);

        // Lắng nghe sự kiện thay đổi trạng thái maximized
        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isMaximized) -> {
            if (!isMaximized) {
                // Khi không ở chế độ maximized, đặt kích thước 0.9999 lần kích thước màn hình
                double newWidth = screenWidth * 1;
                double newHeight = screenHeight * 1;
                primaryStage.setWidth(newWidth);
                primaryStage.setHeight(newHeight);
                // Đặt vị trí để cửa sổ nằm chính giữa màn hình
                primaryStage.setX((screenWidth - newWidth) / 2);
                primaryStage.setY((screenHeight - newHeight) / 2);
            }
        });

        // Vô hiệu hóa thay đổi kích thước bằng chuột
        primaryStage.setOnCloseRequest(event -> {
            // Có thể thêm logic xử lý khi đóng cửa sổ nếu cần
        });

        // Ngăn chặn thay đổi kích thước bằng chuột kéo
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) {
                primaryStage.setWidth(screenWidth * 1);
            }
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) {
                primaryStage.setHeight(screenHeight * 1);
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
