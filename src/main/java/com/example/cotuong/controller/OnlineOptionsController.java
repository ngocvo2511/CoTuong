package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class OnlineOptionsController {

    @FXML
    private StackPane onlineOptionsPane;
    @FXML
    private Button randomMatchButton;
    @FXML
    private Button createRoomButton;
    @FXML
    private Button findRoomButton;
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

    @FXML
    private void handleCreateRoomButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/create_room.fxml"));
            Parent createRoomRoot = loader.load();
            Scene createRoomScene = new Scene(createRoomRoot, stage.getWidth(), stage.getHeight());
            createRoomScene.getStylesheets().add(getClass().getResource("/com/example/cotuong/css/find_create_room.css").toExternalForm());
            CreateRoomController createRoomController = loader.getController();
            createRoomController.setStage(stage);
            createRoomController.setPreviousScene(onlineOptionsPane.getScene());
            stage.setScene(createRoomScene);
            stage.setTitle("Cờ Tướng - Tạo Phòng");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFindRoomButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/find_room.fxml"));
            Parent findRoomRoot = loader.load();
            Scene findRoomScene = new Scene(findRoomRoot, stage.getWidth(), stage.getHeight());
            findRoomScene.getStylesheets().add(getClass().getResource("/com/example/cotuong/css/find_create_room.css").toExternalForm());
            FindRoomController findRoomController = loader.getController();
            findRoomController.setStage(stage);
            findRoomController.setPreviousScene(onlineOptionsPane.getScene());
            stage.setScene(findRoomScene);
            stage.setTitle("Cờ Tướng - Tìm Phòng");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}