package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Difficulty;
import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
        Sounds.playButtonClickSound();
        startGame(Difficulty.EASY);
    }

    @FXML
    private void handleNormalMode() {
        Sounds.playButtonClickSound();
        startGame(Difficulty.MEDIUM);
    }

    @FXML
    private void handleHardMode() {
        Sounds.playButtonClickSound();
        startGame(Difficulty.HARD);
    }

    @FXML
    private void handleBackButton() {
        Sounds.playButtonClickSound();
        if (modeSelectionController != null) {
            modeSelectionController.hideDifficultySelection();
        }
    }

    private void startGame(Difficulty difficulty) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
            Parent root = loader.load();
            OfflineGameController controller = loader.getController();
            controller.initialize(difficulty,true);

            Stage stage = (Stage) ((Node) easyButton).getScene().getWindow();
            Scene gameScene = new Scene(root, stage.getWidth(), stage.getHeight());

            // Set new Scene
            stage.setScene(gameScene);
            stage.setTitle("Cờ Tướng");
        }
        catch(Exception e){

        }

    }
}