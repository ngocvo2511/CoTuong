package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        System.out.println("Two player mode selected");
        // TODO: Implement two-player mode game start
        // For now, just hide the mode selection overlay
        hideAllOverlays();

        // TODO: Start two-player game
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
            // Hide the mode selection controls but keep the overlay active
            for (int i = 0; i < modeSelectionPane.getChildren().size(); i++) {
                if (modeSelectionPane.getChildren().get(i) != difficultySelectionPane &&
                        modeSelectionPane.getChildren().get(i) != onlineOptionPane) {
                    modeSelectionPane.getChildren().get(i).setVisible(false);
                }
            }

            // Ensure online option is hidden
            if (onlineOptionPane != null) {
                onlineOptionPane.setVisible(false);
            }

            // Show the difficulty selection
            difficultySelectionPane.setVisible(true);
        }
    }

    public void showOnlineOption() {
        if (onlineOptionPane != null) {
            // Hide the mode selection controls but keep the overlay active
            for (int i = 0; i < modeSelectionPane.getChildren().size(); i++) {
                if (modeSelectionPane.getChildren().get(i) != onlineOptionPane &&
                        modeSelectionPane.getChildren().get(i) != difficultySelectionPane) {
                    modeSelectionPane.getChildren().get(i).setVisible(false);
                }
            }

            // Ensure difficulty selection is hidden
            if (difficultySelectionPane != null) {
                difficultySelectionPane.setVisible(false);
            }

            // Show the online options
            onlineOptionPane.setVisible(true);
        }
    }

    public void hideDifficultySelection() {
        if (difficultySelectionPane != null) {
            difficultySelectionPane.setVisible(false);

            // Show the mode selection controls again
            for (int i = 0; i < modeSelectionPane.getChildren().size(); i++) {
                if (modeSelectionPane.getChildren().get(i) != difficultySelectionPane &&
                        modeSelectionPane.getChildren().get(i) != onlineOptionPane) {
                    modeSelectionPane.getChildren().get(i).setVisible(true);
                }
            }
        }
    }

    public void hideOnlineOption() {
        if (onlineOptionPane != null) {
            onlineOptionPane.setVisible(false);

            // Show the mode selection controls again
            for (int i = 0; i < modeSelectionPane.getChildren().size(); i++) {
                if (modeSelectionPane.getChildren().get(i) != onlineOptionPane &&
                        modeSelectionPane.getChildren().get(i) != difficultySelectionPane) {
                    modeSelectionPane.getChildren().get(i).setVisible(true);
                }
            }
        }
    }

    public void hideAllOverlays() {
        // Hide difficulty selection if visible
        if (difficultySelectionPane != null) {
            difficultySelectionPane.setVisible(false);
        }

        // Hide online options if visible
        if (onlineOptionPane != null) {
            onlineOptionPane.setVisible(false);
        }

        // Tell main menu controller to hide mode selection
        if (mainMenuController != null) {
            mainMenuController.hideModeSelection();
        }
    }
}