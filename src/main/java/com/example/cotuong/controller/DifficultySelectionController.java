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
        // Initialization logic if needed
    }

    public void setModeSelectionController(ModeSelectionController controller) {
        this.modeSelectionController = controller;
    }

    @FXML
    private void handleEasyMode() {
        System.out.println("Easy difficulty selected");
        // TODO: Start game with easy difficulty
        startGame("easy");
    }

    @FXML
    private void handleNormalMode() {
        System.out.println("Normal difficulty selected");
        // TODO: Start game with normal difficulty
        startGame("normal");
    }

    @FXML
    private void handleHardMode() {
        System.out.println("Hard difficulty selected");
        // TODO: Start game with hard difficulty
        startGame("hard");
    }

    @FXML
    private void handleBackButton() {
        // Close this overlay to return to mode selection
        if (modeSelectionController != null) {
            modeSelectionController.hideDifficultySelection();
        }
    }

    private void startGame(String difficulty) {
        // TODO: Implement game start with selected difficulty
        System.out.println("Starting game with " + difficulty + " difficulty");

        // Here you would start the game, potentially loading a game board screen
        // For now we'll just return to the main menu as a placeholder
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }
    }
}