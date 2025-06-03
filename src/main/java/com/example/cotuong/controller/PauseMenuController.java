        package com.example.cotuong.controller;

import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class PauseMenuController {

    @FXML
    private StackPane pausePane;

    @FXML
    private Button resumeButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button mainMenuButton;

    private OfflineGameController offlineGameController;

    @FXML
    private void initialize() {
        pausePane.setVisible(true);
    }

    @FXML
    private void handleResume() {
        Sounds.playButtonClickSound();
        if (offlineGameController != null) {
            offlineGameController.removePauseOverlay();
        }
    }

    @FXML
    private void handleNewGame() {
        Sounds.playButtonClickSound();
        // Logic sẽ được thêm sau
    }

    @FXML
    private void handleSettings() {
        Sounds.playButtonClickSound();
        // Logic sẽ được thêm sau
    }

    @FXML
    private void handleMainMenu() {
        Sounds.playButtonClickSound();
        if (offlineGameController != null) {
            offlineGameController.removePauseOverlay();
            // Logic chuyển về màn hình chính sẽ được thêm sau
        }
    }

    public void showPauseMenu() {
        pausePane.setVisible(true);
    }

    public void hidePauseMenu() {
        pausePane.setVisible(false);
    }

    public void setOfflineGameController(OfflineGameController controller) {
        this.offlineGameController = controller;
    }
}
