package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ModeSelectionController {
    @FXML
    private StackPane modeSelectionPane;
    @FXML
    private Button offlineButton;
    @FXML
    private Button onlineButton;
    @FXML
    private Button aiButton;
    @FXML
    private Button backButton;

    private Stage stage;
    private Scene mainMenuScene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainMenuScene(Scene scene) {
        this.mainMenuScene = scene;
    }

    @FXML
    private void handleOfflineMode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
            Parent root = loader.load();
            Scene gameScene = new Scene(root, 1200, 720);
            stage.setScene(gameScene);
            stage.setTitle("Cờ Tướng - Game");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải màn hình chơi offline");
            alert.setContentText("Vui lòng kiểm tra lại file cấu hình hoặc liên hệ hỗ trợ.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOnlineMode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OnlineOptions.fxml"));
            Parent root = loader.load();
            OnlineOptionsController controller = loader.getController();
            controller.setStage(stage);
            controller.setModeSelectionScene(stage.getScene());
            Scene onlineOptionsScene = new Scene(root, 1200, 720);
            stage.setScene(onlineOptionsScene);
            stage.setTitle("Cờ Tướng - Chọn Chế Độ Online");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải màn hình chơi online");
            alert.setContentText("Vui lòng kiểm tra kết nối mạng hoặc liên hệ hỗ trợ.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAIMode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/DifficultySelection.fxml"));
            Parent root = loader.load();
            DifficultySelectionController controller = loader.getController();
            controller.setStage(stage);
            controller.setModeSelectionScene(stage.getScene());
            Scene difficultyScene = new Scene(root, 1200, 720);
            stage.setScene(difficultyScene);
            stage.setTitle("Cờ Tướng - Chọn Độ Khó");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        if (mainMenuScene != null) {
            stage.setScene(mainMenuScene);
            stage.setTitle("Cờ Tướng - Menu Chính");
        }
    }
}