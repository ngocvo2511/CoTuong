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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private BorderPane mainMenu;
    @FXML
    private Button instructionsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button playButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button loadButton;

    private Stage stage;
    private Scene mainMenuScene;
    private double xOffset = 0;
    private double yOffset = 0;

    private StackPane modeSelectionPane;
    private ModeSelectionController modeSelectionController;

    public void initialize() {
        Font font = Font.loadFont(getClass().getResourceAsStream("/com/example/cotuong/font/0226-LNTH-Daybreaker.ttf"), 10);

        // Set up event handlers for other buttons
        instructionsButton.setOnAction(e -> handleInstructionsButton());
        settingsButton.setOnAction(e -> handleSettingsButton());
        historyButton.setOnAction(e -> handleHistoryButton());
        loadButton.setOnAction(e -> handleLoadButton());

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/instructions.fxml"));
            VBox instructionsPane = loader.load();
            InstructionsController instructionsController = loader.getController();

            Stage instructionsStage = new Stage();
            instructionsStage.setTitle("Hướng Dẫn Chơi");
            instructionsStage.setScene(new Scene(instructionsPane, 1000, 700));
            instructionsController.setStage(instructionsStage);

            instructionsStage.initModality(Modality.APPLICATION_MODAL);
            instructionsStage.initOwner(stage);

            instructionsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading instructions window: " + e.getMessage());
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/history.fxml"));
            VBox historyPane = loader.load();
            HistoryController historyController = loader.getController();

            Stage historyStage = new Stage();
            historyStage.setTitle("Lịch Sử Đấu");
            historyStage.setScene(new Scene(historyPane, 700, 500));
            historyController.setStage(historyStage);

            historyStage.initModality(Modality.APPLICATION_MODAL);
            historyStage.initOwner(stage);

            historyStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading history window: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoadButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/load.fxml"));
            VBox loadPane = loader.load();
            LoadController loadController = loader.getController();

            Stage loadStage = new Stage();
            loadStage.setTitle("Tải Trận");
            loadStage.setScene(new Scene(loadPane, 700, 500));
            loadController.setStage(loadStage);

            loadStage.initModality(Modality.APPLICATION_MODAL);
            loadStage.initOwner(stage);

            loadStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading load window: " + e.getMessage());
        }
    }

    private void loadModeSelectionOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/mode_selection.fxml"));
            modeSelectionPane = (StackPane) loader.load();
            modeSelectionController = loader.getController();
            modeSelectionController.setMainMenuController(this);

            modeSelectionPane.setVisible(false);

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