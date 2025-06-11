package com.example.cotuong.controller;

import com.example.cotuong.network.LobbyManager;
import com.example.cotuong.network.ErrorNotificationHandler;
import com.example.cotuong.utils.Sounds;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.FadeTransition;

import java.io.IOException;

public class FindRandomMatchController implements ErrorNotificationHandler {
    @FXML
    private StackPane findRandomMatchPane;

    @FXML
    public TextField ipField;

    @FXML
    private TextField playerNameField;

    @FXML
    private ComboBox<String> timeSelectionComboBox;

    @FXML
    private Button findMatchButton;

    @FXML
    private Button cancelButton;

    @FXML private HBox errorNotificationPanel;
    @FXML private Text errorMessageText;
    private Timeline hideNotificationTimer;

    private OnlineOptionsController onlineOptionsController;
    private StackPane waitingMatchPane;
    private WaitingMatchController waitingMatchController;

    public void initialize() {
        // Initialize time options
        timeSelectionComboBox.setItems(FXCollections.observableArrayList(
                "5 phút", "10 phút", "15 phút", "20 phút", "30 phút"
        ));
        timeSelectionComboBox.setValue("10 phút"); // Default selection

        // Add validation listeners
        playerNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        timeSelectionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());

        // Initially validate form
        validateForm();

        // Load waiting match overlay
        loadWaitingMatchOverlay();

        // Set this controller as the error handler
        LobbyManager.getInstance().setErrorHandler(this);
    }

    @Override
    public void showErrorNotification(String message) {
        errorMessageText.setText(message);
        errorNotificationPanel.setVisible(true);
        errorNotificationPanel.setManaged(true);

        // Cancel any existing timer
        if (hideNotificationTimer != null) {
            hideNotificationTimer.stop();
        }

        // Create a new timer to hide the notification after 3 seconds
        hideNotificationTimer = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            errorNotificationPanel.setVisible(false);
            errorNotificationPanel.setManaged(false);
        }));
        hideNotificationTimer.play();
    }

    private void loadWaitingMatchOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cotuong/fxml/waiting_match_overlay.fxml"));
            waitingMatchPane = (StackPane) loader.load();
            waitingMatchController = loader.getController();
            waitingMatchController.setFindRandomMatchController(this);

            // Initially invisible
            waitingMatchPane.setVisible(false);

            // Add to the parent StackPane
            findRandomMatchPane.getChildren().add(waitingMatchPane);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading waiting match overlay: " + e.getMessage());
        }
    }

    public void setOnlineOptionsController(OnlineOptionsController controller) {
        this.onlineOptionsController = controller;
    }

    @FXML
    private void handleFindMatch() {
        Sounds.playButtonClickSound();
        if (!validateForm()) {
            return;
        }

        String playerName = playerNameField.getText().trim();
        String timeSelection = timeSelectionComboBox.getValue();
        String time = timeSelection.replaceAll("[^\\d]", "");
        
        try {
            if(!ipField.isDisable()){
                String ip = ipField.getText().trim();
                onlineOptionsController.setServerIp(ip);
            }
            LobbyManager.getInstance().connectClient();
            // Set this controller as the error handler
            LobbyManager.getInstance().setErrorHandler(this);
            
            // Gửi yêu cầu tìm trận đến server
            LobbyManager.getInstance().getClient().findRandomMatch(playerName, Integer.parseInt(time));

            // Hiển thị overlay chờ tìm trận
            showWaitingMatchOverlay(playerName, timeSelection);
        } catch (Exception e) {
            showErrorNotification("Không thể kết nối đến server. Vui lòng kiểm tra lại địa chỉ IP.");
        }
    }

    private void showWaitingMatchOverlay(String playerName, String timeSelection) {
        if (waitingMatchPane != null && waitingMatchController != null) {
            // Cập nhật thông tin chờ
            waitingMatchController.setupWaitingInfo(playerName, timeSelection);

            // Ẩn các controls của màn hình tìm trận
            for (Node node : findRandomMatchPane.getChildren()) {
                if (node != waitingMatchPane) {
                    node.setVisible(false);
                }
            }

            // Hiển thị overlay chờ tìm trận
            waitingMatchPane.setVisible(true);
        }
    }

    public void hideWaitingMatchOverlay() {
        if (waitingMatchPane != null) {
            waitingMatchPane.setVisible(false);

            // Hiển thị lại các controls của màn hình tìm trận
            for (Node node : findRandomMatchPane.getChildren()) {
                if (node != waitingMatchPane) {
                    node.setVisible(true);
                }
            }
        }
    }

    @FXML
    private void handleCancel() {
        Sounds.playButtonClickSound();
        // Close this overlay and return to online options
        if (onlineOptionsController != null) {
            onlineOptionsController.hideFindRandomMatchOverlay();
        }
    }

    private boolean validateForm() {
        boolean isValid = !ipField.getText().trim().isEmpty() && !playerNameField.getText().trim().isEmpty() &&
                timeSelectionComboBox.getValue() != null;

        findMatchButton.setDisable(!isValid);
        return isValid;
    }

    public void setPlayerData(String playerName, String time) {
        if (playerName != null) playerNameField.setText(playerName);
        if (time != null) timeSelectionComboBox.setValue(time);
    }

    public void setServerIp(String serverIp) {
        if(serverIp == null){
            serverIp = "";
        }
        ipField.setText(serverIp);
        ipField.setDisable(!serverIp.isEmpty());
    }
}