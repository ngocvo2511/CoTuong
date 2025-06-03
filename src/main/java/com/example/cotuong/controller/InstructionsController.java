package com.example.cotuong.controller;

import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

public class InstructionsController {

    @FXML
    private StackPane instructionsPane;
    @FXML
    private TextArea instructionsText;

    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    @FXML
    public void initialize() {
        instructionsText.setText(
                "HƯỚNG DẪN CHƠI CỜ TƯỚNG\n\n"
        );
    }

    @FXML
    private void handleCloseButton() {
        if (mainMenuController != null) {
            Sounds.playButtonClickSound();
            mainMenuController.hideInstructions(); // Hide the instructions overlay
        }
    }
}