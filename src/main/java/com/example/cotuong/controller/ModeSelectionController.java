package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Difficulty;
import com.example.cotuong.session.ClientSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ModeSelectionController {
    @FXML
    private StackPane modeSelectionPane;
    @FXML
    private Button computerModeButton;
    @FXML
    private Button twoPlayerModeButton;
    @FXML
    private Button onlineModeButton;
    @FXML
    private Button backButton;

    private MainMenuController mainMenuController;
    private StackPane difficultySelectionPane;
    private DifficultySelectionController difficultySelectionController;
    private StackPane onlineOptionPane;
    private OnlineOptionsController onlineOptionController;

    public void initialize() {
        loadDifficultySelectionOverlay();
        loadOnlineOptionOverlay();
    }

    public void setMainMenuController(MainMenuController controller) {
        this.mainMenuController = controller;
    }

    private void loadDifficultySelectionOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/difficulty_selection.fxml"));
            difficultySelectionPane = (StackPane) loader.load();
            difficultySelectionController = loader.getController();
            difficultySelectionController.setModeSelectionController(this);
            difficultySelectionPane.setVisible(false);
            modeSelectionPane.getChildren().add(difficultySelectionPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading difficulty selection overlay: " + e.getMessage());
        }
    }

    private void loadOnlineOptionOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/online_option.fxml"));
            onlineOptionPane = (StackPane) loader.load();
            onlineOptionController = loader.getController();
            onlineOptionController.setModeSelectionController(this);
            onlineOptionPane.setVisible(false);
            modeSelectionPane.getChildren().add(onlineOptionPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading online option overlay: " + e.getMessage());
        }
    }

    @FXML
    private void handleComputerMode() {
        System.out.println("Computer mode selected");
        showDifficultySelection();
    }

    @FXML
    private void handleTwoPlayerMode() {
        if (mainMenuController != null) {
            mainMenuController.startGame(false, Difficulty.NONE); // Chế độ 2 người chơi, không cần độ khó
        }
    }

    @FXML
    private void handleOnlineMode() {
        System.out.println("Online mode selected");
        showOnlineOption();
    }

    @FXML
    private void handleBackButton() {
        if (mainMenuController != null) {
            mainMenuController.hideModeSelection();
        }
    }

    public void showDifficultySelection() {
        if (difficultySelectionPane != null) {
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != difficultySelectionPane && child != onlineOptionPane) {
                    child.setVisible(false);
                }
            }
            if (onlineOptionPane != null) {
                onlineOptionPane.setVisible(false);
            }
            difficultySelectionPane.setVisible(true);
        }
    }

    public void showOnlineOption() {
        if (onlineOptionPane != null) {
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != onlineOptionPane && child != difficultySelectionPane) {
                    child.setVisible(false);
                }
            }
            if (difficultySelectionPane != null) {
                difficultySelectionPane.setVisible(false);
            }
            onlineOptionPane.setVisible(true);
        }
    }

    public void hideDifficultySelection() {
        if (difficultySelectionPane != null) {
            difficultySelectionPane.setVisible(false);
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != difficultySelectionPane && child != onlineOptionPane) {
                    child.setVisible(true);
                }
            }
        }
    }

    public void hideOnlineOption() {
        if (onlineOptionPane != null) {
            onlineOptionPane.setVisible(false);
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != onlineOptionPane && child != difficultySelectionPane) {
                    child.setVisible(true);
                }
            }
        }
    }

    public void hideAllOverlays() {
        if (difficultySelectionPane != null) {
            difficultySelectionPane.setVisible(false);
        }
        if (onlineOptionPane != null) {
            onlineOptionPane.setVisible(false);
        }
        if (mainMenuController != null) {
            mainMenuController.hideModeSelection();
        }
    }

    // Thêm phương thức để DifficultySelectionController gọi
    public void startComputerGame(Difficulty difficulty) {
        if (mainMenuController != null) {
            mainMenuController.startGame(true, difficulty); // Chế độ AI với độ khó
        }
    }
}