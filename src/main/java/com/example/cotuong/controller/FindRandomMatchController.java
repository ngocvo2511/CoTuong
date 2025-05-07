package com.example.cotuong.controller;

import com.example.cotuong.network.LobbyManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

import java.io.IOException;

public class FindRandomMatchController {
    @FXML
    private StackPane findRandomMatchPane;

    @FXML
    private TextField playerNameField;

    @FXML
    private ComboBox<String> timeSelectionComboBox;

    @FXML
    private Button findMatchButton;

    @FXML
    private Button cancelButton;

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
        if (!validateForm()) {
            return;
        }

        String playerName = playerNameField.getText().trim();
        String timeSelection = timeSelectionComboBox.getValue();
        String time = timeSelection.replaceAll("[^\\d]", "");

        LobbyManager.getInstance().connectClient();
        // Gửi yêu cầu tìm trận đến server
        LobbyManager.getInstance().getClient().findRandomMatch(playerName, Integer.parseInt(time));

        // Hiển thị overlay chờ tìm trận
        showWaitingMatchOverlay(playerName, timeSelection);
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
        // Close this overlay and return to online options
        if (onlineOptionsController != null) {
            onlineOptionsController.hideFindRandomMatchOverlay();
        }
    }

    private boolean validateForm() {
        boolean isValid = !playerNameField.getText().trim().isEmpty() &&
                timeSelectionComboBox.getValue() != null;

        findMatchButton.setDisable(!isValid);
        return isValid;
    }

    // Method to pre-fill the form if needed
    public void setPlayerData(String playerName, String time) {
        if (playerName != null) playerNameField.setText(playerName);
        if (time != null) timeSelectionComboBox.setValue(time);
    }
}