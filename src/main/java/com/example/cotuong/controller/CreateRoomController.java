package com.example.cotuong.controller;

import com.example.cotuong.network.ErrorNotificationHandler;
import com.example.cotuong.network.LobbyManager;
import com.example.cotuong.utils.Sounds;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CreateRoomController implements ErrorNotificationHandler {
    @FXML
    private StackPane createRoomPane;

    @FXML
    public TextField ipField;

    @FXML
    private TextField roomNameField;

    @FXML
    private TextField playerNameField;

    @FXML
    private ComboBox<String> timeSelectionComboBox;

    @FXML private HBox errorNotificationPanel;
    @FXML private Text errorMessageText;
    private Timeline hideNotificationTimer;
    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    private OnlineOptionsController onlineOptionsController;

    public void initialize() {
        // Initialize time options
        timeSelectionComboBox.setItems(FXCollections.observableArrayList(
                "5 phút", "10 phút", "15 phút", "20 phút", "30 phút"
        ));
        timeSelectionComboBox.setValue("10 phút"); // Default selection

        // Set default selection for red team

        // Add validation listeners
        roomNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        playerNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        timeSelectionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());

        // Set this controller as the error handler
        LobbyManager.getInstance().setErrorHandler(this);

        // Initially validate form
        validateForm();
    }

    public void setOnlineOptionsController(OnlineOptionsController controller) {
        this.onlineOptionsController = controller;
    }



    @FXML
    private void handleCreateRoom() {
        Sounds.playButtonClickSound();
        if (!validateForm()) {
            return;
        }

        String roomName = roomNameField.getText().trim();
        String playerName = playerNameField.getText().trim();
        String timeSelection = timeSelectionComboBox.getValue();
        String time = timeSelection.replaceAll("[^\\d]", "");

        LobbyManager.getInstance().connectClient();
        LobbyManager.getInstance().getClient().createRoom(roomName, playerName, Integer.parseInt(time));

        // Close this overlay and navigate to waiting room or directly to game
//        if (onlineOptionsController != null) {
//            onlineOptionsController.handleRoomCreated(roomName, playerName, timeSelection);
//        }
    }

    @FXML
    private void handleCancel() {
        Sounds.playButtonClickSound();
        // Close this overlay and return to online options
        if (onlineOptionsController != null) {
            onlineOptionsController.hideCreateRoomOverlay();
        }
    }

    private boolean validateForm() {
        boolean isValid = !roomNameField.getText().trim().isEmpty() &&
                !playerNameField.getText().trim().isEmpty() &&
                timeSelectionComboBox.getValue() != null;

        createButton.setDisable(!isValid);
        return isValid;
    }

    // Method to pre-fill the form if needed
    public void setRoomData(String roomName, String playerName, String time, String team) {
        if (roomName != null) roomNameField.setText(roomName);
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

    public void showErrorNotification(String errorMessage) {
        // Dừng timer cũ nếu có
        if (hideNotificationTimer != null) {
            hideNotificationTimer.stop();
        }

        // Set text và hiển thị panel
        errorMessageText.setText(errorMessage);
        errorNotificationPanel.setVisible(true);
        errorNotificationPanel.setManaged(true);

        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), errorNotificationPanel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Tự động ẩn sau 3 giây
        hideNotificationTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> hideErrorNotification()));
        hideNotificationTimer.play();
    }

    /**
     * Ẩn thông báo lỗi với hiệu ứng fade out
     */
    private void hideErrorNotification() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), errorNotificationPanel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            errorNotificationPanel.setVisible(false);
            errorNotificationPanel.setManaged(false);
        });
        fadeOut.play();
    }

    /**
     * Ẩn thông báo lỗi ngay lập tức (không có animation)
     */
    public void hideErrorNotificationImmediately() {
        if (hideNotificationTimer != null) {
            hideNotificationTimer.stop();
        }
        errorNotificationPanel.setVisible(false);
        errorNotificationPanel.setManaged(false);
    }

}