package com.example.cotuong.controller;

import com.example.cotuong.network.LobbyManager;
import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class WaitingMatchController {
    @FXML
    private StackPane waitingMatchPane;

    @FXML
    private Label playerInfoLabel;

    @FXML
    private Label timeInfoLabel;

    @FXML
    private Button cancelSearchButton;

    private FindRandomMatchController findRandomMatchController;
    private String playerName;
    private String timeSelection;

    public void initialize() {
        // Initialize default values
    }

    public void setFindRandomMatchController(FindRandomMatchController controller) {
        this.findRandomMatchController = controller;
    }

    public void setupWaitingInfo(String playerName, String timeSelection) {
        this.playerName = playerName;
        this.timeSelection = timeSelection;

        // Update labels with player info
        playerInfoLabel.setText("Người chơi: " + playerName);
        timeInfoLabel.setText("Thời gian: " + timeSelection);
    }

    @FXML
    private void handleCancelSearch() {
        Sounds.playButtonClickSound();
        // Cancel the search operation
        LobbyManager.getInstance().getClient().cancelRandomMatchSearch(playerName);

        // Hide this overlay and return to find match screen
        if (findRandomMatchController != null) {
            findRandomMatchController.hideWaitingMatchOverlay();
        }
    }
}