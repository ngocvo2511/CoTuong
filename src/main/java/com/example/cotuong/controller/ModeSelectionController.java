package com.example.cotuong.controller;

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
        // Load the difficulty selection overlay
        loadDifficultySelectionOverlay();

        // Load the online options overlay
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

            // Initially invisible
            difficultySelectionPane.setVisible(false);

            // Add to the parent StackPane
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

            // Initially invisible
            onlineOptionPane.setVisible(false);

            // Add to the parent StackPane
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
        try {
            FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
            Parent root = gameLoader.load();

            OfflineGameController gameController = gameLoader.getController();
            gameController.initialize(0, false); // vsAI = false

            Stage stage = (Stage) twoPlayerModeButton.getScene().getWindow();
            Scene gameScene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(gameScene);
            stage.setTitle("Cờ Tướng");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải màn hình game: " + e.getMessage());
        }
    }

    @FXML
    private void handleOnlineMode() {
        System.out.println("Online mode selected");
        showOnlineOption();
    }

    @FXML
    private void handleBackButton() {
        // Close this overlay to return to main menu
        if (mainMenuController != null) {
            mainMenuController.hideModeSelection();
        }
    }

    public void showDifficultySelection() {
        if (difficultySelectionPane != null) {
            // Hide other controls
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != difficultySelectionPane && child != onlineOptionPane)
                {
                    child.setVisible(false);
                }
            }
            // Hide other overlays
            if (onlineOptionPane != null) {
                onlineOptionPane.setVisible(false);
            }

            // Show difficulty selection
            difficultySelectionPane.setVisible(true);
        }
    }

    public void showOnlineOption() {
        if (onlineOptionPane != null) {
            // Hide other controls
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != onlineOptionPane && child != difficultySelectionPane) {
                    child.setVisible(false);
                }
            }
            // Hide other overlays
            if (difficultySelectionPane != null) {
                difficultySelectionPane.setVisible(false);
            }

            // Show online options
            onlineOptionPane.setVisible(true);
        }
    }





    public void hideDifficultySelection() {
        if (difficultySelectionPane != null) {
            difficultySelectionPane.setVisible(false);
            // Show mode selection controls again
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != difficultySelectionPane && child != onlineOptionPane ) {
                    child.setVisible(true);
                }
            }
        }
    }

    public void hideOnlineOption() {
        if (onlineOptionPane != null) {
            onlineOptionPane.setVisible(false);
            // Show mode selection controls again
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != onlineOptionPane && child != difficultySelectionPane ) {
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
}