package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

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
        // Close this overlay to return to mode selection
        if (modeSelectionController != null) {
            modeSelectionController.hideDifficultySelection();
        }
    }

    private void startGame(int difficulty) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
            Parent root = loader.load();

            OfflineGameController controller = loader.getController();
            controller.initialize(difficulty, true); // vsAI = true

            // Lấy stage hiện tại từ nút
            Stage stage = (Stage) ((Node) easyButton).getScene().getWindow();

            // Tạo scene mới với kích thước của stage hiện tại
            Scene gameScene = new Scene(root, stage.getWidth(), stage.getHeight());

            // Đặt scene mới
            stage.setScene(gameScene);
            stage.setTitle("Cờ Tướng - Đấu với AI");

            // Ghi log để kiểm tra kích thước
            System.out.println("AI Mode Stage size: " + stage.getWidth() + "x" + stage.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (modeSelectionController != null) {
            modeSelectionController.hideAllOverlays();
        }
    }
}