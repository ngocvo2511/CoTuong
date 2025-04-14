package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class CreateRoomController {

    @FXML
    private TextField roomNameField;
    @FXML
    private TextField playerNameField;
    @FXML
    private ChoiceBox<String> timeChoiceBox;
    @FXML
    private Button createRoomButton;
    @FXML
    private Button backButton;

    private Stage stage;
    private Scene previousScene;

    public void initialize() {
        timeChoiceBox.getItems().addAll("5 phút", "10 phút", "15 phút", "30 phút");
        timeChoiceBox.setValue("10 phút");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    private void handleCreateRoomButton() {
        String roomName = roomNameField.getText().trim();
        String playerName = playerNameField.getText().trim();
        String time = timeChoiceBox.getValue();

        if (!roomName.isEmpty() && !playerName.isEmpty()) {
            // Logic để tạo phòng sẽ được thêm sau
            // Hiện tại chỉ in ra console để kiểm tra
            System.out.println("Creating room: " + roomName + " for player: " + playerName + " with time: " + time);
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