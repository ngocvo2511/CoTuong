package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Difficulty;
import com.example.cotuong.network.ServerDiscoveryClient;
import com.example.cotuong.session.ClientSession;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

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
    private StackPane historyPane;
    private HistoryController historyController;
    private StackPane loadPane;
    private LoadController loadController;
    private StackPane instructionsPane;
    private InstructionsController instructionsController;
    private StackPane settingsPane;
    private SettingsController settingsController;

    private int selectedTime = 10; // Giá trị mặc định
    private boolean isPlayerFirst = true; // Mặc định người chơi đi trước

    public void initialize() {
        Font font = Font.loadFont(getClass().getResourceAsStream("/com/example/cotuong/font/0226-LNTH-Daybreaker.ttf"), 10);

        instructionsButton.setOnAction(e -> handleInstructionsButton());
        settingsButton.setOnAction(e -> handleSettingsButton());
        historyButton.setOnAction(e -> handleHistoryButton());
        loadButton.setOnAction(e -> handleLoadButton());
        playButton.setOnAction(e -> handlePlayButton());

        loadModeSelectionOverlay();
        loadHistoryOverlay();
        loadLoadOverlay();
        loadInstructionsOverlay();
        loadSettingsOverlay();

        ClientSession session = ClientSession.getInstance();
        if (session.getClientId() == null) {
            String clientId = UUID.randomUUID().toString();
            session.setClientId(clientId);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainMenuScene(Scene scene) {
        this.mainMenuScene = scene;
    }

    public void setSelectedTime(int time) {
        this.selectedTime = time;
    }

    public void setPlayerFirst(boolean isPlayerFirst) {
        this.isPlayerFirst = isPlayerFirst;
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
        Sounds.playButtonClickSound();
        showInstructions();
    }

    @FXML
    private void handleSettingsButton() {
        Sounds.playButtonClickSound();
        showSettings();
    }

    @FXML
    public void handlePlayButton() {
        Sounds.playButtonClickSound();
        showModeSelection();
    }

    @FXML
    private void handleHistoryButton() {
        Sounds.playButtonClickSound();
        showHistory();
    }

    @FXML
    private void handleLoadButton() {
        Sounds.playButtonClickSound();
        showLoad();
    }

    private void loadModeSelectionOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/mode_selection.fxml"));
            modeSelectionPane = loader.load();
            modeSelectionController = loader.getController();
            modeSelectionController.setMainMenuController(this);
            modeSelectionPane.setVisible(false);
            StackPane centerPane = (StackPane) mainMenu.getCenter();
            centerPane.getChildren().add(modeSelectionPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải lớp phủ chọn chế độ: " + e.getMessage());
        }
    }

    private void loadHistoryOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/history.fxml"));
            historyPane = loader.load();
            historyController = loader.getController();
            historyController.setMainMenuController(this);
            historyPane.setVisible(false);
            StackPane centerPane = (StackPane) mainMenu.getCenter();
            centerPane.getChildren().add(historyPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải lớp phủ lịch sử: " + e.getMessage());
        }
    }

    private void loadLoadOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/load.fxml"));
            loadPane = loader.load();
            loadController = loader.getController();
            loadController.setMainMenuController(this);
            loadPane.setVisible(false);
            StackPane centerPane = (StackPane) mainMenu.getCenter();
            centerPane.getChildren().add(loadPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải lớp phủ tải trận: " + e.getMessage());
        }
    }

    private void loadInstructionsOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/instructions.fxml"));
            instructionsPane = loader.load();
            instructionsController = loader.getController();
            instructionsController.setMainMenuController(this);
            instructionsPane.setVisible(false);
            StackPane centerPane = (StackPane) mainMenu.getCenter();
            centerPane.getChildren().add(instructionsPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải lớp phủ hướng dẫn: " + e.getMessage());
        }
    }

    private void loadSettingsOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/settings.fxml"));
            settingsPane = loader.load();
            settingsController = loader.getController();
            settingsController.setMainMenuController(this);
            settingsPane.setVisible(false);
            StackPane centerPane = (StackPane) mainMenu.getCenter();
            centerPane.getChildren().add(settingsPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải lớp phủ cài đặt: " + e.getMessage());
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

    public void showHistory() {
        if (historyPane != null) {
            historyPane.setVisible(true);
        }
    }

    public void hideHistory() {
        if (historyPane != null) {
            historyPane.setVisible(false);
        }
    }

    public void showLoad() {
        if (loadPane != null) {
            loadPane.setVisible(true);
        }
    }

    public void hideLoad() {
        if (loadPane != null) {
            loadPane.setVisible(false);
        }
    }

    public void showInstructions() {
        if (instructionsPane != null) {
            instructionsPane.setVisible(true);
        }
    }

    public void hideInstructions() {
        if (instructionsPane != null) {
            instructionsPane.setVisible(false);
        }
    }

    public void showSettings() {
        if (settingsPane != null) {
            settingsController.refreshUIFromSettings();
            settingsPane.setVisible(true);
        }
    }

    public void hideSettings() {
        if (settingsPane != null) {
            settingsPane.setVisible(false);
        }
    }

    public void startGame(boolean isAI, Difficulty difficulty) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/OfflineGameScreen.fxml"));
            Scene gameScene = new Scene(loader.load());
            OfflineGameController controller = loader.getController();

            controller.initialize(difficulty, isAI);

            hideModeSelection();
            hideSettings();

            stage.setScene(gameScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi khởi động game: " + e.getMessage());
        }
    }
}