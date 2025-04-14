package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DifficultySelectionController {
    @FXML
    private StackPane difficultyPane;
    @FXML
    private Button easyButton;
    @FXML
    private Button mediumButton;
    @FXML
    private Button hardButton;
    @FXML
    private Button backButton;

    private Stage stage;
    private Scene modeSelectionScene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setModeSelectionScene(Scene scene) {
        this.modeSelectionScene = scene;
    }

    @FXML
    private void handleBackButton() {
        if (modeSelectionScene != null) {
            stage.setScene(modeSelectionScene);
            stage.setTitle("Cờ Tướng - Chọn Chế Độ Chơi");
        }
    }
}