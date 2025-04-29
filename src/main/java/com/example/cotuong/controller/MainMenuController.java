package com.example.cotuong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private BorderPane mainMenu;
//    @FXML
//    private HBox titleBar;
//    @FXML
//    private Button minimizeButton;
//    @FXML
//    private Button maximizeButton;
//    @FXML
//    private Button closeButton;
    @FXML
    private Button instructionsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button playButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button downloadButton;

    private Stage stage;
    private Scene mainMenuScene;
    private double xOffset = 0;
    private double yOffset = 0;

    private StackPane modeSelectionPane;
    private ModeSelectionController modeSelectionController;

    public void initialize() {
        // Make the stage draggable
//        titleBar.setOnMousePressed(this::handleMousePressed);
//        titleBar.setOnMouseDragged(this::handleMouseDragged);

        // Window control buttons
//        minimizeButton.setOnAction(e -> stage.setIconified(true));
//        maximizeButton.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
//        closeButton.setOnAction(e -> stage.close());
        Font font = Font.loadFont(getClass().getResourceAsStream("@../font/0226-LNTH-Daybreaker.ttf"), 10);

        // Set up event handlers for other buttons
        instructionsButton.setOnAction(e -> handleInstructionsButton());
        settingsButton.setOnAction(e -> handleSettingsButton());
        historyButton.setOnAction(e -> handleHistoryButton());
        downloadButton.setOnAction(e -> handleDownloadButton());

        // Load the mode selection overlay
        loadModeSelectionOverlay();

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainMenuScene(Scene scene) {
        this.mainMenuScene = scene;
    }

    private void handleMousePressed(MouseEvent event) {
        xOffset = stage.getX() - event.getScreenX();
        yOffset = stage.getY() - event.getScreenY();
    }

    private void handleMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }

    @FXML
    private void handleInstructionsButton() {
        System.out.println("Instructions button clicked");
    }

    @FXML
    private void handleSettingsButton() {
        System.out.println("Settings button clicked");
    }

    @FXML
    public void handlePlayButton() {
        showModeSelection();
    }

    @FXML
    private void handleHistoryButton() {
        System.out.println("History button clicked");
    }

    @FXML
    private void handleDownloadButton() {
        System.out.println("Download button clicked");
    }

    private void loadModeSelectionOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/mode_selection.fxml"));
            modeSelectionPane = (StackPane) loader.load();
            modeSelectionController = loader.getController();
            modeSelectionController.setMainMenuController(this);

            // Initially invisible
            modeSelectionPane.setVisible(false);

            // Add to the main BorderPane as an overlay
            StackPane centerPane = (StackPane) mainMenu.getCenter();
            centerPane.getChildren().add(modeSelectionPane);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading mode selection overlay: " + e.getMessage());
        }
    }

    public void showModeSelection() {
        if (modeSelectionPane != null) {
            modeSelectionPane.setVisible(true);
        }
    }

    public void hideModeSelection() {
        if (modeSelectionPane != null) {
            modeSelectionPane.setVisible(false);
        }
    }
}