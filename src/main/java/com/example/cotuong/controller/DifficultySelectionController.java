package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class DifficultySelectionController {
    @FXML
    private StackPane difficultySelectionPane;
    @FXML
    private Button easyButton;
    @FXML
    private Button normalButton;
    @FXML
    private Button hardButton;
    @FXML
    private Button backButton;

    private ModeSelectionController modeSelectionController;

    public void initialize() {
        // Gán sự kiện cho các nút
        easyButton.setOnAction(e -> handleEasyMode());
        normalButton.setOnAction(e -> handleNormalMode());
        hardButton.setOnAction(e -> handleHardMode());
        backButton.setOnAction(e -> handleBackButton());
    }

    public void setModeSelectionController(ModeSelectionController controller) {
        this.modeSelectionController = controller;
    }

    @FXML
    private void handleEasyMode() {
        startGame(2);
    }

    @FXML
    private void handleNormalMode() {
        startGame(3);
    }

    @FXML
    private void handleHardMode() {
        startGame(4);
    }

    @FXML
    private void handleBackButton() {
        if (modeSelectionController != null) {
            modeSelectionController.hideDifficultySelection();
        }
    }

    private void startGame(int difficulty) {
        if (modeSelectionController != null) {
            modeSelectionController.startComputerGame(difficulty); // Gọi phương thức trên ModeSelectionController
            modeSelectionController.hideAllOverlays();
        }
    }
}