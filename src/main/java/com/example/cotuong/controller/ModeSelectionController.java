package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Difficulty;
import com.example.cotuong.session.ClientSession;
import com.example.cotuong.utils.Sounds;
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
    private Runnable onCancel;
    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    private MainMenuController mainMenuController;
    private OfflineGameController offlineGameController;
    private OnlineGameController onlineGameController;

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
        Sounds.playButtonClickSound();
        System.out.println("Computer mode selected");
        showDifficultySelection();
    }

    @FXML
    private void handleTwoPlayerMode() {
        try{
            Sounds.playButtonClickSound();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
            Parent root = loader.load();
            OfflineGameController controller = loader.getController();
            controller.initialize(Difficulty.NONE,false);

            Stage stage = (Stage) ((Node) modeSelectionPane).getScene().getWindow();
            Scene gameScene = new Scene(root, stage.getWidth(), stage.getHeight());

            // Set new Scene
            stage.setScene(gameScene);
            stage.setTitle("Cờ Tướng");

        }
        catch(Exception e){

        }
    }

    @FXML
    private void handleOnlineMode() {
        Sounds.playButtonClickSound();
        System.out.println("Online mode selected");
        showOnlineOption();
    }

    @FXML
    private void handleBackButton() {
        Sounds.playButtonClickSound();
        if(onCancel!=null){
            onCancel.run();
            return;
        }
        if (mainMenuController != null) {
            mainMenuController.hideModeSelection();
        }
        if(onlineGameController!=null){

        }
        if(offlineGameController!=null){
            offlineGameController.closeOverlay();
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
    public void setOfflineGameController(OfflineGameController offlineGameController){
        this.offlineGameController = offlineGameController;
    }
    public void setOnlineGameController(OnlineGameController onlineGameController){
        this.onlineGameController = onlineGameController;
    }
}