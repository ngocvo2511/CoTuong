package com.example.cotuong.controller;

import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class InstructionsController {

    @FXML
    private StackPane instructionsPane;

    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    @FXML
    public void initialize() {
        // Không cần thiết lập văn bản hoặc tải hình ảnh; nội dung đã nhúng trong FXML
    }

    @FXML
    private void handleCloseButton() {
        if (mainMenuController != null) {
            Sounds.playButtonClickSound();
            mainMenuController.hideInstructions(); // Ẩn lớp phủ hướng dẫn
        }
    }
}