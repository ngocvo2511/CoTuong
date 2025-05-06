package com.example.cotuong.controller;

import com.example.cotuong.network.LobbyManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class JoinRoomController {
    @FXML
    private StackPane joinRoomPane;

    @FXML
    private TextField roomNameField;

    @FXML
    private TextField playerNameField;

    @FXML
    private ComboBox<String> timeSelectionComboBox;



    @FXML
    private Button joinButton;

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

        // Initially validate form
        validateForm();
    }

    public void setOnlineOptionsController(OnlineOptionsController controller) {
        this.onlineOptionsController = controller;
    }



    @FXML
    private void handleJoinRoom() {
        if (!validateForm()) {
            return;
        }

        String roomName = roomNameField.getText().trim();
        String playerName = playerNameField.getText().trim();
        String timeSelection = timeSelectionComboBox.getValue();

        LobbyManager.getInstance().connectClient();
        LobbyManager.getInstance().getClient().joinRoom(roomName, playerName);


        // Close this overlay and navigate to waiting room or directly to game
//        if (onlineOptionsController != null) {
//            onlineOptionsController.handleRoomJoined(roomName, playerName, timeSelection);
//        }
    }

    @FXML
    private void handleCancel() {
        // Close this overlay and return to online options
        if (onlineOptionsController != null) {
            onlineOptionsController.hideJoinRoomOverlay();
        }
    }

    private boolean validateForm() {
        boolean isValid = !roomNameField.getText().trim().isEmpty() &&
                !playerNameField.getText().trim().isEmpty() &&
                timeSelectionComboBox.getValue() != null;

        joinButton.setDisable(!isValid);
        return isValid;
    }

    // Method to pre-fill the form if needed
    public void setRoomData(String roomName, String playerName, String time, String team) {
        if (roomName != null) roomNameField.setText(roomName);
        if (playerName != null) playerNameField.setText(playerName);
        if (time != null) timeSelectionComboBox.setValue(time);
    }
}