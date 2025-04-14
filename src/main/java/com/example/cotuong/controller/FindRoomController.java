package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FindRoomController {

    @FXML
    private TextField roomNameField;
    @FXML
    private TextField playerNameField;
    @FXML
    private Button findRoomButton;
    @FXML
    private Button backButton;

    private Stage stage;
    private Scene previousScene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    private void handleFindRoomButton() {
        String roomName = roomNameField.getText().trim();
        String playerName = playerNameField.getText().trim();

        if (!roomName.isEmpty() && !playerName.isEmpty()) {
            // Logic để tìm phòng sẽ được thêm sau
            // Hiện tại chỉ in ra console để kiểm tra
            System.out.println("Finding room: " + roomName + " for player: " + playerName);
        }
    }

    @FXML
    private void handleBackButton() {
        if (previousScene != null) {
            stage.setScene(previousScene);
            stage.setTitle("Cờ Tướng - Tùy Chọn Online");
        }
    }
}