package com.example.cotuong.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
public class MainMenuController {
        @FXML
        private BorderPane mainMenu;
        @FXML
        private HBox titleBar;
        @FXML
        private Button minimizeButton;
        @FXML
        private Button maximizeButton;
        @FXML
        private Button closeButton;
        @FXML
        private Button instructionsButton;
        @FXML
        private Button settingsButton;
        @FXML
        private Button playButton;
        @FXML
        private Button historyButton;
        @FXML
        private Button downloadButton;

        private Stage stage;
        private Scene mainMenuScene;
        private double xOffset = 0;
        private double yOffset = 0;

        public void initialize() {
            // Make the stage draggable
            titleBar.setOnMousePressed(this::handleMousePressed);
            titleBar.setOnMouseDragged(this::handleMouseDragged);

            // Window control buttons
            minimizeButton.setOnAction(e -> stage.setIconified(true));
            maximizeButton.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
            closeButton.setOnAction(e -> stage.close());
        }

        public void setStage(Stage stage) {
            this.stage = stage;
        }

        public void setMainMenuScene(Scene scene) {
            this.mainMenuScene = scene;
        }

        private void handleMousePressed(MouseEvent event) {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        }

        private void handleMouseDragged(MouseEvent event) {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        }

        @FXML
        private void handleInstructionsButton() {
            System.out.println("Instructions button clicked");
        }

        @FXML
        private void handleSettingsButton() {
            System.out.println("Settings button clicked");
        }

        @FXML
        private void handlePlayButton() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/ModeSelection.fxml"));
                Parent root = loader.load();
                ModeSelectionController controller = loader.getController();
                controller.setStage(stage);
                controller.setMainMenuScene(mainMenuScene);
                Scene modeSelectionScene = new Scene(root, 1200, 720);
                stage.setScene(modeSelectionScene);
                stage.setTitle("Cờ Tướng - Chọn Chế Độ Chơi");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void handleHistoryButton() {
            System.out.println("History button clicked");
        }

        @FXML
        private void handleDownloadButton() {
            System.out.println("Download button clicked");
        }
    }

