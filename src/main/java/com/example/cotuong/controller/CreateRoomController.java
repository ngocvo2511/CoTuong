package com.example.cotuong.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class CreateRoomController {
    @FXML
    private StackPane createRoomPane;

    @FXML
    private TextField roomNameField;

    @FXML
    private TextField playerNameField;

    @FXML
    private ComboBox<String> timeSelectionComboBox;

    @FXML
    private Button redTeamButton;

    @FXML
    private Button blackTeamButton;

    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    private OnlineOptionsController onlineOptionsController;
    private String selectedTeam = "red"; // Default to red team

    public void initialize() {
        // Initialize time options
        timeSelectionComboBox.setItems(FXCollections.observableArrayList(
                "5 phút", "10 phút", "15 phút", "20 phút", "30 phút"
        ));
        timeSelectionComboBox.setValue("10 phút"); // Default selection

        // Set default selection for red team
        redTeamButton.getStyleClass().add("selected");

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
    private void handleSelectRedTeam() {
        selectedTeam = "red";
        redTeamButton.getStyleClass().add("selected");
        blackTeamButton.getStyleClass().remove("selected");
    }

    @FXML
    private void handleSelectBlackTeam() {
        selectedTeam = "black";
        blackTeamButton.getStyleClass().add("selected");
        redTeamButton.getStyleClass().remove("selected");
    }

    @FXML
    private void handleCreateRoom() {
        if (!validateForm()) {
            return;
        }

        String roomName = roomNameField.getText().trim();
        String playerName = playerNameField.getText().trim();
        String timeSelection = timeSelectionComboBox.getValue();

        System.out.println("Creating room with following details:");
        System.out.println("Room Name: " + roomName);
        System.out.println("Player Name: " + playerName);
        System.out.println("Time: " + timeSelection);
        System.out.println("Team: " + selectedTeam);

        // TODO: Implement actual room creation with server communication

        // Close this overlay and navigate to waiting room or directly to game
        if (onlineOptionsController != null) {
            onlineOptionsController.handleRoomCreated(roomName, playerName, timeSelection, selectedTeam);
        }
    }

    @FXML
    private void handleCancel() {
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
        if (team != null) {
            if (team.equals("black")) {
                handleSelectBlackTeam();
            } else {
                handleSelectRedTeam();
            }
        }
    }
}