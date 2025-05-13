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
    private StackPane settingsPane;
    private SettingsOffline2PlayersController settingsController;

    public void initialize() {
        // Load the difficulty selection overlay
        loadDifficultySelectionOverlay();

        // Load the online options overlay
        loadOnlineOptionOverlay();

        // Load the settings overlay
        loadSettingsOverlay();
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

    private void loadSettingsOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/settings_offline_2players.fxml"));
            settingsPane = (StackPane) loader.load();
            settingsController = loader.getController();
            settingsController.setModeSelectionController(this);

            // Initially invisible
            settingsPane.setVisible(false);

            // Add to the parent StackPane
            modeSelectionPane.getChildren().add(settingsPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading settings overlay: " + e.getMessage());
        }
    }

    @FXML
    private void handleComputerMode() {
        System.out.println("Computer mode selected");
        showDifficultySelection();
    }

    @FXML
    private void handleTwoPlayerMode() {
        showSettingsOverlay();
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
                if (child != difficultySelectionPane && child != onlineOptionPane && child != settingsPane) {
                    child.setVisible(false);
                }
            }
            // Hide other overlays
            if (onlineOptionPane != null) {
                onlineOptionPane.setVisible(false);
            }
            if (settingsPane != null) {
                settingsPane.setVisible(false);
            }
            // Show difficulty selection
            difficultySelectionPane.setVisible(true);
        }
    }

    public void showOnlineOption() {
        if (onlineOptionPane != null) {
            // Hide other controls
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != onlineOptionPane && child != difficultySelectionPane && child != settingsPane) {
                    child.setVisible(false);
                }
            }
            // Hide other overlays
            if (difficultySelectionPane != null) {
                difficultySelectionPane.setVisible(false);
            }
            if (settingsPane != null) {
                settingsPane.setVisible(false);
            }
            // Show online options
            onlineOptionPane.setVisible(true);
        }
    }

    public void showSettingsOverlay() {
        if (settingsPane != null) {
            // Hide other controls
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != settingsPane && child != difficultySelectionPane && child != onlineOptionPane) {
                    child.setVisible(false);
                }
            }
            // Hide other overlays
            if (difficultySelectionPane != null) {
                difficultySelectionPane.setVisible(false);
            }
            if (onlineOptionPane != null) {
                onlineOptionPane.setVisible(false);
            }
            // Show settings overlay
            settingsPane.setVisible(true);
        }
    }

    public void hideSettingsOverlay() {
        if (settingsPane != null) {
            settingsPane.setVisible(false);
            // Show mode selection controls again
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != settingsPane && child != difficultySelectionPane && child != onlineOptionPane) {
                    child.setVisible(true);
                }
            }
            // Proceed to game if confirmed
            if (settingsController != null && settingsController.isConfirmed()) {
                int selectedTimeMinutes = settingsController.getSelectedTimeMinutes();
                double selectedVolume = settingsController.getSelectedVolume();
                if (selectedTimeMinutes > 0) {
                    try {
                        // Proceed to OfflineGameScreen
                        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
                        Parent root = gameLoader.load();

                        OfflineGameController gameController = gameLoader.getController();
                        gameController.initialize(0, false); // vsAI = false
                        // Pass selected time and volume (assuming methods exist)
                        // gameController.setMatchTime(selectedTimeMinutes * 60);
                        // gameController.setVolume(selectedVolume);

                        // Get current Stage
                        Stage stage = (Stage) ((Node) twoPlayerModeButton).getScene().getWindow();

                        // Create Scene with current Stage size
                        Scene gameScene = new Scene(root, stage.getWidth(), stage.getHeight());

                        // Set new Scene
                        stage.setScene(gameScene);
                        stage.setTitle("Cờ Tướng");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Error loading game screen: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void hideDifficultySelection() {
        if (difficultySelectionPane != null) {
            difficultySelectionPane.setVisible(false);
            // Show mode selection controls again
            for (Node child : modeSelectionPane.getChildren()) {
                if (child != difficultySelectionPane && child != onlineOptionPane && child != settingsPane) {
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
                if (child != onlineOptionPane && child != difficultySelectionPane && child != settingsPane) {
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
        if (settingsPane != null) {
            settingsPane.setVisible(false);
        }
        if (mainMenuController != null) {
            mainMenuController.hideModeSelection();
        }
    }
}